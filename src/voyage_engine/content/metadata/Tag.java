package voyage_engine.content.metadata;

import spool.Asset;

public class Tag extends Asset {
    private int ID;
    private String name;
    private Tag parent;

    public Tag(String str, Tag parent) {
        super(false);
        // generate some ID setID(generateID());
        setName(str);
    }

    public int getID() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public Tag getParent() {
        return parent;
    }
    @Override
    public boolean isReady() {
        return isReady;
    }
    public void setID(int val) {
        ID = val;
    }
    public void setName(String val) {
        name = val;
    }
    public void setParent(Tag t) {
        parent = t;
    }
}
