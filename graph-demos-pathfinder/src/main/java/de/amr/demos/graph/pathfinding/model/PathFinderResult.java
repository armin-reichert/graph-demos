package de.amr.demos.graph.pathfinding.model;

import java.util.BitSet;

import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;

/**
 * Result of a path-finder run.
 * 
 * @author Armin Reichert
 */
public class PathFinderResult {

	public static final PathFinderResult NO_RESULT = new PathFinderResult(null);

	private final ObservableGraphSearch pathFinder;
	private Path path;
	private final BitSet pathSet;
	private final float runningTimeMillis;
	private final double cost;
	private final long numTouchedVertices;
	private final long numClosedVertices;

	PathFinderResult(ObservableGraphSearch pathFinder) {
		this.pathFinder = pathFinder;
		path = Path.NULL;
		this.pathSet = new BitSet();
		this.runningTimeMillis = 0;
		this.cost = 0;
		this.numTouchedVertices = 0;
		this.numClosedVertices = 0;
	}

	PathFinderResult(ObservableGraphSearch pathFinder, Path path, float runningTimeMillis, double cost,
			long numTouchedVertices, long numClosedVertices) {
		this.pathFinder = pathFinder;
		this.path = path;
		this.pathSet = new BitSet();
		path.forEach(pathSet::set);
		this.runningTimeMillis = runningTimeMillis;
		this.cost = cost;
		this.numTouchedVertices = numTouchedVertices;
		this.numClosedVertices = numClosedVertices;
	}

	public ObservableGraphSearch getPathFinder() {
		return pathFinder;
	}

	public boolean pathContains(int cell) {
		return pathSet.get(cell);
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

	public long getNumTouchedVertices() {
		return numTouchedVertices;
	}

	public long getNumClosedVertices() {
		return numClosedVertices;
	}
}