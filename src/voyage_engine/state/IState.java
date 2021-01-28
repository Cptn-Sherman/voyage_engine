package voyage_engine.state;

public interface IState {
	
	public void tick(double deltaTime);

    public void slowTick();
     
    public void render();
    
    public void dispose();

    public void resizeUI();
}
