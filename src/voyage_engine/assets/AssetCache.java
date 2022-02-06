package voyage_engine.assets;

import java.util.LinkedList;

public class AssetCache {

    public LinkedList<Long> idList;

    public AssetCache() {
        idList = new LinkedList<Long>();
    }

    public void include(long id) {
        idList.add(id);
    }

    public void freeCache() {
        System.out.println("[assets]: releasing " + idList.size() + " assets from cache.");
        for(Long id : idList) {
            AssetManager.release(id);
        }
        idList.clear();
    }
}
