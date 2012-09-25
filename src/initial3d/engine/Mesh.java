package initial3d.engine;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Mesh extends AbstractList<MeshLOD> {

	// please don't mutate objects after giving them to the scenemanager

	private List<MeshLOD> meshlods = new ArrayList<MeshLOD>();

	public Mesh() {

	}

	@Override
	public boolean add(MeshLOD m) {
		return meshlods.add(m);
	}

	@Override
	public MeshLOD get(int arg0) {
		return meshlods.get(arg0);
	}

	@Override
	public int size() {
		return meshlods.size();
	}

	@Override
	public Iterator<MeshLOD> iterator() {
		return meshlods.iterator();
	}

}
