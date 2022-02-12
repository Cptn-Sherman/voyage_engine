package voyage_engine.assets;

import java.util.LinkedList;

public class AssetCache {

    public LinkedList<String> idList;

    public AssetCache() {
        idList = new LinkedList<String>();
    }

    public void include(String id) {
        idList.add(id);
    }

    public void freeCache() {
        System.out.println("[assets]: releasing " + idList.size() + " assets from cache.");
        for(String id : idList) {
            AssetManager.release(id);
        }
        idList.clear();
    }
}
