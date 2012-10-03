package initial3d.test;

import initial3d.Initial3D;
import initial3d.Texture;

import static initial3d.Texture.Channel;

public class TestTexture {

	public static void main(String[] args) {

//		Texture tex = Initial3D.createTexture(64);
//
//		for (int u = 0; u < 16; u++) {
//			for (int v = 0; v < 16; v++) {
//				tex.setTexel(u, v, 1f, 0.3f, 0.7f, 0.4f);
//			}
//		}
//
//		for (int u = 0; u < 64; u++) {
//			for (int v = 0; v < 64; v++) {
//				System.out.println(tex.getTexel(u, v, Channel.BLUE));
//			}
//		}
//
//		// test creation limit
//		for (int i = 0; i < 10000; i++) {
//			tex = Initial3D.createTexture(1024);
//		}
		
		for (int i = 0; i < 20; i++) {
			
			int maxdim = (int) (Math.random() * Math.random() * 1024);
			
			System.out.println(maxdim + ": " + Texture.requiredSize(maxdim));
			
		}

	}

}
