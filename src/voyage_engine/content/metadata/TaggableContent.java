package voyage_engine.content.metadata;

import java.util.ArrayList;
import java.util.List;

public abstract class TaggableContent {
    private List<Tag> tags;

    public TaggableContent () {
        tags = new ArrayList<Tag>();
    }
}
