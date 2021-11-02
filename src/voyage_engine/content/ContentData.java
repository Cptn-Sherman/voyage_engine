package voyage_engine.content;

public abstract class ContentData extends Content {
   public long ID; 

   public long getID() {
      return ID;
   }

   public void setID(long id) {
      ID = id;
   }
}
