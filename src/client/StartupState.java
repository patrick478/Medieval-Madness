package client;

public class StartupState extends AbstractState {

	public StartupState() {
		super();
	}

	@Override
	public void update(long sinceLast) {
		this.client.net.Connect("127.0.0.1", 14121);
		
		this.changeState(new LoginState());
	}

	@Override
	public void fetchScene() {
		// TODO Auto-generated method stub
		
	}

}
