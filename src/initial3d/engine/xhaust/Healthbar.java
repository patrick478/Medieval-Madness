package initial3d.engine.xhaust;

import game.modelloader.Content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


public class Healthbar extends Component {
	public Healthbar() {
		super(500, 30);
	}
	
	Picture hpbar;
	private int currentHp = 0;
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(200,  0, currentHp * 3, 30);
		BufferedImage bi = Content.loadContent("resources/ui/health.png");
		g.drawImage(bi, 200, 0, 300, 30, null);
	}

	public void update(int health) {
		if(this.currentHp != health)
		{
			this.currentHp = health;
			this.repaint();
		}
	}
}
