package voyage_engine.ui;

import voyage_engine.Application;
import voyage_engine.assets.AssetCache;
import voyage_engine.assets.AssetManager;
import voyage_engine.assets.font.Font;
import voyage_engine.assets.mesh.Mesh;
import voyage_engine.assets.shader.Shader;
import voyage_engine.graphics.Color;
import voyage_engine.graphics.IRenderable;
import voyage_engine.graphics.OpenGL;

public class UILabel extends UIComponent implements IRenderable {
	
	Shader shader;
	Font font;
	String text;
	int size;
	float length, height;
	Color color;
	Mesh mesh;
	
	public UILabel(AssetCache cache, String t, int s) {
		this(cache, t, s, Color.WHITE, 0, 0, UIAnchor.BOTTOM_LEFT);
	}
	
	public UILabel(AssetCache cache, String t, int s, Color c, int x, int y, UIAnchor anchor) {
		super(x, y, 0, 0, anchor);
		shader = AssetManager.getShader(cache, "text_shader");
		font = AssetManager.getFont(cache, "Montserrat-Bold", 2, true);
		text = new String();
		size = s;
		mesh = new Mesh();
		setColor(Color.WHITE);
		setText(t);
		
	}
	
	public void setText(String t) {
		if(!text.equals(t)) {
			// we need to re-mesh now that the text has changed.
			text = t;
			font.generateMesh(mesh, text, size, dimensions);
			updateVisiblePosition();
		}
	}
	
	public void setColor(Color c) {
		color = c;
	}

	@Override
	public boolean supportsBatching() {
		return false;
	}

	@Override
	public void updateVisiblePosition() {
		switch(anchor) {
			case BOTTOM_CENTER:
				visiblePos.x = position.x - (dimensions.x / 2f);
				visiblePos.y = position.y + dimensions.y;
				break;
			case BOTTOM_LEFT:
				visiblePos.x = position.x;
				visiblePos.y = position.y + dimensions.y;
				break;
			case BOTTOM_RIGHT:
				visiblePos.x = position.x - dimensions.x;
				visiblePos.y = position.y + dimensions.y;
				break;
			case MIDDLE_CENTER:
				visiblePos.x = position.x - (dimensions.x / 2f);
				visiblePos.y = position.y + (dimensions.y / 2f);
				break;
			case MIDDLE_LEFT:
				visiblePos.x = position.x;
				visiblePos.y = position.y + (dimensions.y / 2f);
				break;
			case MIDDLE_RIGHT:
				visiblePos.x = position.x - dimensions.x;
				visiblePos.y = position.y + (dimensions.y / 2f);
				break;
			case TOP_CENTER:
				visiblePos.x = position.x - (dimensions.x / 2f);
				visiblePos.y = position.y;
				break;
			case TOP_LEFT:
				visiblePos.x = position.x;
				visiblePos.y = position.y;
				break;
			case TOP_RIGHT:
				visiblePos.x = position.x - dimensions.x;
				visiblePos.y = position.y;
				break;
		}
		visiblePos.x /= (float) Application.getWidth();
		visiblePos.y /= (float) Application.getHeight();
	}

	@Override
	public void render() {
		OpenGL.checkEnableShader(shader);
		OpenGL.bindMesh(mesh);
		OpenGL.bindTexture(font.getTexture());
		
		OpenGL.loadMatrix("orthogonal", OpenGL.getCamera().getOrthogonalMatrix());
		OpenGL.loadVector2("translation", (float) getVisiblePosition().x, getVisiblePosition().y);
		OpenGL.loadColor("color", color);
		
        OpenGL.draw();
        // clean up the texture and the mesh bindings.
        OpenGL.unbindTexture();
        OpenGL.unbindMesh(mesh);
	}
}
