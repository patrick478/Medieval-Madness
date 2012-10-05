package client;

import initial3d.engine.RenderWindow;
import initial3d.engine.Scene;

import java.util.Scanner;

public class LoginState extends AbstractState {

	private boolean begunLogin = false;
	public LoginState() {
		super();
	}

	@Override
	public void update(long updateTime, RenderWindow window) {
		if(!this.client.net.isConnected()) return;
		
		// THIS IS SO DISGUSTING - I CAN'T EVEN LOOK MYSELF IN THE MIRROR!
		if(!this.begunLogin)
		{
			System.out.printf("Username: ");
			Scanner scanner = new Scanner(System.in);
			scanner.hasNext();
			String username = scanner.next();
			System.out.printf("Password: ");
			scanner.hasNext();
			String password = scanner.next();
			
			this.client.net.beginLogin(username, password);
			this.begunLogin = true;
		}
	}
}
