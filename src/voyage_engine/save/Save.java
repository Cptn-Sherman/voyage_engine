package voyage_engine.save;

import java.io.File;

import spool.IMultithreadProcess;

public class Save implements IMultithreadProcess {
    public boolean isComplete = false;
    public String hexCode;

    @Override
    public void process() {
        System.out.println("[save]: generating file structure...");
        // generate file structure.
        // todo: replace this with actual character name and 
        File file = new File("save\\HEXCODE:CHARNAME.toHashcode()\\"); 
        if(file.exists() == false)
            file.mkdir();
        // write character data.
        System.out.println("[save]: writing character...");
        // write world_state data.
        System.out.println("[save]: writing world state...");
        // write world_hunks data. File format is .HNK in folder called "world_hunks" 
        System.out.println("[save]: writing world hunks...");
        // write history_log data.
        System.out.println("[save]: writing history...");
        // flag as complete.
        System.out.println("[save]: save complete.");
        isComplete = true;
    }
}
