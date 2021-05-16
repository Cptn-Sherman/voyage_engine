package voyage_engine.assets;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import spool.Asset;
import spool.IData;
import spool.IJsonSource;
import spool.Spool;
import voyage_engine.assets.font.Font;
import voyage_engine.assets.texture.Texture;
import voyage_engine.graphics.Shader;

public class AssetManager {
	private static Manifest manifest;
	private static HashMap<Long, Asset> assetMap;

	public static void init(boolean rebaseManifest) {
		assetMap = new HashMap<Long, Asset>();

		System.out.println("[manifest]: loading manifest...");

		if (!rebaseManifest) {
			manifest = loadFromJsonAllowFailure("data\\manifest.json", Manifest.class, true);
			// if the manifest file was not loaded a new one will be compiled and saved to
			// the disk.
			if (manifest == null) {
				System.out.println("[manifest]: manifest was not found, compiling new manifest...");
				manifest = new Manifest();
				manifest.compile();
			}
		} else {
			manifest = loadFromJsonAllowFailure("data\\manifest.json", Manifest.class, true);
			// if the manifest file was not loaded a new one will be compiled and saved to
			// the disk.
			if (manifest == null) {
				System.out.println("[manifest]: rebasing manifest...");
				manifest = new Manifest();
				manifest.compile();
			} else {
				manifest.compile();
			}
		}
		
		// Start spool and use the default initializer to auto detect thread count.
		Spool.init();
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

	// determines if the asset is reference counted and if the number of references
	// has reached zero.
	public static boolean checkUnstoreAsset(Asset asset) {
		if (asset.isReferencedCounted() && asset.referenceCount() <= 0) {
			assetMap.remove(asset.getAssetID());
			return true;
		}
		return false;
	}

	public static void writeToJson(IJsonSource content, String filepath, boolean writeAll) {
		System.out.println("[content]: writing json data...");
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
					"[content]: Warning! issues occured reading JSON: " + filename + " however, failure was allowed.");
		} catch (IOException e) {
			System.out.println("[content]: Warning! issues occured with IO operations: " + filename
					+ " however, failure was allowed.");
		}
		return content;
	}
	
	public static void unload(Asset asset) {
		asset.updateReferenceCount(-1);
		// if the reference count has hit zero than remove the asset.
		if(asset.referenceCount() < 0) {
			assetMap.remove(asset.getAssetID());
			if(asset instanceof IGPUAsset) {
				((IGPUAsset) asset).remove();
			}
		}
	}

	public static void cleanup() {
		System.out.println("[assets]: cleaning up...");
		
		Spool.stop();
		// forces all the assets to unload from the gpu.
		System.out.println("[content]: unloading " + assetMap.size() + " stored asset(s)...");
		for (Asset asset : assetMap.values()) {
			unload(asset);
		}
	}

	public static String getFilePath(String filename) {
		return manifest.getPath(filename);
	}
}
