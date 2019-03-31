package de.amr.demos.graph.pathfinding.model;

import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BidiAStarSearch;
import de.amr.graph.pathfinder.impl.BidiBreadthFirstSearch;
import de.amr.graph.pathfinder.impl.BidiDijkstraSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;
import de.amr.util.StopWatch;

/**
 * Model for path finder demo application.
 * 
 * @author Armin Reichert
 */
public class PathFinderModel {

	public static final int MIN_MAP_SIZE = 2;
	public static final int MAX_MAP_SIZE = 316;

	private GridGraph<Tile, Double> map;
	private int source;
	private int target;
	private Map<PathFinderAlgorithm, PathFinderResult> results = new EnumMap<>(PathFinderAlgorithm.class);

	public PathFinderModel(int mapSize, Topology topology) {
		newMap(mapSize, topology);
		source = map.cell(mapSize / 4, mapSize / 2);
		target = map.cell(mapSize * 3 / 4, mapSize / 2);
	}

	private ObservableGraphSearch newPathFinder(PathFinderAlgorithm algorithm) {
		switch (algorithm) {
		case AStar:
			return new AStarSearch(map, (u, v) -> map.getEdgeLabel(u, v), map::euclidean);
		case BFS:
			return new BreadthFirstSearch(map, map::euclidean);
		case Dijkstra:
			return new DijkstraSearch(map, (u, v) -> map.getEdgeLabel(u, v));
		case GreedyBestFirst:
			return new BestFirstSearch(map, v -> map.euclidean(v, target), map::euclidean);
		case BidiBFS:
			return new BidiBreadthFirstSearch(map, map::euclidean);
		case BidiAStar:
			return new BidiAStarSearch(map, (u, v) -> map.getEdgeLabel(u, v), map::euclidean, map::euclidean);
		case BidiDijkstra:
			return new BidiDijkstraSearch(map, map::euclidean);
		}
		throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
	}

	private void newMap(int mapSize, Topology topology) {
		GridGraph<Tile, Double> oldMap = map;
		map = new GridGraph<>(mapSize, mapSize, topology, v -> null, (u, v) -> 0.0, UndirectedEdge::new);
		map.setDefaultVertexLabel(cell -> Tile.BLANK);
		map.setDefaultEdgeLabel(map::euclidean);
		map.fill();
		if (oldMap == null) {
			return;
		}

		float scalingFactor = (float) map.numCols() / oldMap.numCols();
		// copy walls into map, keep aspect ratio
		for (int oldCell = 0; oldCell < oldMap.numVertices(); ++oldCell) {
			int oldRow = oldMap.row(oldCell), oldCol = oldMap.col(oldCell);
			int row = scaledCoord(oldRow, scalingFactor), col = scaledCoord(oldCol, scalingFactor);
			if (map.isValidRow(row) && map.isValidCol(col)) {
				setMapContent(map.cell(col, row), oldMap.get(oldCell));
			}
		}

		boolean mapped = false;
		for (GridPosition pos : GridPosition.values()) {
			if (pos == GridPosition.CENTER) {
				continue;
			}
			if (source == oldMap.cell(pos)) {
				source = map.cell(pos);
				mapped = true;
				break;
			}
		}
		if (!mapped) {
			int sourceCol = scaledCoord(oldMap.col(source), scalingFactor),
					sourceRow = scaledCoord(oldMap.row(source), scalingFactor);
			if (map.isValidCol(sourceCol) && map.isValidRow(sourceRow)) {
				source = map.cell(sourceCol, sourceRow);
			}
			else {
				source = 0;
			}
		}

		mapped = false;
		for (GridPosition pos : GridPosition.values()) {
			if (pos == GridPosition.CENTER) {
				continue;
			}
			if (target == oldMap.cell(pos)) {
				target = map.cell(pos);
				mapped = true;
				break;
			}
		}
		if (!mapped) {
			int targetCol = scaledCoord(oldMap.col(target), scalingFactor),
					targetRow = scaledCoord(oldMap.row(target), scalingFactor);
			if (map.isValidCol(targetCol) && map.isValidRow(targetRow)) {
				target = map.cell(targetCol, targetRow);
			}
			else {
				target = map.numVertices() - 1;
			}
		}

		if (source == 0 && target == 0) {
			target = map.numVertices() - 1;
		}

		setMapContent(source, Tile.BLANK);
		setMapContent(target, Tile.BLANK);
	}

	private static int scaledCoord(int coord, float scaling) {
		return (int) (scaling * coord);
	}

	public GridGraph<Tile, Double> getMap() {
		return map;
	}

	public Topology getMapTopology() {
		return map.getTopology();
	}

	public void setMapTopology(Topology topology) {
		if (topology != map.getTopology()) {
			newMap(map.numRows(), topology);
		}
	}

	public int getMapSize() {
		return map.numRows();
	}

	public void setMapSize(int mapSize) {
		if (mapSize != map.numRows()) {
			newMap(mapSize, map.getTopology());
		}
	}

	public void clearMap() {
		map.clearVertexLabels();
	}

	public void setMapContent(int cell, Tile tile) {
		Objects.requireNonNull(tile);
		map.set(cell, tile);
		switch (tile) {
		case BLANK:
			map.neighbors(cell).filter(neighbor -> map.get(neighbor) != WALL)
					.filter(neighbor -> !map.adjacent(cell, neighbor)).forEach(neighbor -> map.addEdge(cell, neighbor));
			break;
		case WALL:
			map.neighbors(cell).filter(neighbor -> map.get(neighbor) != WALL)
					.filter(neighbor -> map.adjacent(cell, neighbor))
					.forEach(neighbor -> map.removeEdge(cell, neighbor));
			break;
		default:
			throw new IllegalArgumentException("Unknown tile " + tile);
		}
	}

	public Optional<PathFinderResult> getResult(PathFinderAlgorithm algorithm) {
		return Optional.ofNullable(results.get(algorithm));
	}

	public void setResult(PathFinderAlgorithm algorithm, Path path, float timeMillis) {
		ObservableGraphSearch pathFinder = getPathFinder(algorithm);
		long touched = map.vertices().filter(v -> pathFinder.getState(v) != TraversalState.UNVISITED).count();
		long closed = map.vertices().filter(v -> pathFinder.getState(v) == TraversalState.COMPLETED).count();
		results.put(algorithm,
				new PathFinderResult(pathFinder, path, timeMillis, pathFinder.getCost(target), touched, closed));
	}

	public void clearResults() {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			clearResult(algorithm);
		}
	}

	public void clearResult(PathFinderAlgorithm algorithm) {
		ObservableGraphSearch pathFinder = newPathFinder(algorithm);
		results.put(algorithm, new PathFinderResult(pathFinder));
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public ObservableGraphSearch getPathFinder(PathFinderAlgorithm algorithm) {
		if (!results.containsKey(algorithm)) {
			clearResult(algorithm);
		}
		return results.get(algorithm).getPathFinder();
	}

	public void runAllPathFinders() {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			runPathFinder(algorithm);
		}
	}

	public void runPathFinder(PathFinderAlgorithm algorithm, GraphSearchObserver observer) {
		clearResult(algorithm);
		ObservableGraphSearch pathFinder = getPathFinder(algorithm);
		if (observer != null) {
			pathFinder.addObserver(observer);
		}
		StopWatch watch = new StopWatch();
		watch.start();
		Path path = pathFinder.findPath(source, target);
		watch.stop();
		if (observer != null) {
			pathFinder.removeObserver(observer);
		}
		setResult(algorithm, path, watch.getNanos() / 1_000_000f);

	}

	public void runPathFinder(PathFinderAlgorithm algorithm) {
		runPathFinder(algorithm, null);
	}
}