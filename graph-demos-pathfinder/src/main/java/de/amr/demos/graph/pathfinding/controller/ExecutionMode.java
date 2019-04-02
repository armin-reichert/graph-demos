package de.amr.demos.graph.pathfinding.controller;

public enum ExecutionMode {

	MANUAL, VISIBLE, ALL;

	@Override
	public String toString() {
		switch (this) {
		case MANUAL:
			return "Run manually";
		case VISIBLE:
			return "Auto-run displayed pathfinders";
		case ALL:
			return "Auto-run all pathfinders";
		default:
			return "";
		}
	}

}
