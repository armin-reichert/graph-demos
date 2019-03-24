package de.amr.demos.graph.pathfinding.model;

import java.util.BitSet;

import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;

/**
 * Represents of a path finder run.
 * 
 * @author Armin Reichert
 */
public class PathFinderRun {

	private final ObservableGraphSearch pathFinder;
	private Path path;
	private final BitSet pathCells;
	private final float runningTimeMillis;
	private final double cost;
	private final long numOpenVertices;
	private final long numClosedVertices;

	PathFinderRun(ObservableGraphSearch pathFinder) {
		this.pathFinder = pathFinder;
		path = Path.EMPTY_PATH;
		this.pathCells = new BitSet();
		this.runningTimeMillis = 0;
		this.cost = 0;
		this.numOpenVertices = 0;
		this.numClosedVertices = 0;
	}

	PathFinderRun(ObservableGraphSearch pathFinder, Path path, float runningTimeMillis, double cost,
			long numOpenVertices, long numClosedVertices) {
		this.pathFinder = pathFinder;
		this.path = path;
		this.pathCells = new BitSet();
		path.forEach(pathCells::set);
		this.runningTimeMillis = runningTimeMillis;
		this.cost = cost;
		this.numOpenVertices = numOpenVertices;
		this.numClosedVertices = numClosedVertices;
	}

	public ObservableGraphSearch getPathFinder() {
		return pathFinder;
	}

	public boolean pathContains(int cell) {
		return pathCells.get(cell);
	}

	public float getRunningTimeMillis() {
		return runningTimeMillis;
	}

	public int getPathLength() {
		return path.numEdges();
	}

	public double getCost() {
		return cost;
	}

	public long getNumOpenVertices() {
		return numOpenVertices;
	}

	public long getNumClosedVertices() {
		return numClosedVertices;
	}
}