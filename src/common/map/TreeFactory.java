package common.map;

import java.util.ArrayList;
import java.util.List;

import initial3d.engine.Mesh;
import initial3d.engine.MeshLOD;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;
import common.entity.GameObject;

public class TreeFactory {
	
	private Perlin perlin; 
	
	public TreeFactory(Long seed){
		perlin = new Perlin(seed);
	}
	
	public GameObject getTree(Vec3 pos){
		double trunkLength = 10 * (perlin.getNoise(pos.x, 0, pos.z, 8)+1);
		double xWidth = perlin.getNoise(pos.x, 0.3, pos.z, 8)+1;
		double zWidth = perlin.getNoise(pos.x, 0.7, pos.z, 8)+1;
		
		Tree tree = new Tree(Vec3.create(xWidth, trunkLength, zWidth), pos, System.nanoTime());
		
		List<Mesh> meshes = new ArrayList<Mesh>();
		meshes.add(trunkMesh(trunkLength, xWidth, zWidth));
		tree.setMeshContexts(meshes);
		
		return tree;
	}
	
	public Mesh trunkMesh(double trunkLength, double xWidth, double zWidth){
		final double branchSpace = 0.5;
		
		int numBranch = (int)(trunkLength / branchSpace);
		
		MeshLOD mlod = new MeshLOD(4*numBranch+1, 3, 5*numBranch+1, 1, 2, 1);
		
		Vec3 base = Vec3.create(0, -(trunkLength/2), 0);

		addBranch(mlod, base, base.neg(), 10 * branchSpace);
		
		for(int i=1; i<numBranch; i++){
			Vec3 branchBase = base.add(0, i*branchSpace, 0);
			Vec3 branchEnd = branchBase.add((
					Math.random()-0.5)*xWidth *10 * numBranch/(double)i, 
					3*Math.random()*branchSpace, 
					(Math.random()-0.5)*zWidth*10 * numBranch/(double)i);
			
			addBranch(mlod, branchBase, branchEnd, 10 * branchSpace*(i/(double)numBranch));
		}
		
		Mesh mesh = new Mesh();
		mesh.add(mlod);

		return mesh;
	}
	
	private void addBranch(MeshLOD mlod,  Vec3 str, Vec3 fin, double width){
		width /=2;
		int[] ver = new int[5];
		
		//create the point
		ver[0] = mlod.addVertex(fin.x, fin.y, fin.z);
		
		//create the four points on base
		ver[1] = mlod.addVertex(str.x, str.y, str.z-width);
		ver[2] = mlod.addVertex(str.x-width, str.y, str.z);
		ver[3] = mlod.addVertex(str.x, str.y, str.z+width);
		ver[4] = mlod.addVertex(str.x+width, str.y, str.z);
		
		mlod.addPolygon(new int[] { ver[0], ver[1], ver[2]}, null, null, null);
		mlod.addPolygon(new int[] { ver[0], ver[2], ver[3]}, null, null, null);
		mlod.addPolygon(new int[] { ver[0], ver[3], ver[4]}, null, null, null);
		mlod.addPolygon(new int[] { ver[0], ver[4], ver[1]}, null, null, null);
	}
	
	public class Tree extends GameObject{
		public Tree(Vec3 _radius, Vec3 _position, long id){
			super(_radius, _position, Quat.one, id);
		}
	}
}






















