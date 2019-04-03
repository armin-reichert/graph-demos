package de.amr.demos.graph.pathfinding.controller.action;

import javax.swing.AbstractAction;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;

public abstract class PathFinderAction extends AbstractAction {

	protected final PathFinderController controller;

	public PathFinderAction(PathFinderController controller) {
		this.controller = controller;
	}
}