package voyage_engine.ui;

import voyage_engine.Application;
import voyage_engine.util.Vec2;

public enum UIAnchor {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_CENTER,
    MIDDLE_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;
    
    public static void computeVisiblePosition(UIAnchor anchor, Vec2 pos, Vec2 dim, Vec2 vPos) {
        switch(anchor) {
            case BOTTOM_CENTER:
            	vPos.x = pos.x - (dim.x / 2) / (float) Application.getWidth();
                vPos.y = pos.y;
                break;
            case BOTTOM_LEFT:
            	vPos.x = pos.x;
                vPos.y = pos.y;
                break;
            case BOTTOM_RIGHT:
                vPos.x = pos.x - (dim.x) / (float) Application.getWidth();
                vPos.y = pos.y;
                break;
            case MIDDLE_CENTER:
            	vPos.x = pos.x - (dim.x / 2) / (float) Application.getWidth();
            	vPos.y = pos.y - (dim.y / 2) / (float) Application.getWidth();
                break;
            case MIDDLE_LEFT:
            	vPos.x = pos.x;
            	vPos.y = pos.y - (dim.y / 2) / (float) Application.getWidth();
                break;
            case MIDDLE_RIGHT:
            	vPos.x = pos.x - (dim.x) / (float) Application.getWidth();
            	vPos.y = pos.y - (dim.y / 2) / (float) Application.getWidth();
                break;
            case TOP_CENTER:
            	vPos.x = pos.x - (dim.x / 2) / (float) Application.getWidth();
            	vPos.y = pos.y - (dim.y) / (float) Application.getWidth();
                break;
            case TOP_LEFT:
            	vPos.x = pos.x;
            	vPos.y = pos.y - (dim.y) / (float) Application.getWidth();
                break;
            case TOP_RIGHT:
            	vPos.x = pos.x - (dim.x) / (float) Application.getWidth();
            	vPos.y = pos.y - (dim.y) / (float) Application.getWidth();
                break;
        }
    }
}
