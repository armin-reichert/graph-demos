package de.amr.demos.graph.pathfinding.controller.action;

import java.awt.event.ActionEvent;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.controller.TopologySelection;

public class Set8NeighborTopology extends PathFinderAction {

	public Set8NeighborTopology(PathFinderController controller) {
		super(controller);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.changeTopology(TopologySelection._8_NEIGHBORS);
	}
}