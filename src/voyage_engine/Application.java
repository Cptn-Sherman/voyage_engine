package voyage_engine;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import voyage_engine.assets.AssetManager;
import voyage_engine.graphics.OpenGL;
import voyage_engine.graphics.Window;
import voyage_engine.input.Input;
import voyage_engine.input.Input.Key;
import voyage_engine.state.State;

public class Application {
	private static boolean running = true;
	
	private static Window window;
	private static Settings settings;
	private static State currentState;
	
	// application performance metrics.
    private static int frame_count, tick_count;
	private static int fps, tps;
	private static long free_memory, used_memory, total_memory, max_memory;
	private static float used_memory_percentage;
	
	public static void main(String[] args) {
		new Application();
	}

	public Application() {
		window = new Window();
		initialize();
		// timing variables.
		final double nsPerTick = 1000000000.0D / 60.0D;
		double timer = System.currentTimeMillis();
		double currentTick = System.nanoTime();
		double lastTick = System.nanoTime();
		double unprocessed = 0;
		boolean shouldRender;
		long now, last = System.nanoTime();

		while (running) {
			shouldRender = !settings.shouldLimitFPS();
			now = System.nanoTime();
			unprocessed += (now - last) / nsPerTick;
			last = now;
			
			// checks if the window is requesting to close.
			if (window.shouldClose())
				running = false;
			
			// catch up on updates to hit target ticks per second.
			while (unprocessed >= 1) {
				// update delta time per tick.
				currentTick = System.nanoTime();
				// check the screenshot buffers
				window.checkBuffers();
				// update the inputs
				glfwPollEvents();
				Input.poll();
				double delta = (currentTick / 1000000000.0) - (lastTick / 1000000000.0);
				tick(delta);
				tick_count++;
				// update unprocessed value.
				unprocessed -= 1.0d;
				shouldRender = true;
				lastTick = currentTick;
			}
			
			// fetch any data that needs to be stored on the gpu or finalized at this time.
			AssetManager.poll();

			if (shouldRender) {
				OpenGL.prepare();
				render();
				window.update();
				OpenGL.glErrorCheck();
				frame_count++;
			}
			
			// if 1 second has elapsed write the counters and reset everything.
			if (System.currentTimeMillis() - timer > 1000) {
				slowTick();
				fps = frame_count;
				tps = tick_count;
				frame_count = 0;
				tick_count = 0;
				timer += 1000;
			}
		}
		dispose();
	}
	
	public void render() {
		if(currentState != null)
			currentState.render();		
	}

	protected void initialize() {
		settings = AssetManager.<Settings>loadFromJson("custom_settings.json", Settings.class, false);
		// setup logging if enabled.
		if (settings.debugLogs) {
			// create new print stream for console out.
			//System.setOut(newOut);
		}
		window.create();
		OpenGL.initialize();
		Input.initialize(window.getAddress());
		AssetManager.initialize(false);
		// print version information
		max_memory = (Runtime.getRuntime().maxMemory() / (1024 * 1024));
		String maxMemoryStr = (max_memory == Long.MAX_VALUE) ? "no limit" : (max_memory + "MB");
		System.out.println("[client]: java: " + System.getProperty("java.version"));		
		System.out.println("[client]: openGL: " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("[client]: lwjgl: " + Version.getVersion());
		System.out.println("[client]: memory: " + maxMemoryStr);
	}
	
	protected void tick(double delta) {
		if(currentState != null)
			currentState.tick(delta);
		if (Input.isKeyDown(Key.F12)) {
            Application.running = false;
		}
		if(Input.isKeyDebounceDown(Key.END)) {
			window.takeScreenshot();
		}
	}
	
	protected void slowTick() {
		if(currentState != null)
			currentState.slowTick();
		// get values for memory usage.
		max_memory = Runtime.getRuntime().maxMemory();
		free_memory = Runtime.getRuntime().freeMemory();
		total_memory = Runtime.getRuntime().totalMemory();
		// compute used memory.
		used_memory = total_memory - free_memory;
		// compute used memory as percentage.
		used_memory_percentage = (float) used_memory / (float) total_memory;
		used_memory_percentage *= 100;
		used_memory_percentage = Math.round(used_memory_percentage);
		// print fps, tps, and memory usage.
		System.out.println(frame_count + " frames, " + tick_count + " ticks, " + "[" + used_memory_percentage + "%] used: " + byteToMegabyte(used_memory) + "MB, allocated: " + byteToMegabyte(total_memory) + "MB");
	}
	
	protected void dispose() {
		if (currentState != null) {
			currentState.dispose();
		}
		AssetManager.cleanup();
		window.close();
		settings.save();
		System.exit(0);
	}
	
	public static Settings getSettings() {
		return settings;
	}
	
	public static Window getWindow() {
		return window;
	}

	public static void setFocused(boolean value) {
		window.setFocus(value);
	}

	public static boolean isFocused() {
		return window.isFocused();
	}

	public static int getFPS() {
		return fps;
	}
	
	public static int getTPS() {
		return tps;
	}

	public static int getWidth() {
		return window.getWidth();
	}

	public static int getHeight() {
		return window.getHeight();
	}

	private static long byteToMegabyte(long val) {
		return val / (1024 * 1024);
	}

    public static void setState(State state) {
		System.out.println("[client]: setting state: " + state.getName());
		// releasing the cached assets now that a new state has been set.
		if (currentState != null) {
			currentState.dispose();
		}
		currentState = state;
    }
}
