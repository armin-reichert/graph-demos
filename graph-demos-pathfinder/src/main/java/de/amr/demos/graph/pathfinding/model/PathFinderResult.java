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

	public static final PathFinderResult NO_RESULT = new PathFinderResult(null, "");

	private final ObservableGraphSearch pathFinder;
	private final String pathFinderName;
	private Path path;
	private BitSet pathSet;
	private float runningTimeMillis;
	private double cost;
	private long numTouchedVertices;
	private long numClosedVertices;

	public PathFinderResult(ObservableGraphSearch pathFinder, String pathFinderName) {
		this.pathFinder = pathFinder;
		this.pathFinderName = pathFinderName;
		clear();
	}

	public void clear() {
		path = Path.NULL;
		this.pathSet = new BitSet();
		this.runningTimeMillis = 0;
		this.cost = 0;
		this.numTouchedVertices = 0;
		this.numClosedVertices = 0;
	}

	public ObservableGraphSearch getPathFinder() {
		return pathFinder;
	}

	public String getPathFinderName() {
		return pathFinderName;
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

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
		pathSet.clear();
		path.forEach(pathSet::set);
	}

	public void setRunningTimeMillis(float runningTimeMillis) {
		this.runningTimeMillis = runningTimeMillis;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setNumTouchedVertices(long numTouchedVertices) {
		this.numTouchedVertices = numTouchedVertices;
	}

	public void setNumClosedVertices(long numClosedVertices) {
		this.numClosedVertices = numClosedVertices;
	}
}