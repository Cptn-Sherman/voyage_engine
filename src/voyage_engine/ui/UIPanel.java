package voyage_engine.ui;

import voyage_engine.assets.AssetManager;
import voyage_engine.assets.mesh.Mesh;
import voyage_engine.assets.mesh.MeshUtil;
import voyage_engine.graphics.Color;
import voyage_engine.graphics.IRenderable;
import voyage_engine.graphics.OpenGL;
import voyage_engine.assets.shader.Shader;
import voyage_engine.util.Vec2;

public class UIPanel extends UIComponent implements IRenderable {
    private final float DEFAULT_SCALE_FACTOR = 1.0f;

    private float scaleFactor;
    private Vec2 screenPos;

    private Mesh mesh;
    private Shader shader;

    public UIPanel (float rx, float ry, int width, int height) {
        this(rx, ry, width, height, UIAnchor.MIDDLE_CENTER);
    }

    public UIPanel(float x, float y, int width, int height, UIAnchor anchor) {
        setAnchor(anchor);
        setPosition(x, y);
        setDimensions(width, height);
        screenPos = new Vec2();
        scaleFactor = DEFAULT_SCALE_FACTOR;
        // request and generate required assets.
        shader = AssetManager.getShader("color_shader");
        mesh = MeshUtil.generateQuad(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
		super.setPosition(x, y);
        updateScreenPosition();
	}

	@Override
	public void setDimensions(float w, float h) {
		super.setDimensions(w, h);
        updateScreenPosition();
    }

	@Override
	public void setAnchor(UIAnchor anchor) {
		super.setAnchor(anchor);
        updateScreenPosition();
    }

    
    @Override
    public boolean supportsBatching() {
        return false;
    }
    
    @Override
    public void render() {
        if(!getVisibility()) return;
        if(!mesh.isReady()) return;
        OpenGL.checkEnableShader(shader);
        OpenGL.bindMesh(mesh);
        // set shader uniforms.
        OpenGL.loadVector2("translation", screenPos);
        // set view and projection matrix.
        OpenGL.loadMatrix("orthogonal", OpenGL.getCamera().getOrthogonalMatrix());
        OpenGL.loadColor("color", Color.WHITE);
        // dispatch render.
        OpenGL.draw();
        // clean up the stuffs.
        OpenGL.unbindMesh(mesh);
    }

    public void updateScreenPosition() {
        // take the screen position [0.0 to 1.0] and create the pixel screen coordinates using the visible position from the super UIComponent...
    
    }
}
