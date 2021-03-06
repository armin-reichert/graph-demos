package de.amr.demos.graph.pathfinding.model;

import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BidiAStarSearch;
import de.amr.graph.pathfinder.impl.BidiBreadthFirstSearch;
import de.amr.graph.pathfinder.impl.BidiDijkstraSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;

/**
 * Enum type for available pathfinder algorithms.
 * 
 * @author Armin Reichert
 */
public enum PathFinderAlgorithm {
	BFS("Breadth-First Search", BreadthFirstSearch.class),
	Dijkstra("Dijkstra Search", DijkstraSearch.class),
	GreedyBestFirst("Best-First Search", BestFirstSearch.class),
	AStar("A* Search", AStarSearch.class),
	BidiBFS("Bidirectional Breadth-First Search", BidiBreadthFirstSearch.class),
	BidiDijkstra("Bidirectional Dijkstra Search", BidiDijkstraSearch.class),
	BidiAStar("Bidirectional A* Search", BidiAStarSearch.class);

	private final String displayName;
	private final Class<?> implementation;

	private PathFinderAlgorithm(String name, Class<?> implementation) {
		this.displayName = name;
		this.implementation = implementation;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Class<?> getImplementation() {
		return implementation;
	}

	@Override
	public String toString() {
		return displayName;
	}
}