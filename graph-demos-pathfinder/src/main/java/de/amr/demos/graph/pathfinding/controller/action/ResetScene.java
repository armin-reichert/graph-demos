package de.amr.demos.graph.pathfinding.controller.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;

public class ResetScene extends PathFinderAction {

	public ResetScene(PathFinderController controller) {
		super(controller);
		putValue(Action.NAME,	"Reset Scene");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.resetScene();
	}
}