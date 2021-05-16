package voyage_engine.ui;

public abstract class UIComponent {

	private int pos_x, pos_y;
	private int width, height;
	
	private boolean visible = true;
	
	
	UIComponent() {
		this(0, 0, 10, 10);
	}
	
	UIComponent (int x, int y, int w, int h) {
		setPosition(x, y);
		setDimensions(w, h);
	}
	
	public void setVisibility(boolean vis) {
		visible = vis;
	}
	
	public void setPosition(int x, int y) {
		pos_x = x;
		pos_y = y;
	}
	
	public void setDimensions(int w, int h) {
		width = w;
		height = h;
	}
	
	public int getPosX() {
		return pos_x;
	}
	
	public int getPosY() {
		return pos_y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean getVisibility() {
		return visible;
	}
}
