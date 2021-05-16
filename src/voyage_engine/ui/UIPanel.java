package voyage_engine.ui;

import voyage_engine.Application;
import voyage_engine.assets.AssetManager;
import voyage_engine.assets.mesh.Mesh;
import voyage_engine.assets.mesh.MeshUtil;
import voyage_engine.graphics.Color;
import voyage_engine.graphics.IRenderable;
import voyage_engine.graphics.OpenGL;
import voyage_engine.graphics.Shader;

public class UIPanel implements IRenderable {
    private final float DEFAULT_SCALE_FACTOR = 1.0f;
	
	private AnchorType anchor;
    private boolean visible = true;
    private float rx, ry, vx, vy;
    private int width, height;
    
    private float scaleFactor = DEFAULT_SCALE_FACTOR;

    private Mesh mesh;
    private Shader shader;

    public UIPanel (float rx, float ry, int width, int height) {
        this(rx, ry, width, height, AnchorType.MIDDLE_CENTER);
    }

    public UIPanel(float rx, float ry, int width, int height, AnchorType type) {
    	super();
        setPosition(rx, ry);
        setAnchorType(type);
        setDimensions(width, height);
        computePosition();
        // load the shader used to render panels.
        shader = AssetManager.getShader("color_shader");
        // generate the mesh for the quad.
        mesh = MeshUtil.generateQuad(width, height);
    }

    public void setPosition(float relativeX, float relativeY) {
        this.rx = relativeX;
        this.ry = relativeY;
    }
    
    public void setDimensions(int pixelWidth, int pixelHeight) {
        this.width = pixelWidth;
        this.height = pixelHeight;
        
    }
    
    public void setAnchorType(AnchorType type) {
        this.anchor = type;
        
    }

    private void computePosition() {
        switch(anchor) {
        case BOTTOM_CENTER:
            vx = rx;
            vy = ry + (height / 2) / (float) Application.getHeight();
            break;
        case BOTTOM_LEFT:
            vx = rx + (width / 2) /  (float) Application.getWidth();
            vy = ry + (height / 2) / (float) Application.getHeight();
            break;
        case BOTTOM_RIGHT:
            vx = rx - (width / 2) /  (float) Application.getWidth();
            vy = ry + (height / 2) / (float) Application.getHeight();
            break;
        case MIDDLE_CENTER:
            vx = rx;
            vy = ry;
            break;
        case MIDDLE_LEFT:
            vx = rx + (width / 2) /  (float) Application.getWidth();
            vy = ry;
            break;
        case MIDDLE_RIGHT:
            vx = rx - (width / 2) /  (float) Application.getWidth();
            vy = ry;
            break;
        case TOP_CENTER:
            vx = rx;
            vy = ry - (height / 2) / (float) Application.getHeight();
            break;
        case TOP_LEFT:
            vx = rx + (width / 2) /  (float) Application.getWidth();
            vy = ry - (height / 2) / (float) Application.getHeight();
            break;
        case TOP_RIGHT:
            vx = rx - (width / 2) /  (float) Application.getWidth();
            vy = ry - (height / 2) / (float) Application.getHeight();
            break;
        }
    }

    
    @Override
    public boolean supportsBatching() {
        // TODO this probably willlllllll at some point idk.
        return false;
    }
    
    @Override
    public void render() {
        if(!visible) return;
        if(!mesh.isReady()) return;
        OpenGL.checkEnableShader(shader);
        OpenGL.bindMesh(mesh);
        // set shader uniforms.
        // todo: replace the application part.
        OpenGL.loadVector2("translation", vx, vy);
        // set view and projection matrix.
        OpenGL.loadMatrix("orthogonal", OpenGL.getCamera().getOrthogonalMatrix());
        OpenGL.loadColor("color", Color.WHITE);
        // dispatch render.
        OpenGL.draw();
        // clean up the stuffs.
        OpenGL.unbindMesh(mesh);
    }

    // enum used to define the origin for the panel.
    public enum AnchorType {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_CENTER,
        MIDDLE_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
    }
}
