package voyage_engine.graphics;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import spool.Spool;
import voyage_engine.Application;
import voyage_engine.assets.screenshot.Screenshot;
import voyage_engine.input.Input;
import voyage_engine.input.Input.MouseButton;


public class Window {
	private long window_address;
	private int width, height;
	private boolean focused = false;
	private boolean showMouseOnFocus = false;

	// screenshot buffer
	private static ByteBuffer screenshot_buffer;
	private static boolean staleBuffers = false;

	public void create() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("[client]: ERROR! Unable to initialize glfw");
		}

		// configure GLFW and set window hints.
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		// create the window
		width = Application.getSettings().getWidth();
		height = Application.getSettings().getHeight();
		window_address = glfwCreateWindow(width, height, "Remnants", NULL, NULL);
		System.out.println("[client]: creating window [" + width + "x" + height + "]...");

		// check that the window was created successfully.
		if (window_address == NULL) {
			throw new RuntimeException("[client]: ERROR! failed to create the GLFW window");
		}

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
			glfwGetWindowSize(window_address, pWidth, pHeight);
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			// Center the window
			glfwSetWindowPos(window_address, (vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		}

		// make the OpenGL context current
		glfwMakeContextCurrent(window_address);
		// enable v-sync
		glfwSwapInterval(0); // currently disabled
		// Make the window visible
		glfwShowWindow(window_address);
		// set the initial cursor position and capture the mouse upon creation
		GLFW.glfwSetCursorPos(window_address, width / 2f, height / 2f);
		setFocus(true);

		// creates the screenshot_buffer to the window size, used to store the pixel
		// data from the current rendered frame.
		// this buffer is sent to another thread to write to disk to prevent stuttering.
		// Allocating this buffer at this time
		// also ensures there is no long pause as the buffer always exists.
		screenshot_buffer = BufferUtils.createByteBuffer(width * height * 4);

		// set up the call backs for the window.
		// callback for resizing the window frame
		GLFW.glfwSetFramebufferSizeCallback(window_address, (window, width, height) -> {
			Application.getSettings().setDimensions(width, height);
			this.width = width;
			this.height = height;
			GL11.glViewport(0, 0, width, height);
			staleBuffers = true;
			// recalculate all ui elements and panels.
			// TODO: add these back in
//			Application.getState().resizeUI();
			System.out.println("[client]: display resized to [" + width + "x" + height + "]");
		});

		// callback for clicking inside or out of the window.
		GLFW.glfwSetWindowFocusCallback(window_address, (window, value) -> {
			setFocus(value);
		});
	}

	// queues a work ticket to save the screen buffer to a png.
	public void takeScreenshot() {
		if (screenshot_buffer != null && staleBuffers == false) {
			GL11.glReadBuffer(GL11.GL_FRONT);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, screenshot_buffer);
			// create the work ticket
			// push the work ticket.
			Screenshot screenshot = new Screenshot(screenshot_buffer, "PNG", width, height, 4);
			// push the ticket to the queue.
			Spool.addMultithreadProcess(screenshot);
		} else {
			System.out.println("[ERROR]: screen bytebuffer is stale or uninitialized.");
			staleBuffers = true;
		}
	}
	
	// checks the buffers to see if they need to be resized.
	public void checkBuffers() {
		if (Input.isMouseButtonUp(MouseButton.LEFT) && staleBuffers) {
			// resize ui elements now that the display is not changing size every update.
			// initialize up the buffer for rendering to screenshot file
			screenshot_buffer = BufferUtils.createByteBuffer(width * height * 4);
			staleBuffers = false;
			System.out.println("[client]: resized screenshot buffers.");
		}
	}
	
	public void setFocus(boolean value) {
		focused = value;
		// update the mouse visibility and capture status.
		if (Application.isFocused()) {
			if (!showMouseOnFocus) {
				GLFW.glfwSetInputMode(window_address, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			} else {
				GLFW.glfwSetInputMode(window_address, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			}
		} else {
			GLFW.glfwSetInputMode(window_address, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
		System.out.println("[client]: display focused " + Application.isFocused());
	}
	
	
	public void setShowMouseOnFocus(boolean value) {
		showMouseOnFocus = value;
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	public boolean shouldShowMouseOnFocus() {
		return showMouseOnFocus;
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(window_address);
	}

	public void update() {
		glfwSwapBuffers(window_address);
	}

	public long getAddress() {
		return window_address;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}



	// closes and disposes of window resources, clears callbacks, terminates GLFW.
	public void close() {
		// free the window callbacks and destroy the window
		glfwFreeCallbacks(window_address);
		glfwDestroyWindow(window_address);
		// terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}
