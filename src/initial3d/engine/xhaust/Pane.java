package initial3d.engine.xhaust;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Drawable;

import static initial3d.Initial3D.*;

public class Pane extends Drawable {

	private final BufferedImage bi;
	private final Texture tex;
	
	private final int width, height;
	
	private String str = "";

	public Pane(int width_, int height_) {
		
		width = width_;
		height = height_;
		
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		tex = Initial3D.createTexture(Texture.requiredSize(Math.max(width, height)));

		requestInputEnabled(true);
	}

	@Override
	protected void draw(Initial3D i3d, int framewidth, int frameheight) {

		if (doRepaint()) {
			tex.drawImage(bi);
		}
		
		i3d.disable(LIGHTING);
		
		i3d.enable(TEXTURE_2D);
		i3d.texImage2D(FRONT, tex, null, null);
		
		i3d.objectID(getDrawIDStart());
		
		i3d.matrixMode(MODEL);
		i3d.pushMatrix();
		i3d.loadIdentity();
		i3d.matrixMode(VIEW);
		i3d.pushMatrix();
		i3d.loadIdentity();
		
		i3d.begin(POLYGON);
		
		i3d.texCoord2d(0, 1);
		i3d.vertex3d(1, -1, 3);
		i3d.texCoord2d(1, 1);
		i3d.vertex3d(-1, -1, 3);
		i3d.texCoord2d(1, 0);
		i3d.vertex3d(-1, 1, 3);
		i3d.texCoord2d(0, 0);
		i3d.vertex3d(1, 1, 3);
		
		i3d.end();
		
		i3d.popMatrix();
		i3d.matrixMode(MODEL);
		i3d.popMatrix();

	}

	private boolean doRepaint() {

		// go down through components seeing if repaint required
		// if true, repaint component and all subcomponents
		// return true if anything painted
		
		Graphics g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.drawString(str, 20, 20);

		return true;

	}

	@Override
	protected void processKeyEvent(KeyEvent e) {
		System.out.println(e.getKeyChar());
		if (e.getID() == KeyEvent.KEY_TYPED) {
			str += e.getKeyChar();
		}
	}

	@Override
	protected void processMouseEvent(MouseEvent e, int drawid, int framex, int framey) {

	}

}
