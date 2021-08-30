package voyage_engine.assets.compute;
import spool.SpoolAsset;
import spool.IInstantLoad;
import spool.IJsonSource;
import voyage_engine.assets.IGPUAsset;

public class ComputeShader extends SpoolAsset implements IInstantLoad, IJsonSource, IGPUAsset {

    public ComputeShader(boolean referenceCounted) {
        super(referenceCounted);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isReady() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void load() {

    }
}
