package voyage_engine.input;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;


import voyage_engine.Application;
import voyage_engine.util.Vec2;

public class Input {
	private static HashMap<Key, Integer> KEY_TO_GLFW = new HashMap<Key, Integer>();
	private static HashMap<MouseButton, Integer> MOUSEBUTTON_TO_GLFW = new HashMap<MouseButton, Integer>();
	private static long window;
	// Keyboard input
	private static List<Key> lastKeys = new ArrayList<Key>();
	private static List<Key> currentKeys = new ArrayList<Key>();
	private static List<Key> consumedKeys = new ArrayList<Key>();

	// Mouse input
	private static List<MouseButton> lastMouseButtons = new ArrayList<MouseButton>();
	private static List<MouseButton> currentMouseButtons = new ArrayList<MouseButton>();
	private static Vec2 lastMouse = new Vec2();
	private static Vec2 currentMouse = new Vec2();
	private static Vec2 mouseDelta = new Vec2();

	public static void init(long w) {
		System.out.println("[client]: initializing input map...");
		window = w;
		MOUSEBUTTON_TO_GLFW.put(MouseButton.ONE, 0);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.TWO, 1);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.THREE, 2);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.FOUR, 3);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.FIVE, 4);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.SIX, 5);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.SEVEN, 6);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.EIGHT, 7);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.LAST, 7);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.LEFT, 0);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.RIGHT, 1);
		MOUSEBUTTON_TO_GLFW.put(MouseButton.MIDDLE, 2);
		KEY_TO_GLFW.put(Key.SPACE, 32);
		KEY_TO_GLFW.put(Key.APOSTROPHE, 39);
		KEY_TO_GLFW.put(Key.COMMA, 44);
		KEY_TO_GLFW.put(Key.MINUS, 45);
		KEY_TO_GLFW.put(Key.PERIOD, 46);
		KEY_TO_GLFW.put(Key.SLASH, 47);
		KEY_TO_GLFW.put(Key.NUM_0, 48);
		KEY_TO_GLFW.put(Key.NUM_1, 49);
		KEY_TO_GLFW.put(Key.NUM_2, 50);
		KEY_TO_GLFW.put(Key.NUM_3, 51);
		KEY_TO_GLFW.put(Key.NUM_4, 52);
		KEY_TO_GLFW.put(Key.NUM_5, 53);
		KEY_TO_GLFW.put(Key.NUM_6, 54);
		KEY_TO_GLFW.put(Key.NUM_7, 55);
		KEY_TO_GLFW.put(Key.NUM_8, 56);
		KEY_TO_GLFW.put(Key.NUM_9, 57);
		KEY_TO_GLFW.put(Key.SEMICOLON, 59);
		KEY_TO_GLFW.put(Key.EQUAL, 61);
		KEY_TO_GLFW.put(Key.A, 65);
		KEY_TO_GLFW.put(Key.B, 66);
		KEY_TO_GLFW.put(Key.C, 67);
		KEY_TO_GLFW.put(Key.D, 68);
		KEY_TO_GLFW.put(Key.E, 69);
		KEY_TO_GLFW.put(Key.F, 70);
		KEY_TO_GLFW.put(Key.G, 71);
		KEY_TO_GLFW.put(Key.H, 72);
		KEY_TO_GLFW.put(Key.I, 73);
		KEY_TO_GLFW.put(Key.J, 74);
		KEY_TO_GLFW.put(Key.K, 75);
		KEY_TO_GLFW.put(Key.L, 76);
		KEY_TO_GLFW.put(Key.M, 77);
		KEY_TO_GLFW.put(Key.N, 78);
		KEY_TO_GLFW.put(Key.O, 79);
		KEY_TO_GLFW.put(Key.P, 80);
		KEY_TO_GLFW.put(Key.Q, 81);
		KEY_TO_GLFW.put(Key.R, 82);
		KEY_TO_GLFW.put(Key.S, 83);
		KEY_TO_GLFW.put(Key.T, 84);
		KEY_TO_GLFW.put(Key.U, 85);
		KEY_TO_GLFW.put(Key.V, 86);
		KEY_TO_GLFW.put(Key.W, 87);
		KEY_TO_GLFW.put(Key.X, 88);
		KEY_TO_GLFW.put(Key.Y, 89);
		KEY_TO_GLFW.put(Key.Z, 90);
		KEY_TO_GLFW.put(Key.LEFT_BRACKET, 91);
		KEY_TO_GLFW.put(Key.BACKSLASH, 92);
		KEY_TO_GLFW.put(Key.RIGHT_BRACKET, 93);
		KEY_TO_GLFW.put(Key.GRAVE_ACCENT, 96);
		KEY_TO_GLFW.put(Key.ESCAPE, 256);
		KEY_TO_GLFW.put(Key.ENTER, 257);
		KEY_TO_GLFW.put(Key.TAB, 258);
		KEY_TO_GLFW.put(Key.BACKSPACE, 259);
		KEY_TO_GLFW.put(Key.INSERT, 260);
		KEY_TO_GLFW.put(Key.DELETE, 261);
		KEY_TO_GLFW.put(Key.RIGHT, 262);
		KEY_TO_GLFW.put(Key.LEFT, 263);
		KEY_TO_GLFW.put(Key.DOWN, 264);
		KEY_TO_GLFW.put(Key.UP, 265);
		KEY_TO_GLFW.put(Key.PAGE_UP, 266);
		KEY_TO_GLFW.put(Key.PAGE_DOWN, 267);
		KEY_TO_GLFW.put(Key.HOME, 268);
		KEY_TO_GLFW.put(Key.END, 269);
		KEY_TO_GLFW.put(Key.CAPS_LOCK, 280);
		KEY_TO_GLFW.put(Key.SCROLL_LOCK, 281);
		KEY_TO_GLFW.put(Key.NUM_LOCK, 282);
		KEY_TO_GLFW.put(Key.PRINT_SCREEN, 283);
		KEY_TO_GLFW.put(Key.PAUSE, 284);
		// Function keys
		KEY_TO_GLFW.put(Key.F1, 290);
		KEY_TO_GLFW.put(Key.F2, 291);
		KEY_TO_GLFW.put(Key.F3, 292);
		KEY_TO_GLFW.put(Key.F4, 293);
		KEY_TO_GLFW.put(Key.F5, 294);
		KEY_TO_GLFW.put(Key.F6, 295);
		KEY_TO_GLFW.put(Key.F7, 296);
		KEY_TO_GLFW.put(Key.F8, 297);
		KEY_TO_GLFW.put(Key.F9, 298);
		KEY_TO_GLFW.put(Key.F10, 299);
		KEY_TO_GLFW.put(Key.F11, 300);
		KEY_TO_GLFW.put(Key.F12, 301);
		KEY_TO_GLFW.put(Key.F13, 302);
		KEY_TO_GLFW.put(Key.F14, 303);
		KEY_TO_GLFW.put(Key.F15, 304);
		KEY_TO_GLFW.put(Key.F16, 305);
		KEY_TO_GLFW.put(Key.F17, 306);
		KEY_TO_GLFW.put(Key.F18, 307);
		KEY_TO_GLFW.put(Key.F19, 308);
		KEY_TO_GLFW.put(Key.F20, 309);
		KEY_TO_GLFW.put(Key.F21, 310);
		KEY_TO_GLFW.put(Key.F22, 311);
		KEY_TO_GLFW.put(Key.F23, 312);
		KEY_TO_GLFW.put(Key.F24, 313);
		KEY_TO_GLFW.put(Key.F25, 314);
		// Keypad
		KEY_TO_GLFW.put(Key.KP_0, 320);
		KEY_TO_GLFW.put(Key.KP_1, 321);
		KEY_TO_GLFW.put(Key.KP_2, 322);
		KEY_TO_GLFW.put(Key.KP_3, 323);
		KEY_TO_GLFW.put(Key.KP_4, 324);
		KEY_TO_GLFW.put(Key.KP_5, 325);
		KEY_TO_GLFW.put(Key.KP_6, 326);
		KEY_TO_GLFW.put(Key.KP_7, 327);
		KEY_TO_GLFW.put(Key.KP_8, 328);
		KEY_TO_GLFW.put(Key.KP_9, 329);
		// Key pad operations
		KEY_TO_GLFW.put(Key.KP_DECIMAL, 330);
		KEY_TO_GLFW.put(Key.KP_DIVIDE, 331);
		KEY_TO_GLFW.put(Key.KP_MULTIPLY, 332);
		KEY_TO_GLFW.put(Key.KP_SUBTRACT, 333);
		KEY_TO_GLFW.put(Key.KP_ADD, 334);
		KEY_TO_GLFW.put(Key.KP_ENTER, 335);
		KEY_TO_GLFW.put(Key.KP_EQUAL, 336);
		// Left and right lower
		KEY_TO_GLFW.put(Key.LEFT_SHIFT, 340);
		KEY_TO_GLFW.put(Key.LEFT_CONTROL, 341);
		KEY_TO_GLFW.put(Key.LEFT_ALT, 342);
		KEY_TO_GLFW.put(Key.LEFT_SUPER, 343);
		KEY_TO_GLFW.put(Key.RIGHT_SHIFT, 344);
		KEY_TO_GLFW.put(Key.RIGHT_CONTROL, 345);
		KEY_TO_GLFW.put(Key.RIGHT_ALT, 346);
		KEY_TO_GLFW.put(Key.RIGHT_SUPER, 347);
		KEY_TO_GLFW.put(Key.MENU, 348);
	}

	// updates the list of current keys and last keys.
	public static void poll() {
		// copy current keys to last keys.
		lastKeys.clear();
		for(Key k : currentKeys) {
			lastKeys.add(k);
		}
		currentKeys.clear();
		consumedKeys.clear();
		// copy current mousebuttons to last mousebuttons
		lastMouseButtons.clear();
		for(MouseButton mb : currentMouseButtons) {
			lastMouseButtons.add(mb);
		}
		currentMouseButtons.clear();

		// check all the mouse buttons to see what is down this frame.
		for (MouseButton mb : MouseButton.values()) {
			Integer glfw_val = MOUSEBUTTON_TO_GLFW.get(mb); // convert key to glfw
			int state = GLFW.glfwGetMouseButton(window, glfw_val.intValue());
			if (state == GLFW.GLFW_PRESS) {
				currentMouseButtons.add(mb);
			}
		}

		// check all the keys to see what is down this frame.
		for (Key key : Key.values()) {
			Integer glfw_key = KEY_TO_GLFW.get(key); // convert key to glfw
			int state = GLFW.glfwGetKey(window, glfw_key.intValue());
			if (state == GLFW.GLFW_PRESS) {
				currentKeys.add(key);
			}
		}

		// store previous position
		lastMouse.x = currentMouse.x;
		lastMouse.y = currentMouse.y;
		// get the mouse current position on screen
		DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(window, posX, posY);
		currentMouse.x = (float) posX.get(0) / (float) Application.getSettings().getWidth();
		currentMouse.y = 1.0f - (float) posY.get(0) / (float) Application.getSettings().getHeight();
		// update mouse deltas
		if(Application.isFocused()) {
			mouseDelta.x = currentMouse.x - lastMouse.x;
			mouseDelta.y = currentMouse.y - lastMouse.y;
		}
		// System.out.println(currentMouse.toString());
	}

	public static boolean isKeyDown(Key key) {
		return currentKeys.contains(key);
	}

	public static boolean isKeyUp(Key key) {
		return !currentKeys.contains(key);
	}

	// note: when using this function an if statement and and else if statement both call this and a second parametter is && 
	// the second call will always fail because the first check will return the key is down and the second condition could fail then 
	// the else statement will check again but fail even if the second would result in a pass for the total of both conditions.
	public static boolean isKeyDebounceDown(Key key) {
		if (currentKeys.contains(key) && (!lastKeys.contains(key))) {
			if (consumedKeys.contains(key)) {
				return false;
			} else {
				consumedKeys.add(key);
				return true;
			}
		} else {
			return false;
		}
	}

	public static  boolean isKeyDebounceUp(Key key) {
		return !currentKeys.contains(key) && lastKeys.contains(key);
	}

	public static  boolean isMouseButtonDown(MouseButton mb) {
		return currentMouseButtons.contains(mb);
	}

	public static boolean isMouseButtonUp(MouseButton mb) {
		return !currentMouseButtons.contains(mb);
	}

	public static boolean isMouseButtonDebounceDown(MouseButton mb) {
		return currentMouseButtons.contains(mb) && !lastMouseButtons.contains(mb);
	}

	public static boolean isMouseButtonDebounceUp(MouseButton mb) {
		return !currentMouseButtons.contains(mb) && lastMouseButtons.contains(mb);
	}

	public static Vec2 currentMousePosition() {
		return currentMouse;
	}

	public static Vec2 currentMouseDelta() {
		return mouseDelta;
	}

	public enum Key {
		SPACE,
		APOSTROPHE,
		COMMA,	 
		MINUS, 
		PERIOD,
		SLASH,
		NUM_0,
		NUM_1,
		NUM_2,
		NUM_3,
		NUM_4,
		NUM_5,
		NUM_6,
		NUM_7,
		NUM_8,
		NUM_9,
		SEMICOLON,
		EQUAL,
		A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
		LEFT_BRACKET,
		BACKSLASH,
		RIGHT_BRACKET,
		GRAVE_ACCENT,
		ESCAPE,ENTER,
		TAB,
		BACKSPACE,
		INSERT,
		DELETE,
		RIGHT,
		LEFT,
		DOWN,
		UP,
		PAGE_UP,
		PAGE_DOWN,
		HOME,
		END,
		CAPS_LOCK,
		SCROLL_LOCK,
		NUM_LOCK,
		PRINT_SCREEN,
		PAUSE,
		F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12,
		F13, F14, F15, F16, F17, F18, F19, F20, F21, F22, F23, F24, F25,
		KP_0, KP_1 , KP_2, KP_3, KP_4, KP_5, KP_6, KP_7, KP_8, KP_9,
		KP_DECIMAL,
		KP_DIVIDE,
		KP_MULTIPLY,
		KP_SUBTRACT,
		KP_ADD,
		KP_ENTER,
		KP_EQUAL,
		LEFT_SHIFT,
		LEFT_CONTROL,
		LEFT_ALT,
		LEFT_SUPER,
		RIGHT_SHIFT,
		RIGHT_CONTROL,
		RIGHT_ALT,
		RIGHT_SUPER,
		MENU
	}

	public enum MouseButton {
		ONE,
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		LAST,
		LEFT,
		RIGHT,
		MIDDLE,
	}
}

