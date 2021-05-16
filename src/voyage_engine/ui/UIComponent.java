package voyage_engine.ui;

import voyage_engine.util.Vec2;

public abstract class UIComponent {
	protected Vec2 position, visiblePos;
	protected Vec2 dimensions;
	protected UIAnchor anchor;
	
	private boolean visible = true;
	
	UIComponent() {
		this(0, 0, 10, 10, UIAnchor.BOTTOM_LEFT);
	}
	
	UIComponent (int x, int y, int w, int h, UIAnchor anchor) {
		position = new Vec2();
		visiblePos = new Vec2();
		dimensions = new Vec2();
		
		setAnchor(anchor);
		setPosition(x, y);
		setDimensions(w, h);
		UIAnchor.computeVisiblePosition(anchor, position, dimensions, visiblePos);
	}
	
	public void setVisibility(boolean vis) {
		visible = vis;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
		UIAnchor.computeVisiblePosition(anchor, position, dimensions, visiblePos);
	}
	
	public void setDimensions(float length, float height) {
		dimensions.set(length, height);
		UIAnchor.computeVisiblePosition(anchor, position, dimensions, visiblePos);
	}
	
	public void setAnchor(UIAnchor anchor) {
		this.anchor = anchor;
		UIAnchor.computeVisiblePosition(anchor, position, dimensions, visiblePos);
	}
	
	public Vec2 getPosition() {
		return position;
	}
	
	public Vec2 getVisiblePosition() {
		return visiblePos;
	}
	
	public Vec2 getDimensions() {
		return dimensions;
	}

	public UIAnchor getAnchor() {
		return anchor;
	}
	
	public boolean getVisibility() {
		return visible;
	}
}
