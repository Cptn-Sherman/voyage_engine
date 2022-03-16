package voyage_engine.content.item;

import voyage_engine.content.ContentData;

public class Item extends ContentData {
    private String name;
    private int value;
    private float weight;

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public float getWeight() {
        return weight;
    }

    public void setName(String val) {
        name = val;
    }
    
    public void setValue(int val) {
        value = val;
    }

    public void setWeight(float val) {
        weight = val;
    }
}
