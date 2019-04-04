package de.amr.demos.graph.pathfinding.controller.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.controller.TopologySelection;

public class Set4NeighborTopology extends PathFinderAction {

	public Set4NeighborTopology(PathFinderController controller) {
		super(controller);
		putValue(Action.NAME, "4 Neighbors");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.changeTopology(TopologySelection._4_NEIGHBORS);
	}
}
