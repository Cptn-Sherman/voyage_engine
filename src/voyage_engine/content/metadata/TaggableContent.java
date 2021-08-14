package voyage_engine.content.metadata;

import java.util.ArrayList;
import java.util.List;

import voyage_engine.content.ContentData;

public abstract class TaggableContent extends ContentData {
    private List<Tag> tags;

    public TaggableContent () {
        tags = new ArrayList<Tag>();
    }
}
