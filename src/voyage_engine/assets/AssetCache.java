package voyage_engine.assets;

import java.util.LinkedList;

public class AssetCache {

    public LinkedList<Integer> idList;

    public AssetCache() {
        idList = new LinkedList<Integer>();
    }

    public void include(int id) {
        idList.add(id);
    }

    public void freeCache() {
        System.out.println("[assets]: releasing " + idList.size() + " assets from cache.");
        for(int id : idList) {
            AssetManager.release(id);
        }
        idList.clear();
    }
}
