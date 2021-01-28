package voyage_engine.graphics;

public interface IRenderer {
    
    // this is complicated
    public <T extends IRenderable> void render(T renderable);
}