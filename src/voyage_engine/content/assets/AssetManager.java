package voyage_engine.content.assets;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import voyage_engine.content.assets.font.Font;
import voyage_engine.content.assets.shader.Shader;
import voyage_engine.content.assets.texture.Texture;
import spool.IData;
import spool.IJsonSource;
import spool.Spool;

public class AssetManager {
	private static Manifest manifest;
	private static HashMap<Long, Asset> assetMap;

	private static boolean forceUnload = false;

	public static void initialize(boolean rebaseManifest) {
		// Start spool and use the default initializer to auto detect thread count.
		Spool.initialize();
		assetMap = new HashMap<Long, Asset>();

		System.out.println("[manifest]: loading manifest...");
		if (!rebaseManifest) { // if we are not rebasing, just attempt to load the manifest.
			manifest = loadFromJsonAllowFailure("data\\manifest.json", Manifest.class, true);
			// if the manifest file was not loaded a new one will be compiled and saved to
			// the disk.
		}
		// if the manifest failed to load or if rebase is set to true a new manifest must be generated.
		if (manifest == null) {
			System.out.println("[manifest]: generating new manifest...");
			manifest = new Manifest();
		}
		// compile the manifest, checking for new assets or changes.
		manifest.compile();
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
	}

	public static String getFilePath(String filename) {
		return manifest.getPath(filename);
	}

	// returns the requested texture by filename if it can be found in the content
	// map
	// or returns a new pointer to the texture and attempts to load the content
	// using
	// multi-threaded Loading.
	public static Texture getTexture(String filename, boolean filter, boolean mipmap) {
		long id = manifest.getID(filename);
		Texture texture = (Texture) assetMap.get(id);
		if (texture == null) {
			texture = new Texture(manifest.getPath(filename), filter, mipmap);
			texture.setAssetID(id);
			assetMap.put(id, texture);
			Spool.addMultithreadProcess(texture);
		}
		texture.updateReferenceCount(1);
		return texture;
	}

	// returns the requested font by filename if it can be found in the content map
	// or returns a new pointer to the font and loads the font immediately.
	public static Font getFont(String filename, int oversampling, boolean filter) {
		Long id = manifest.getID(filename);
		Font font = (Font) assetMap.get(id);
		if (font == null) {
			font = new Font(manifest.getPath(filename), oversampling, filter);
			font.setAssetID(id);
			font.load();
			assetMap.put(id, font);
		}
		// this update reference also updates the reference count for the texture which
		// is attached.
		font.updateReferenceCount(1);
		return font;
	}

	// returns the requested shader file
	public static Shader getShader(String filename) {
		long id = manifest.getID(filename);
		Shader shader = (Shader) assetMap.get(id);
		if (shader == null) {
			shader = loadFromJson(manifest.getPath(filename), Shader.class, false);
			shader.setAssetID(id);
			shader.load();
			assetMap.put(id, shader);
		}
		shader.updateReferenceCount(1);
		return shader;
	}

	public static void writeToJson(IJsonSource content, String filepath, boolean writeAll) {
		System.out.println("[assets]: writing json data...");
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
	
	public static void unload(Asset asset) {
		if (asset == null) {
			System.err.println("[assets]: Warning! attempted to unload a null asset!");
		} else {
			asset.updateReferenceCount(-1);
			// if the reference count has hit zero than remove the asset.
			if(asset.getReferenceCount() < 0 || forceUnload) {
				assetMap.remove(asset.getAssetID());
				if(asset instanceof IGPUAsset) {
					((IGPUAsset) asset).remove();
				}
			}
		}
	}

	public static void cleanup() {
		System.out.println("[assets]: cleaning up...");
		Spool.stop();
		// forces all the assets to unload from the gpu.
		forceUnload = true;
		System.out.println("[assets]: unloading " + assetMap.size() + " stored asset(s)...");
		for (Asset asset : assetMap.values()) {
			unload(asset);
		}
	}


}
