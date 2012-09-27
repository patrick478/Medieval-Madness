/* Map generation project
 * <http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/>
 * Copyright 2010 Amit J Patel <amitp@cs.stanford.edu>
 * 
 * licensed under the MIT Open Source license
 * <http://www.opensource.org/licenses/mit-license.php>
 * 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including 
 * without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to 
 * the following conditions:
 *   
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software. 
 *   
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */

package common.map.worldgenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import static common.map.worldgenerator.Biome.*;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import common.map.Perlin;
import common.map.voronoi.*;




public class MapGenerator {

	public static final int NUM_POINTS = 2000;
	// 0 to 1, fraction of water corners for water polygon
	public static final double LAKE_THRESHOLD = 0.3; 
	public static final int NUM_LLOYD_ITERATIONS = 2;

	// Passed in by the caller:
	public final int SIZE;
	public final long SEED;

	// Island shape is controlled by the islandRandom seed and the
	// type of island, passed in when we set the island shape. The
	// islandShape function uses both of them to determine whether any
	// point should be water or land. TODO change description
	public final IslandStrategy islandShape;
	public final Random mapRandom;

	// These store the graph data
	public List<Center> centers = new ArrayList<Center>();
	public List<Corner> corners = new ArrayList<Corner>();
	public List<Edge> edges = new ArrayList<Edge>();
	

	public MapGenerator(long seed, int size) {
		mapRandom = new Random(seed);
		this.SIZE = size;
		this.SEED = seed;

		this.islandShape = new PerlinIsland(seed);
	}
	
	public List<Triangle> getTriangles(){
		List<Triangle> tri = new ArrayList<Triangle>();
		for(Center c : centers){
			for(Edge e : c.borders){
				tri.add(new Triangle(
						new Point[]{c.point, e.v0.point, e.v1.point}, 
						new double[]{c.elevation, e.v0.elevation, e.v1.elevation}
				));
//				System.out.println(tri.get(tri.size()-1).toString());
			}
		}
		return tri;
	}

	public void run() {
		/* Random points */
		List<Point> points = generateRandomPoints();
		improveRandomPoints(points);

		/*Building Graph*/
		buildGraph(points);
		improveCorners();
		points = null;

		/*GetHeights*/
		islandShape.assignCornerElevations(corners);
		assignOceanCoastAndLand();
		// Assign elevations to non-land corners
		redistributeElevations(landCorners(corners));
		//change coast and ocean to zero
		for (Corner q : corners) {
			if (q.ocean || q.coast) {
				q.elevation = 0.0;
			}
		}
		assignPolygonElevations();

		/*Work out rivers*/
		calculateDownslopes();
		calculateWatersheds();
		createRivers();

		/*Assign Moisture and Biome*/
		assignCornerMoisture();
		redistributeMoisture(landCorners(corners));
		assignPolygonMoisture();
		for (Center p : centers) {
			p.biome = getBiome(p);
		}
	}

	/** 
	 * Takes a list of points on the map and uses the voronoi
	 * class to return a list of graph edges representing
	 * the voronoi diagram for those points 
	 */
	private List<GraphEdge> createVoronoi(List<Point> points) {
		double[] xValues = new double[points.size()];
		double[] yValues = new double[points.size()];

		for (int i = 0; i < points.size(); i++) {
			xValues[i] = points.get(i).x;
			yValues[i] = points.get(i).y;
		}

		Voronoi v = new Voronoi(0.01);
		return v.generateVoronoi(xValues, yValues, 0, SIZE, 0, SIZE);
	}

	/** 
	 * Generate random points and assign them to be on the island or
	 * in the water. Some water points are inland lakes; others are
	 * ocean. We'll determine ocean later by looking at what's
	 * connected to ocean. 
	 */
	public List<Point> generateRandomPoints() {
		List<Point> points = new ArrayList<Point>();

		for (int i = 0; i < NUM_POINTS; i++) {
			points.add(new Point(nextInRange(10, SIZE - 10), nextInRange(10,
					SIZE - 10)));
		}
		return points;
	}

	/** 
	 * Provides a psuedo random number including the lower bound
	 * up to but not including the upper bound 
	 */
	private double nextInRange(int low, int high) {
		return (mapRandom.nextDouble() * (high - low)) + low;
	}

	/** 
	 * Improve the random set of points with Lloyd Relaxation.
	 * which moves each point to the centroid of the
	 * generated Voronoi polygon, then generates Voronoi again. 
	 */
	public void improveRandomPoints(List<Point> points) {
		
		for (int i = 0; i < NUM_LLOYD_ITERATIONS; i++) {
			List<GraphEdge> gEdges = createVoronoi(points);
			for (int r = 0; r < points.size(); r++) {
				Point p = points.get(r);
				Set<Point> region = new HashSet<Point>();

				// populate the set of points surrounding the center
				for (GraphEdge e : gEdges) {
					if (e.site1 == r || e.site2 == r) {
						region.add(new Point((int) e.x1, (int) e.y1));
						region.add(new Point((int) e.x2, (int) e.y2));
					}
				}

				p.x = 0;
				p.y = 0;
				for (Point q : region) {
					p.x += q.x;
					p.y += q.y;
				}
				p.x /= region.size();
				p.y /= region.size();
			}
		}
	}

	/** 
	 * Build graph data structure in 'edges', 'centers', 'corners',
	 * based on information in the Voronoi results: point.neighbors
	 * will be a list of neighboring points of the same type (corner
	 * or center); point.edges will be a list of edges that include
	 * that point. Each edge connects to four points: the Voronoi edge
	 * edge.{v0,v1} and its dual Delaunay triangle edge edge.{d0,d1}.
	 * For boundary polygons, the Delaunay edge will have one null
	 * point, and the Voronoi edge may be null. 
	 */
	public void buildGraph(List<Point> points) {

		// Build Center objects for each of the points
		for (Point point : points) {
			Center p = new Center();
			p.index = centers.size();
			p.point = point;
			p.neighbors = new ArrayList<Center>();
			p.borders = new ArrayList<Edge>();
			p.corners = new ArrayList<Corner>();
			centers.add(p);
		}

		// The Voronoi library generates lines from point to point.
		// To centralize edges on one "corner" we get a map of corners
		HashMap<Point, Corner> cornerMap = new HashMap<Point, Corner>();

		for (GraphEdge m : createVoronoi(points)) {
			// don't want polygons on the outside to be connected
			// via a voronoi edge of length 0
			if (m.x1 == m.x2 && m.y1 == m.y2) {
				continue;
			}

			// Fill the graph data. Make an Edge object corresponding to
			// the edge from the voronoi library.
			Edge edge = new Edge();
			edge.index = edges.size();
			edge.river = 0;
			edges.add(edge);
			edge.midpoint = new Point((m.x1 + m.x2) / 2, (m.y1 + m.y2) / 2);

			// Edges point to corners. Edges point to centers.
			edge.v0 = makeCorner(cornerMap, new Point(m.x1, m.y1));
			edge.v1 = makeCorner(cornerMap, new Point(m.x2, m.y2));

			// m.sitex refers to the index of the point passed ie, the edge
			edge.d0 = centers.get(m.site1);
			edge.d1 = centers.get(m.site2);

			// Centers point to edges. Corners point to edges.
			if (edge.d0 != null) {
				edge.d0.borders.add(edge);
			}
			if (edge.d1 != null) {
				edge.d1.borders.add(edge);
			}
			if (edge.v0 != null) {
				edge.v0.protrudes.add(edge);
			}
			if (edge.v1 != null) {
				edge.v1.protrudes.add(edge);
			}

			// Centers point to centers.
			if (edge.d0 != null && edge.d1 != null) {
				addToCenterList(edge.d0.neighbors, edge.d1);
				addToCenterList(edge.d1.neighbors, edge.d0);
			}

			// Corners point to corners
			if (edge.v0 != null && edge.v1 != null) {
				addToCornerList(edge.v0.adjacent, edge.v1);
				addToCornerList(edge.v1.adjacent, edge.v0);
			}

			// Centers point to corners
			if (edge.d0 != null) {
				addToCornerList(edge.d0.corners, edge.v0);
				addToCornerList(edge.d0.corners, edge.v1);
			}
			if (edge.d1 != null) {
				addToCornerList(edge.d1.corners, edge.v0);
				addToCornerList(edge.d1.corners, edge.v1);
			}

			// Corners point to centers
			if (edge.v0 != null) {
				addToCenterList(edge.v0.touches, edge.d0);
				addToCenterList(edge.v0.touches, edge.d1);
			}
			if (edge.v1 != null) {
				addToCenterList(edge.v1.touches, edge.d0);
				addToCenterList(edge.v1.touches, edge.d1);
			}
		}

		// //TODO patch of a fix
		// for(Corner c : corners){
		// if (c.adjacent.size() == 1 && !c.border){
		// Center n = c.touches.get(0);
		// for(Corner r : n.corners){
		// if (r.adjacent.size() < 3 && !r.border){
		//
		//
		//
		// break;
		// }
		// }
		// }
		// }

	}

	// TODO what is this doing exactly, so what should i do...
	private Corner makeCorner(Map<Point, Corner> cornerMap, Point point) {
		Corner q = cornerMap.get(point);
		if (q != null) {
			return q;
		}
		q = new Corner();
		q.index = corners.size();

		// add this corner to global corners
		// and index it by its point
		corners.add(q);
		cornerMap.put(point, q);

		q.point = point;
		q.border = (point.x == 0 || point.x == SIZE || point.y == 0 || point.y == SIZE);
		q.touches = new ArrayList<Center>();
		q.protrudes = new ArrayList<Edge>();
		q.adjacent = new ArrayList<Corner>();

		return q;
	}

	private void addToCornerList(List<Corner> v, Corner x) {
		if (x != null && v.indexOf(x) < 0) {
			v.add(x);
		}
	}

	private void addToCenterList(List<Center> v, Center x) {
		if (x != null && v.indexOf(x) < 0) {
			v.add(x);
		}
	}

	/**
	 * The corners are moved to the average of the polygon centers around them.
	 * Short edges become longer. Long edges tend to become shorter. The
	 * polygons tend to be more uniform after this step.
	 */
	public void improveCorners() {
		// Although Lloyd relaxation improves the uniformity of polygon
		// sizes, it doesn't help with the edge lengths. Short edges can
		// be bad for some games, and lead to weird artifacts on
		// rivers. We can easily lengthen short edges by moving the
		// corners, but **we lose the Voronoi property**.

		Point[] newCorners = new Point[corners.size()];

		// First we compute the average of the centers next to each corner.
		for (Corner q : corners) {
			if (q.border) {
				newCorners[q.index] = q.point;
			} else {
				Point point = new Point(0, 0);
				for (Center r : q.touches) {
					point.x += r.point.x;
					point.y += r.point.y;
				}
				point.x /= q.touches.size();
				point.y /= q.touches.size();
				newCorners[q.index] = point;
			}
		}

		// Move the corners to the new locations.
		for (int i = 0; i < corners.size(); i++) {
			corners.get(i).point = newCorners[i];
		}

		// The edge midpoints were computed for the old corners and need
		// to be recomputed.
		for (Edge edge : edges) {
			double midX = (edge.v0.point.x + edge.v1.point.x) / 2;
			double midY = (edge.v0.point.y + edge.v1.point.y) / 2;
			edge.midpoint = new Point(midX, midY);
		}
	}

	/** 
	 * Create an array of corners that are on land only, for use by
	 * algorithms that work only on land. We return an array instead
	 * of a vector because the redistribution algorithms want to sort
	 * this array using Array.sortOn.
	 */
	public List<Corner> landCorners(List<Corner> corners) {
		List<Corner> locations = new ArrayList<Corner>();
		for (Corner q : corners) {
			if (!q.ocean && !q.coast) {
				locations.add(q);
			}
		}
		return locations;
	}

	/** 
	 * Change the overall distribution of elevations so that lower
	 * elevations are more common than higher
	 * elevations. Specifically, we want elevation X to have frequency
	 * (1-X). To do this we will sort the corners, then set each
	 * corner to its desired elevation. 
	 */
	public void redistributeElevations(List<Corner> locations) {
		// SCALE_FACTOR increases the mountain area. At 1.0 the maximum
		// elevation barely shows up on the map, so we set it to 1.1.
		double SCALE_FACTOR = 1.1;

		// locations.sortOn('elevation', Array.NUMERIC);//comparator TODO
		Collections.sort(locations, new Comparator<Corner>() {
			@Override
			public int compare(Corner o1, Corner o2) {
				return (int) ((o1.elevation - o2.elevation) * 1000);
				// TODO right comparable variable?
			}
		});

		for (int i = 0; i < locations.size(); i++) {
			// Let y(x) be the total area that we want at elevation <= x.
			// We want the higher elevations to occur less than lower
			// ones, and set the area to be y(x) = 1 - (1-x)^2.
			double y = (double) i / (locations.size() - 1);

			// Now we have to solve for x, given the known y.
			// * y = 1 - (1-x)^2
			// * y = 1 - (1 - 2x + x^2)
			// * y = 2x - x^2
			// * x^2 - 2x + y = 0
			// From this we can use the quadratic equation to get:
			double x = Math.sqrt(SCALE_FACTOR)
					- Math.sqrt(SCALE_FACTOR * (1 - y));
			if (x > 1.0)
				x = 1.0; // TODO: does this break downslopes?
			locations.get(i).elevation = x;
		}

		// finally we make the lakes flat and record them TODO record lakes for
		// what?
		List<HashSet<Center>> lakes = new ArrayList<HashSet<Center>>();

		// TODO???
		for (Center c : centers) {
			// if c is a lake of sorts
			if (!c.ocean && c.water) {
				boolean isLake = false;// flag that center is already in a lake

				for (HashSet<Center> lake : lakes) {
					if (isLake = lake.contains(c)) {
						break;
					}
				}

				if (isLake) {
					lakes.add(createLake(c));
				}

			}
		}
	}

	/** Helper method to return the lake set of a given polygon */
	private HashSet<Center> createLake(Center c) {
		// else create a new lake with it, and all its neighbors
		HashSet<Center> newLake = new HashSet<Center>();
		double lowest = Double.MAX_VALUE;

		// set up the list of neighbours to be traversed
		List<Center> neighbours = new ArrayList<Center>();
		neighbours.add(c);

		// traverse all neighours that are also connected lakes
		while (!neighbours.isEmpty()) {
			Center next = neighbours.remove(0);

			for (Center neigh : next.neighbors) {
				// if its a lake and it's not already in the new lake
				// add it and record it's neighbors and corners
				if (!c.ocean && c.water && !newLake.contains(neigh)) {
					neighbours.add(neigh);
					newLake.add(neigh);
					for(Corner k : neigh.corners){
						lowest = (k.elevation<lowest) ? k.elevation : lowest;
					}
				}
			}
		}
		return newLake;
	}

	/**
	 * Change the overall distribution of moisture to be evenly distributed.
	 */
	public void redistributeMoisture(List<Corner> locations) {
		Collections.sort(locations, new Comparator<Corner>() {
			@Override
			public int compare(Corner o1, Corner o2) {
				return (int) ((o1.moisture - o2.moisture) * 1000);
			}
		});

		for (int i = 0; i < locations.size(); i++) {
			locations.get(i).moisture = (double) i / (locations.size() - 1);
		}
	}

	/**
	 * Determine polygon and corner types: ocean, coast, land.
	 */
	public void assignOceanCoastAndLand() {
		// Compute polygon attributes 'ocean' and 'water' based on the
		// corner attributes. Count the water corners per
		// polygon. Oceans are all polygons connected to the edge of the
		// map. In the first pass, mark the edges of the map as ocean;
		// in the second pass, mark any water-containing polygon
		// connected an ocean as ocean.
		Queue<Center> queue = new ArrayDeque<Center>();

		for (Center p : centers) {
			int numWater = 0;
			for (Corner q : p.corners) {
				if (q.border) {
					p.border = true;
					p.ocean = true;
					q.water = true;
					queue.add(p);
				}
				if (q.water) {
					numWater += 1;
				}
			}
			p.water = (p.ocean || numWater >= p.corners.size() * LAKE_THRESHOLD);
		}
		while (!queue.isEmpty()) {
			Center p = queue.poll();
			for (Center r : p.neighbors) {
				if (r.water && !r.ocean) {
					r.ocean = true;
					queue.add(r);
				}
			}
		}

		// Set the polygon attribute 'coast' based on its neighbors. If
		// it has at least one ocean and at least one land neighbor,
		// then this is a coastal polygon.
		for (Center p : centers) {
			int numOcean = 0;
			int numLand = 0;
			for (Center r : p.neighbors) {
				numOcean += (r.ocean) ? 1 : 0;
				numLand += (!r.water) ? 1 : 0;
			}
			p.coast = (numOcean > 0) && (numLand > 0);
		}

		// Set the corner attributes based on the computed polygon
		// attributes. If all polygons connected to this corner are
		// ocean, then it's ocean; if all are land, then it's land;
		// otherwise it's coast.
		for (Corner q : corners) {
			int numOcean = 0;
			int numLand = 0;
			for (Center p : q.touches) {
				numOcean += (p.ocean) ? 1 : 0;
				numLand += (!p.water) ? 1 : 0;
			}
			q.ocean = (numOcean == q.touches.size());
			q.coast = (numOcean > 0) && (numLand > 0);
			q.water = q.border || ((numLand != q.touches.size()) && !q.coast);
		}
	}

	// Polygon elevations are the average of the elevations of their corners.
	public void assignPolygonElevations() {
		for (Center p : centers) {
			double sumElevation = 0.0;
			for (Corner q : p.corners) {
				sumElevation += q.elevation;
			}
			p.elevation = sumElevation / p.corners.size();
		}
	}

	// Calculate downslope pointers. At every point, we point to the
	// point downstream from it, or to itself. This is used for
	// generating rivers and watersheds.
	public void calculateDownslopes() {
		for (Corner q : corners) {
			Corner r = q; // default case
			for (Corner s : q.adjacent) {
				if (s.elevation <= r.elevation) {
					r = s;
				}
			}
			q.downslope = r;
		}
	}

	/** 
	 * Calculate the watershed of every land point. The watershed is
	 * the last downstream land point in the downslope graph.
	 * watersheds are currently calculated on corners, but it'd be
	 * more useful to compute them on polygon centers so that every
	 * polygon can be marked as being in one watershed. 
	 */
	public void calculateWatersheds() {

		// Initially the watershed pointer points downslope one step.
		for (Corner q : corners) {
			q.watershed = q;
			if (!q.ocean && !q.coast) {
				q.watershed = q.downslope;
			}
		}
		// Follow the downslope pointers to the coast. Limit to 100
		// iterations although most of the time with NUM_POINTS=2000 it
		// only takes 20 iterations because most points are not far from
		// a coast. TODO: can run faster by looking at
		// p.watershed.watershed instead of p.downslope.watershed.
		for (int i = 0; i < 100; i++) {
			boolean changed = false;
			for (Corner q : corners) {
				if (!q.ocean && !q.coast && !q.watershed.coast) {
					Corner r = q.downslope.watershed;
					if (!r.ocean)
						q.watershed = r;
					changed = true;
				}
			}
			if (!changed)
				break;
		}
		// How big is each watershed?
		for (Corner q : corners) {
			Corner r = q.watershed;
			r.watershed_size = 1 + Math.max(r.watershed_size, 0);
		}
	}

	/** 
	 * Create rivers along edges. Pick a random corner point, then
	 * move downslope. Mark the edges and corners as rivers. 
	 */
	public void createRivers() {
		for (int i = 0; i < SIZE / 2; i++) {
			Corner q = corners.get((int) nextInRange(0, corners.size() - 1));
			if (q.ocean || q.elevation < 0.3 || q.elevation > 0.9)
				continue;

			// Bias rivers to go west: if (q.downslope.x > q.x) continue;
			while (!q.coast) {
				if (q == q.downslope) {
					break;
				}
				Edge edge = lookupEdgeFromCorner(q, q.downslope);
				edge.river = edge.river + 1;
				q.river = Math.max(q.river, 0) + 1;
				q.downslope.river = Math.max(q.downslope.river, 0) + 1;
				// TODO: fix double count
				q = q.downslope;
			}
		}
	}

	/** 
	 * Calculate moisture. Freshwater sources spread moisture: rivers
	 * and lakes (not oceans). Saltwater sources have moisture but do
	 * not spread it (we set it at the end, after propagation). 
	 */
	public void assignCornerMoisture() {
		Queue<Corner> queue = new ArrayDeque<Corner>();
		// Fresh water
		for (Corner q : corners) {
			if ((q.water || q.river > 0) && !q.ocean) {
				q.moisture = q.river > 0 ? Math.min(3.0, (0.2 * q.river)) : 1.0;
				queue.add(q);
			} else {
				q.moisture = 0.0;
			}
		}
		while (!queue.isEmpty()) {
			Corner q = queue.poll();

			for (Corner r : q.adjacent) {
				double newMoisture = q.moisture * 0.9;
				if (newMoisture > r.moisture) {
					r.moisture = newMoisture;
					queue.add(r);
				}
			}
		}
		// Salt water
		for (Corner q : corners) {
			if (q.ocean || q.coast) {
				q.moisture = 1.0;
			}
		}
	}

	// Polygon moisture is the average of the moisture at corners
	public void assignPolygonMoisture() {
		for (Center p : centers) {
			double sumMoisture = 0.0;
			for (Corner q : p.corners) {
				// ceiling value for all corner water values
				if (q.moisture > 1.0) {
					q.moisture = 1.0;
				}
				sumMoisture += q.moisture;
			}
			p.moisture = sumMoisture / p.corners.size();
		}
	}

	/** 
	 * Assign a biome type to each polygon. If it has
	 * ocean/coast/water, then that's the biome; otherwise it depends
	 * on low/high elevation and low/medium/high moisture. This is
	 * roughly based on the Whittaker diagram but adapted to fit the
	 * needs of the island map generator.
	 */
	static public Biome getBiome(Center p) {
		if (p.ocean) {
			return OCEAN;
		} else if (p.water) {
			if (p.elevation < 0.1)
				return MARSH;
			if (p.elevation > 0.8)
				return ICE;
			return LAKE;
		} else if (p.coast) {// TODO change this to only lower down is beach
			return BEACH;

			// High elevation
		} else if (p.elevation > 0.8) {
			if (p.moisture > 0.50)
				return SNOW;
			else if (p.moisture > 0.33)
				return TUNDRA;
			else if (p.moisture > 0.16)
				return BARE;
			else
				return SCORCHED;

			// Relatively high
		} else if (p.elevation > 0.6) {
			if (p.moisture > 0.66)
				return TAIGA;
			else if (p.moisture > 0.33)
				return SHRUBLAND;
			else
				return TEMPERATE_DESERT;

			// Medium
		} else if (p.elevation > 0.3) {
			if (p.moisture > 0.83)
				return TEMPERATE_RAIN_FOREST;
			else if (p.moisture > 0.50)
				return TEMPERATE_DECIDUOUS_FOREST;
			else if (p.moisture > 0.16)
				return GRASSLAND;
			else
				return TEMPERATE_DESERT;

			// Quite low
		} else {
			if (p.moisture > 0.66)
				return TROPICAL_RAIN_FOREST;
			else if (p.moisture > 0.33)
				return TROPICAL_SEASONAL_FOREST;
			else if (p.moisture > 0.16)
				return GRASSLAND;
			else
				return SUBTROPICAL_DESERT;
		}
	}

	// Look up a Voronoi Edge object given two adjacent Voronoi
	// polygons, or two adjacent Voronoi corners
	public Edge lookupEdgeFromCenter(Center p, Center r) {
		for (Edge edge : p.borders) {
			if (edge.d0 == r || edge.d1 == r)
				return edge;
		}
		return null;
	}

	public Edge lookupEdgeFromCorner(Corner q, Corner s) {
		for (Edge edge : q.protrudes) {
			if (edge.v0 == s || edge.v1 == s)
				return edge;
		}
		return null;
	}

	/**
	 * Determines the shape and distribution of elevations of the corners of the
	 * map to create the base shape and structure.
	 */
	private abstract class IslandStrategy {

		/**
		 * Determine whether a given point should be on the island or in the
		 * water.
		 */
		protected abstract boolean inside(Point p);

		/**
		 * Determine elevations and water at Voronoi corners. By construction,
		 * we have no local minima. This is important for the downslope vectors
		 * later, which are used in the river construction algorithm. Also by
		 * construction, inlets/bays push low elevation areas inland, which
		 * means many rivers end up flowing out through them. Also by
		 * construction, lakes often end up on river paths because they don't
		 * raise the elevation as much as other terrain does.
		 */
		public abstract void assignCornerElevations(List<Corner> points);
	}

	/**
	 * An implementation of Island Shape that uses normal distribution to create
	 * a round island in the middle of the map altered by Perlin noise to create
	 * inlets, bays and lakes.
	 */
	public class PerlinIsland extends IslandStrategy {
		private Perlin perlin;

		public PerlinIsland(long seed) {
			perlin = new Perlin(seed);
		}

		@Override
		protected boolean inside(Point p) {
			final double SD_ISLAND = 3.5;
			final double SD_MAP_EDGE = 2.7;

			double x = SD_ISLAND * ((2d * p.x / SIZE) - 1);
			double y = SD_ISLAND * ((2d * p.y / SIZE) - 1);

			double fg = normalDistribution(SD_ISLAND);

			// z = (1/sqrt(2PI)) e^(-(x^2 + y^2) / 2)
			double noiseBarrier = normalDistribution(Math.hypot(x, y))
					- (normalDistribution(Math.hypot(x, y) * 1.4) - fg) * 2
					+ fg;

			double z = normalDistribution(Math.hypot(x, y));

			double noise = perlin.getNoise(p.x / SIZE, p.y / SIZE, 0, 8);

			return z - noise * noiseBarrier * 10 > normalDistribution(SD_MAP_EDGE);

			// n*(1-z)
		}

		/* Helper function to determine z value at given point */
		private double normalDistribution(double sd) {
			return (1 / Math.sqrt(2 * Math.PI))
					* Math.pow(Math.E, -(sd * sd) / 2);
		}

		// @Override
		// public void assignCornerElevations(List<Corner> points) {
		// Queue<Corner> queue = new ArrayDeque<Corner>();
		//
		// for (Corner q : points) {
		// q.water = !inside(q.point);
		// }
		//
		// for (Corner q : points) {
		// // The edges of the map are elevation 0
		// if (q.border) {
		// q.elevation = 0.0;
		// queue.add(q);
		// } else {
		// q.elevation = Double.POSITIVE_INFINITY;
		// }
		// }
		//
		// Perlin noise = new Perlin(SEED);
		//
		// // Traverse the graph and assign elevations to each point. As we
		// // move away from the map border, increase the elevations. This
		// // guarantees that rivers always have a way down to the coast by
		// // going downhill (no local minima).
		// while (!queue.isEmpty()) {
		// Corner q = queue.poll();
		//
		// for (Corner s : q.adjacent) {
		// // Every step up is epsilon over water or 1 over land. The
		// // number doesn't matter because we'll rescale the
		// // elevations later.
		// double newElevation = 0.01 + q.elevation;
		// if (!q.water && !s.water) {
		// double perlin = noise.getNoise(s.point.x / SIZE, s.point.y / SIZE, 0,
		// 1);
		// newElevation += 1;//((perlin>0) ? perlin*2 : 0.5);//TODO originally
		// +=1
		// }
		// // If this point changed, we'll add it to the queue so
		// // that we can process its neighbors too.
		// if (newElevation < s.elevation) {
		// s.elevation = newElevation;
		// queue.add(s);
		// }
		// }
		// }
		// }

		@Override
		public void assignCornerElevations(List<Corner> points) {

			// build a priority queue with least elevated at the head
			Queue<Corner> queue = new PriorityQueue<Corner>(SIZE / 10,
					new Comparator<Corner>() {
						@Override
						public int compare(Corner o1, Corner o2) {
							return (int) ((o1.elevation - o2.elevation) * 1000);
						}
					});

			for (Corner q : points) {
				q.water = !inside(q.point);
			}

			for (Corner q : points) {
				// Set the edges of the map are elevation 0 and add to queue
				if (q.border) {
					q.elevation = -1d;
					queue.add(q);
				} else {
					q.elevation = Double.POSITIVE_INFINITY;
				}
			}

			Perlin noise = new Perlin(SEED);

			// Traverse the graph and assign elevations to each point. As we
			// move away from the map border, increase the elevations. This
			// guarantees that rivers always have a way down to the coast by
			// going downhill (no local minima).
			while (!queue.isEmpty()) {
				Corner q = queue.poll();

				for (Corner s : q.adjacent) {
					// Every step up is epsilon over water or 1 over land. The
					// number doesn't matter because we'll rescale the
					// elevations later.
					double newElevation = 0.01 + q.elevation;
					if (!q.water && !s.water) {
						double perlin = noise.getNoise(s.point.x / SIZE,
								s.point.y / SIZE, 0, 1);
						newElevation += ((perlin > 0) ? perlin * 100 : 0.5);
					}

					// If this point changed, we'll add it to the queue so
					// that we can process its neighbors too.
					if (newElevation < s.elevation) {
						s.elevation = newElevation;
						queue.add(s);
					}
				}
			}
		}

	}

	public void look() {

		JFrame j = new JFrame();
		j.setLayout(new BorderLayout());
		JPanel pic = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				paintc(g);
			}
		};
		pic.setPreferredSize(new Dimension(SIZE, SIZE));
		j.add(pic, BorderLayout.CENTER);
		j.pack();
		j.setLocationRelativeTo(null);
		j.setVisible(true);
		j.repaint();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void paintc(Graphics g) {
		for (Center c : centers) {

			// int[] xValues = new int[c.corners.size()];
			// int[] yValues = new int[c.corners.size()];
			//
			//
			// for(int i=0; i< c.corners.size(); i++){
			// xValues[i] = c.corners.get(i).point.x;
			// yValues[i] = c.corners.get(i).point.y;
			// }
			//
			// if(c.water){g.setColor(Color.BLUE);}
			// else{g.setColor(Color.LIGHT_GRAY);}
			// g.fillPolygon(xValues, yValues, c.corners.size());

			for (Corner o : c.corners) {
				for (Corner r : c.corners) {

					if (r.adjacent.contains(o)) {

//						 if (c.water) {
//						 g.setColor(Color.BLUE);
//						 } else {
//						 g.setColor(new Color((int) (255 * c.elevation),
//						 (int) (255 * c.elevation),
//						 (int) (255 * c.elevation)));
//						 }

						g.setColor(c.biome.color);

						// System.out.println(c.biome);

						g.fillPolygon(new int[] { (int) c.point.x,
								(int) o.point.x, (int) r.point.x }, new int[] {
								(int) c.point.y, (int) o.point.y,
								(int) r.point.y }, 3);

					}
				}

				// g.setColor(Color.black);
				// g.drawString(String.format("%s",
				// c.biome.toString().substring(0, 1)),
				// (int) c.point.x-3, (int) c.point.y+4);// TODO getting zeros
				// here
			}

			// for(int i=0; i< c.corners.size(); i++){
			// g.drawLine(c.point.x, c.point.y, c.corners.get(i).point.x,
			// c.corners.get(i).point.y);
			// }

			// g.setColor(Color.BLACK);
			// g.fillOval((int)c.point.x, (int)c.point.y, 3, 3);

		}

		for (Edge e : edges) {
			if (e.river > 0) {
				g.setColor(Color.BLUE);
			} else {
				g.setColor(Color.BLACK);
			}
			g.drawLine((int) e.v0.point.x, (int) e.v0.point.y,
					(int) e.v1.point.x, (int) e.v1.point.y);
		}

		// testing for consistency of final graph
//		g.setColor(Color.RED);
		
//		for (Corner c : corners) {
////			g.setColor(Color.RED);
//			if (c.adjacent.size() <= 1) {
////				for(Center k : c.touches){
////					g.drawLine((int)c.point.x, (int)c.point.y, (int)k.point.x, (int)k.point.y);
////				}
//				g.setColor(Color.BLACK);
//				
//			} else if (c.adjacent.size() == 2) {
//				g.setColor(Color.BLUE);
//			} else if (c.adjacent.size() == 3) {
//				g.setColor(Color.GREEN);
//			} else if (c.adjacent.size() == 4) {
//				g.setColor(Color.ORANGE);
//			} else if (c.adjacent.size() == 5){
//				g.setColor(Color.RED);
//			} else {
//				g.setColor(Color.WHITE);
//			}
//			g.fillOval((int) c.point.x - 1, (int) c.point.y - 1, 3, 3);
//		}

	}

	public static void main(String[] args) {
		MapGenerator mp = new MapGenerator(42, 800);
		mp.run();
		mp.look();
	}
}
