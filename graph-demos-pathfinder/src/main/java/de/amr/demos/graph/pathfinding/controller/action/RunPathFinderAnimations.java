package de.amr.demos.graph.pathfinding.controller.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;

public class RunPathFinderAnimations extends PathFinderAction {

	public RunPathFinderAnimations(PathFinderController controller) {
		super(controller);
		putValue(Action.NAME, "Run Path Finder Animation");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.runPathFinderAnimations();
	}
}