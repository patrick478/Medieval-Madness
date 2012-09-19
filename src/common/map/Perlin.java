package common.map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Perlin {

	private final int GradientSizeTable = 256;
	private final Random _random;
	private final double[] _gradients = new double[GradientSizeTable * 3];
	
	private final int[] _perm = new int[] {
			225,155,210,108,175,199,221,144,203,116, 70,213, 69,158, 33,252,
			5, 82,173,133,222,139,174, 27,  9, 71, 90,246, 75,130, 91,191,
			169,138,  2,151,194,235, 81,  7, 25,113,228,159,205,253,134,142,
			248, 65,224,217, 22,121,229, 63, 89,103, 96,104,156, 17,201,129,
			36,  8,165,110,237,117,231, 56,132,211,152, 20,181,111,239,218,
			170,163, 51,172,157, 47, 80,212,176,250, 87, 49, 99,242,136,189,
			162,115, 44, 43,124, 94,150, 16,141,247, 32, 10,198,223,255, 72,
			53,131, 84, 57,220,197, 58, 50,208, 11,241, 28,  3,192, 62,202,
			18,215,153, 24, 76, 41, 15,179, 39, 46, 55,  6,128,167, 23,188,
			106, 34,187,140,164, 73,112,182,244,195,227, 13, 35, 77,196,185,
			26,200,226,119, 31,123,168,125,249, 68,183,230,177,135,160,180,
			12,  1,243,148,102,166, 38,238,251, 37,240,126, 64, 74,161, 40,
			184,149,171,178,101, 66, 29, 59,146, 61,254,107, 42, 86,154,  4,
			236,232,120, 21,233,209, 45, 98,193,114, 78, 19,206, 14,118,127,
			48, 79,147, 85, 30,207,219, 54, 88,234,190,122, 95, 67,143,109,
			137,214,145, 93, 92,100,245,  0,216,186, 60, 83,105, 97,204, 52
			};
	
	public Perlin(long seed){
		_random = new Random(seed);
		InitGradients();
	}
	
	public double getNoise(double x, double y, double z, int octaves){
		double noise = 0;
		
		double amp = 0.5f;
		double fq = 8;
		
		for(int i=0; i<octaves; i++, fq*=2, amp*=0.5){
			noise += amp*Noise(fq*x, fq*y, fq*z);
		}
		
		return noise;
	}
	
	public double Noise(double x, double y, double z){
		int ix = (int)Math.floor(x);
		double fx0 = x - ix;
		double fx1 = fx0 - 1;
		double wx = Smooth(fx0);
	
		int iy = (int)Math.floor(y);
		double fy0 = y - iy;
		double fy1 = fy0 - 1;
		double wy = Smooth(fy0);
	
		int iz = (int)Math.floor(z);
		double fz0 = z - iz;
		double fz1 = fz0 - 1;
		double wz = Smooth(fz0);
	
		double vx0 = Lattice(ix, iy, iz, fx0, fy0, fz0);
		double vx1 = Lattice(ix + 1, iy, iz, fx1, fy0, fz0);
		double vy0 = Lerp(wx, vx0, vx1);
	
		vx0 = Lattice(ix, iy + 1, iz, fx0, fy1, fz0);
		vx1 = Lattice(ix + 1, iy + 1, iz, fx1, fy1, fz0);
		double vy1 = Lerp(wx, vx0, vx1);
	
		double vz0 = Lerp(wy, vy0, vy1);
	
		vx0 = Lattice(ix, iy, iz + 1, fx0, fy0, fz1);
		vx1 = Lattice(ix + 1, iy, iz + 1, fx1, fy0, fz1);
		vy0 = Lerp(wx, vx0, vx1);
	
		vx0 = Lattice(ix, iy + 1, iz + 1, fx0, fy1, fz1);
		vx1 = Lattice(ix + 1, iy + 1, iz + 1, fx1, fy1, fz1);
		vy1 = Lerp(wx, vx0, vx1);
	
		double vz1 = Lerp(wy, vy0, vy1);
		return Lerp(wz, vz0, vz1);
	}
	
	private void InitGradients(){
		for (int i = 0; i < GradientSizeTable; i++){
			double z = 1f - 2f * (double)_random.nextDouble();
			double r = (double)Math.sqrt(1f - z * z);
			double theta = 2 * (double)Math.PI * (double)_random.nextDouble();
			_gradients[i * 3] = r * (double)Math.cos(theta);
			_gradients[i * 3 + 1] = r * (double)Math.sin(theta);
			_gradients[i * 3 + 2] = z;
		}
	}
	
	private int Permutate(int x){
		int mask = GradientSizeTable - 1;
		return _perm[x & mask];
	}
	
	private int Index(int ix, int iy, int iz){
		return Permutate(ix + Permutate(iy + Permutate(iz)));
	}
	
	private double Lattice(int ix, int iy, int iz, double fx, double fy, double fz){
		int index = Index(ix, iy, iz);
		int g = index * 3;
		return _gradients[g] * fx + _gradients[g + 1] * fy + _gradients[g + 2] * fz;
	}
	
	private double Lerp(double t, double value0, double value1){
		return value0 + t * (value1 - value0);
	}
	
	private double Smooth(double x){
		return x * x * (3 - 2 * x);
	}
	
//	public static void main(String[] args) {
//		
//		int WIDTH =  800, HIEGHT = 800;
//		double fq = 4.0f;
//		
//		
//		final BufferedImage bi = new BufferedImage(WIDTH, HIEGHT, BufferedImage.TYPE_INT_RGB);
//		PerlinNoise noise = new PerlinNoise(1);
//		
//		
//		for(int y = 0; y<HIEGHT; y++){
//			for(int x = 0; x<WIDTH; x++){
//				bi.setRGB(x, y, (int)((noise.getNoise(x/(double)WIDTH, y/(double)HIEGHT, 0, 2)+1)*127));
////				System.out.printf("%5f  ", noise.Noise((fq*x)/(double)WIDTH,(fq* y)/(double)HIEGHT, 0));
//			}
////			System.out.println();
//		}
//		
//		JFrame j = new JFrame();
//		j.setLayout(new BorderLayout());
//		JPanel pic = new JPanel(){
//			@Override
//			public void paintComponent(Graphics g){
//				g.drawImage(bi, 0, 0, getWidth(), getHeight(), null);
//			}
//		};
//		
//		pic.setPreferredSize(new Dimension(WIDTH, HIEGHT));
//		
//		
//		j.add(pic, BorderLayout.CENTER);
//		j.pack();
//		j.setLocationRelativeTo(null);
//		j.setVisible(true);
//		
//	}
}
