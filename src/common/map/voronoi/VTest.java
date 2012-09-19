package common.map.voronoi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class VTest {
	public static void main(String args[]){
		
		final int _WIDTH = 800;
		final int _HEIGHT = 800;
		
		JFrame j = new JFrame();
		j.setLayout(new BorderLayout());
		JPanel pic = new JPanel(){
			@Override
			public void paintComponent(Graphics g){
				Random rand = new Random(32);
				
				g.translate(20, 20);
				
				List<common.map.worldgenerator.Point> points = new ArrayList<common.map.worldgenerator.Point>();
				
				for(int i=0; i<2000; i++){
					points.add(new common.map.worldgenerator.Point(((rand.nextDouble() * (_WIDTH - 20)) + 10), ((rand.nextDouble() * (_HEIGHT - 20)) + 10)));
				}

				double[] xValues = new double[points.size()];
				double[] yValues = new double[points.size()];
				for(int i=0; i< points.size(); i++){
					xValues[i] = points.get(i).x;
					yValues[i] = points.get(i).y;
				}
				Voronoi v = new Voronoi(1);
				
				g.setColor(Color.RED);
//				g.fillOval(5, 5, 5, 5);
				
//				for(Point p : points){
//					System.out.println(p.toString());
//				}
//				for(int i= 0 ; i<points.size(); i++){
//					System.out.println(xValues[i] +" : "+  yValues[i]);
//					
//				}
				
				for(common.map.worldgenerator.Point p : points){
					g.fillOval((int)p.x-1, (int)p.y-1, 3, 3);
				}
				
				List<GraphEdge> graph = v.generateVoronoi(xValues, yValues, 0, _WIDTH, 0, _HEIGHT);
				System.out.println(graph.size());
				
				for(GraphEdge e : graph){
					//if(e.x1==e.x2 && e.y1==e.y2){continue;}
					
					g.setColor(Color.BLACK);
					g.fillOval((int)e.x1-2, (int)e.y1-2, 5, 5);
					g.fillOval((int)e.x2-2, (int)e.y2-2, 5, 5);
					g.drawLine((int)e.x1, (int)e.y1, (int)e.x2, (int)e.y2);
//					g.setColor(Color.BLUE);
//					g.drawLine((int)points.get(e.site1).x, (int)points.get(e.site1).y, (int)points.get(e.site2).x, (int)points.get(e.site2).y);
					
					
					if(e.x1==0 || e.y1 == 0 || e.x2==0 || e.y2==0){
						System.out.println(e);
						g.setColor(Color.RED);
						g.drawLine((int)e.x1, (int)e.y1, (int)e.x2, (int)e.y2);
					}
					
					if(e.x1==_WIDTH || e.y1 == _HEIGHT || e.x2==_WIDTH || e.y2==_HEIGHT){
						System.out.println(e);
						g.setColor(Color.RED);
						g.drawLine((int)e.x1, (int)e.y1, (int)e.x2, (int)e.y2);
					}
				}
				
			}
		};
		
		pic.setPreferredSize(new Dimension(_WIDTH, _HEIGHT));
		
		
		j.add(pic, BorderLayout.CENTER);
		j.pack();
		j.setLocationRelativeTo(null);
		j.setVisible(true);
		j.repaint();
	}
}
