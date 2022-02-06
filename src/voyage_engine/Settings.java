package voyage_engine;

import com.google.gson.annotations.Expose;

import spool.IJsonSource;
import voyage_engine.assets.AssetManager;

public class Settings implements IJsonSource {
	@Expose
	boolean limitFPS = false;
	@Expose
	int width = 1280;
	@Expose
	int height = 720;
	
	float width_scale;
	float height_scale;
	
	@Expose
	boolean fullscreen = false;
	
	// graphical settings.
	@Expose
	int chunkLoadDistance = 16;
	
	// music settings.
	@Expose
	float musicFrequency = 0.5f;
	@Expose
	float masterVolume = 1.0f;
	@Expose
	float sfxVolume = 1.0f;
	@Expose
	float musicVolume = 1.0f;
	@Expose
	float ambientVolume = 1.0f;
	
	// controls
	@Expose
	float mouseSensitivity = 0.05f;
	
	// debug settings.
	@Expose
	boolean debugInfoVisible = false;
	@Expose
	boolean debugLogs = false;
	
	public boolean shouldLimitFPS() {
		return limitFPS;
	}
	
	public int getHeight() {
		return height;
	}

	public float getWidthScale() {
		return width_scale;
	}

	public float getHeightScale() {
		return height_scale;
	}

	public void setHeightScale(float val) {
		height_scale = val;
	}
	
	public void setWidthScale(float val) {
		width_scale = val;
	}

	public int getWidth() {
		return width;
	}
	
	public void setDimensions(int w, int h) {
		width = w;
		height = h;
	}
	
	public void save() {
		AssetManager.writeToJson(this, "data\\custom_settings.json", false);
	}
}
