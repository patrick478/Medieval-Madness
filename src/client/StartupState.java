package client;

import initial3d.engine.Scene;

public class StartupState extends AbstractState {

	public StartupState() {
		super();
	}

	@Override
	public void update(long sinceLast) {
		this.client.net.Connect("127.0.0.1", 14121);
		while(!this.client.net.isConnected())
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		this.changeState(new LoginState());
	}
}
