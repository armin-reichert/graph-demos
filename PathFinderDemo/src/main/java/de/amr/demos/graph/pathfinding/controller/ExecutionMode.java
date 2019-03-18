package de.amr.demos.graph.pathfinding.controller;

public enum ExecutionMode {

	MANUAL, AUTO_SELECTED, AUTO_ALL;

	@Override
	public String toString() {
		switch (this) {
		case AUTO_ALL:
			return "Auto-run all";
		case AUTO_SELECTED:
			return "Auto-run selected";
		case MANUAL:
			return "Run manually";
		default:
			return "";
		}
	}

}
