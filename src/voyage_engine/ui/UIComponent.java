package voyage_engine.ui;

import voyage_engine.Application;
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
		updateVisiblePosition();
	}
	
	public void setVisibility(boolean vis) {
		visible = vis;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
		updateVisiblePosition();
	}
	
	public void setDimensions(float length, float height) {
		dimensions.set(length, height);
		updateVisiblePosition();
	}
	
	public void setAnchor(UIAnchor anchor) {
		this.anchor = anchor;
		updateVisiblePosition();
	}

	// this assumes the origin is the upper left corner of the mesh.
	public void updateVisiblePosition() {
		switch(anchor) {
			case BOTTOM_CENTER:
				visiblePos.x = position.x - ((dimensions.x / 2) / (float) Application.getWidth());
				visiblePos.y = position.y;
				break;
			case BOTTOM_LEFT:
				visiblePos.x = position.x;
				visiblePos.y = position.y;
				break;
			case BOTTOM_RIGHT:
				visiblePos.x = position.x - ((dimensions.x) / (float) Application.getWidth());
				visiblePos.y = position.y;
				break;
			case MIDDLE_CENTER:
				visiblePos.x = position.x - ((dimensions.x / 2) / (float) Application.getWidth());
				visiblePos.y = position.y + ((dimensions.y / 2) / (float) Application.getHeight());
				break;
			case MIDDLE_LEFT:
				visiblePos.x = position.x;
				visiblePos.y = position.y + ((dimensions.y / 2) / (float) Application.getHeight());
				break;
			case MIDDLE_RIGHT:
				visiblePos.x = position.x - ((dimensions.x) / (float) Application.getWidth());
				visiblePos.y = position.y + ((dimensions.y / 2) / (float) Application.getHeight());
				break;
			case TOP_CENTER:
				visiblePos.x = position.x - ((dimensions.x / 2) / (float) Application.getWidth());
				visiblePos.y = position.y + ((dimensions.y) / (float) Application.getHeight());
				break;
			case TOP_LEFT:
				visiblePos.x = position.x;
				visiblePos.y = position.y + ((dimensions.y) / (float) Application.getHeight());
				break;
			case TOP_RIGHT:
				visiblePos.x = position.x - ((dimensions.x) / (float) Application.getWidth());
				visiblePos.y = position.y + ((dimensions.y) / (float) Application.getHeight());
				break;
		}
	}
	
	public Vec2 getPosition() {
		return position;
	}
	
	public Vec2 getVisiblePosition() {
		return visiblePos;
	}
	
	public Vec2 getdimensionsensions() {
		return dimensions;
	}

	public UIAnchor getAnchor() {
		return anchor;
	}
	
	public boolean getVisibility() {
		return visible;
	}
}
