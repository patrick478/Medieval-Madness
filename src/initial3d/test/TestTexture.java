package initial3d.test;

import initial3d.Initial3D;
import initial3d.Texture;

import static initial3d.Texture.Channel;

public class TestTexture {

	public static void main(String[] args) {

		Texture tex = Initial3D.createTexture(64);

		for (int u = 0; u < 16; u++) {

			for (int v = 0; v < 16; v++) {

				tex.setPixel(u, v, 1f, 0.3f, 0.7f, 0.4f);

			}

		}

		for (int u = 0; u < 64; u++) {

			for (int v = 0; v < 64; v++) {

				System.out.println(tex.getPixel(u, v, Channel.BLUE));

			}

		}

	}

}