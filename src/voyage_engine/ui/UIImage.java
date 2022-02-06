package voyage_engine.ui;

import voyage_engine.assets.texture.Texture;
import voyage_engine.graphics.IRenderable;

public class UIImage extends UIComponent implements IRenderable {

	private Texture texture;
	
	public UIImage() {
		
	}
	
	@Override
	public boolean supportsBatching() {
		return false;
	}

	@Override
	public void render() {
		
	}
}
