package de.amr.demos.graph.pathfinding.model;

import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BidiAStar;
import de.amr.graph.pathfinder.impl.BidiBFS;
import de.amr.graph.pathfinder.impl.BidiDijkstra;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;

public enum PathFinderAlgorithm {
	BFS("Breadth-First Search", BreadthFirstSearch.class),
	GreedyBestFirst("Best-First Search", BestFirstSearch.class),
	Dijkstra("Dijkstra", DijkstraSearch.class),
	AStar("A* Search", AStarSearch.class),
	BidiBFS("Bidirectional BFS", BidiBFS.class),
	BidiAStar("Bidirectional A*", BidiAStar.class),
	BidiDijkstra("Bidirectional Dijkstra", BidiDijkstra.class);

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