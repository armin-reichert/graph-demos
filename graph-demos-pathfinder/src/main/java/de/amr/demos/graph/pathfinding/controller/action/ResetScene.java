package de.amr.demos.graph.pathfinding.controller.action;

import java.awt.event.ActionEvent;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;

public class ResetScene extends PathFinderAction {

	public ResetScene(PathFinderController controller) {
		super(controller);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.resetScene();
	}
}