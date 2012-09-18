package client;

public class StartupState extends GameState {

	public StartupState() {
		super();
	}

	@Override
	public void update(long sinceLast) {
		this.changeState(new LoginState());
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
	}

}
