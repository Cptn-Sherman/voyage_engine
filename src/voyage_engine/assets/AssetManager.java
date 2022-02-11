package voyage_engine.assets;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import spool.IData;
import spool.IJsonSource;
import spool.Spool;
import voyage_engine.assets.font.Font;
import voyage_engine.assets.shader.Shader;
import voyage_engine.assets.texture.Texture;

public class AssetManager {
	public static final String MODULE_FOLDER_PATH = "data\\";
	public static final short RESERVED_GENERATED_ASSET_ID = Short.MIN_VALUE;


	private static Manifest manifest;
	private static HashMap<Integer, Asset> assetMap;

	private static LinkedList<Integer> unloadIdList;

	public static void initialize(boolean rebaseManifest) {
		// Start spool and use the default initializer to auto detect thread count.
		Spool.initialize();
		assetMap = new HashMap<Integer, Asset>();
		unloadIdList = new LinkedList<Integer>();

		System.out.println("[manifest]: loading manifest...");
		if (!rebaseManifest) { // if we are not rebasing, just attempt to load the manifest.
			manifest = loadFromJsonAllowFailure(MODULE_FOLDER_PATH + "manifest.json", Manifest.class, true);
		}
		// if the manifest failed to load or if rebase is set to true a new manifest
		// must be generated.
		if (manifest == null) {
			System.out.println("[manifest]: generating new manifest...");
			manifest = new Manifest(MODULE_FOLDER_PATH);
			manifest.compile();
		} else {
			manifest.validate();
		}
		System.out.println("[manifest]: successfully loaded!");
	}

	// check the data queue for assets that need to be uploaded to the gpu.
	public static void poll() {
		synchronized (Spool.getDataQueue()) {
			if (!Spool.getDataQueue().isEmpty()) {
				IData data = Spool.getDataQueue().remove();
				data.process();
			}
		}
		releaseUnreferencedAssets();
	}

	public static String getFilePath(String filename) {
		return manifest.getPath(filename);
	}

	// returns the requested texture by filename if it can be found in the content
	// map
	// or returns a new pointer to the texture and attempts to load the content
	// using
	// multi-threaded Loading.
	public static Texture getTexture(AssetCache cache, String filename, boolean filter, boolean mipmap) {
		int id = manifest.getID(filename);
		// if a cache was provided the id will be include in the cache list to free when
		// cache is no longer needed.
		if (cache != null) {
			cache.include(id);
		}
		Texture texture = (Texture) assetMap.get(id);
		if (texture == null) {
			texture = new Texture(manifest.getPath(filename), filter, mipmap);
			texture.setAssetID(id);
			assetMap.put(id, texture);
			Spool.addMultithreadProcess(texture);
			System.out.println("[assets]: cache miss: " + texture.toString());
		} else {
			System.out.println("[assets]: cache hit: " + texture.toString());
		}
		texture.updateReferenceCount(1);
		return texture;
	}

	// returns the requested font by filename if it can be found in the content map
	// or returns a new pointer to the font and loads the font immediately.
	public static Font getFont(AssetCache cache, String filename, int oversampling, boolean filter) {
		int id = manifest.getID(filename);
		// if a cache was provided the id will be include in the cache list to free when
		// cache is no longer needed.
		if (cache != null) {
			cache.include(id);
		}
		Font font = (Font) assetMap.get(id);
		if (font == null) {
			font = new Font(manifest.getPath(filename), oversampling, filter);
			font.setAssetID(id);
			font.load();
			assetMap.put(id, font);
			System.out.println("[assets]: cache miss: " + font.toString());
		} else {
			System.out.println("[assets]: cache hit: " + font.toString());
		}
		// this update reference also updates the reference count for the texture which
		// is attached.
		font.updateReferenceCount(1);
		return font;
	}

	// returns the requested shader file
	public static Shader getShader(AssetCache cache, String filename) {
		int id = manifest.getID(filename);
		// if a cache was provided the id will be include in the cache list to free when
		// cache is no longer needed.
		if (cache != null) {
			cache.include(id);
		}
		Shader shader = (Shader) assetMap.get(id);
		if (shader == null) {
			String filepath = manifest.getPath(filename);
			shader = loadFromJson(filepath, Shader.class, false);
			shader.setFilename(filepath);
			shader.setAssetID(id);
			shader.load();
			assetMap.put(id, shader);
			System.out.println("[assets]: cache miss: " + shader.toString());
		} else {
			System.out.println("[assets]: cache hit: " + shader.toString());
		}
		shader.updateReferenceCount(1);
		return shader;
	}

	public static void writeToJson(IJsonSource content, String filepath, boolean writeAll) {
		System.out.println("[assets]: writing json: " + filepath);
		Gson gson;
		if (writeAll) {
			gson = new GsonBuilder().setPrettyPrinting().create();
		} else {
			gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		}
		try (FileWriter writer = new FileWriter(filepath)) {
			gson.toJson(content, writer);
			writer.flush();
			writer.close();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T extends IJsonSource> T loadFromJson(String filename, Class<T> type, boolean loadAll) {
		Gson gson;
		if (loadAll) {
			gson = new GsonBuilder().setPrettyPrinting().create();
		} else {
			gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		}
		T content = null;
		try (FileReader reader = new FileReader("data\\" + filename)) {
			content = gson.fromJson(reader, type);
			reader.close();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static <T extends IJsonSource> T loadFromJsonAllowFailure(String filename, Class<T> type, boolean loadAll) {
		Gson gson;
		if (loadAll) {
			gson = new GsonBuilder().setPrettyPrinting().create();
		} else {
			gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		}
		T content = null;
		try (FileReader reader = new FileReader(filename)) {
			content = gson.fromJson(reader, type);
			reader.close();
		} catch (JsonIOException e) {
			System.out.println(
					"[assets]: Warning! issues occured reading JSON: " + filename + " however, failure was allowed.");
		} catch (IOException e) {
			System.out.println("[assets]: Warning! issues occured with IO operations: " + filename
					+ " however, failure was allowed.");
		}
		return content;
	}

	public static void release(Asset asset) {
		if (asset == null) {
			System.err.println("[assets]: Warning! attempted to unload a null asset!");
		} else {
			asset.updateReferenceCount(-1);
			// if the reference count has hit zero than remove the asset.
			if (asset.getReferenceCount() <= 0) {
				if(!unloadIdList.contains(asset.getAssetID())) {
					System.out.println("[assets]: flagged for unload: " + asset.toString());
					unloadIdList.add(asset.getAssetID());
				}
				if (asset instanceof IGPUAsset) {
					((IGPUAsset) asset).remove();
				}
			}
		}
	}

	public static void release(int id) {
		Asset asset = assetMap.get(id);
		release(asset);
	}

	public static void cleanup() {
		System.out.println("[assets]: cleaning up...");
		Spool.stopThreads();
		// forces all the assets to unload from the gpu.
		System.out.println("[assets]: unloading " + assetMap.size() + " stored asset(s)...");
		for (Asset asset : assetMap.values()) {
			release(asset);
		}
		releaseUnreferencedAssets();
	}

	private static void releaseUnreferencedAssets() {
		// delete all the id's in the unload list.
		for (int id : unloadIdList) {
			assetMap.remove(id);
		}
		unloadIdList.clear();
	}
}
