package game.states;

import java.util.Scanner;

import game.Game;
import game.GameState;

/***
 * Pretty obvious, this is the main menu state.
 * 
 * From this state, the user chooses to create a game, or join an existing game. It should switch to the corrosponding state.
 * @author Ben
 *
 */
public class MainMenuState extends GameState {
	public MainMenuState(Game parent) {
		super(parent);
	}

	@Override
	public void initalise() {
		Scanner scanner = new Scanner(System.in);
		while(true)
		{
			System.out.printf("--Main Menu--\n1)Host a game\n2)Join a game\n\tChoice? ");
			scanner.hasNextInt();
			int option = scanner.nextInt();
			if(option == 1)
			{
				this.game.changeState(new CreateGameState(this.game));
				break;
			}
			else if(option == 2)
			{
				this.game.changeState(new FindGameState(this.game));
				break;
			}
			else
				System.out.printf("Unknown menu option\n");
		}
			
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {
	}	
}
