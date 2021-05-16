package voyage_engine.ui;

import voyage_engine.assets.AssetManager;
import voyage_engine.assets.font.Font;
import voyage_engine.assets.mesh.Mesh;
import voyage_engine.graphics.Color;
import voyage_engine.graphics.IRenderable;
import voyage_engine.graphics.OpenGL;
import voyage_engine.graphics.Shader;

public class UILabel extends UIComponent implements IRenderable {
	
	Shader shader;
	Font font;
	String text;
	int size;
	float length, height;
	Color color;
	Mesh mesh;
	
	public UILabel(String t, int s) {
		this(t, s, Color.WHITE, 0, 0);
	}
	
	public UILabel(String t, int s, Color c, int x, int y) {
		super(x, y, 0, 0);
		shader = AssetManager.getShader("text_shader");
		font = AssetManager.getFont("Montserrat-Bold", 2, true);
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
			length = font.generateMesh(mesh, text, size);
			height = font.getLineHeight(size);
		}
	}
	
	public void setColor(Color c) {
		color = c;
	}

	@Override
	public boolean supportsBatching() {
		// TODO: implement batching for UI elements.
		return false;
	}

	@Override
	public void render() {
		OpenGL.checkEnableShader(shader);
		
		OpenGL.bindMesh(mesh);
		OpenGL.bindTexture(font.getTexture());
		
		OpenGL.loadMatrix("orthogonal", OpenGL.getCamera().getOrthogonalMatrix());
		OpenGL.loadVector2("translation", (float) getPosX(), - (float) getPosY() / 2f);
		OpenGL.loadColor("color", color);
		
        OpenGL.draw();
        // clean up the texture and the mesh bindings.
        OpenGL.unbindTexture();
        OpenGL.unbindMesh(mesh);
	}
}
