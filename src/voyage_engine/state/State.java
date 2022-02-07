package voyage_engine.state;

import voyage_engine.assets.AssetCache;

public abstract class State {
    
    private String stateName;
	
    protected AssetCache assetCache;

    protected State() {
        assetCache = new AssetCache();
        stateName = this.getClass().getSimpleName();
    }

	public abstract void tick(double delta);

    public abstract void slowTick();
     
    public abstract void render();
    
    public void dispose() {
        System.out.println("[state]: disposing [" + stateName + "] assets...");
        assetCache.freeCache();
    }

    public String getName() {
        return stateName;
    }
}
