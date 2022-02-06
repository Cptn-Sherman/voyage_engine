package voyage_engine.state;

import voyage_engine.content.assets.AssetCache;

public abstract class State {
	
    protected AssetCache assetCache;
    private String stateName;

    protected State() {
        assetCache = new AssetCache();
        stateName = this.getClass().getSimpleName();
    }

	public abstract void tick(double delta);

    public abstract void slowTick();
     
    public abstract void render();
    
    public void dispose() {
        assetCache.freeCache();
    }

    public String getName() {
        return stateName;
    }
}
