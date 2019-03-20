package de.amr.demos.graph.pathfinding.controller;

public enum TopologySelection {

	_4_NEIGHBORS, _8_NEIGHBORS;

	@Override
	public String toString() {
		return this == _4_NEIGHBORS ? "4 Neighbors" : "8 Neighbors";
	}
}
