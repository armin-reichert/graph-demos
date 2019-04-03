package de.amr.demos.graph.pathfinding.controller.action;

import java.awt.event.ActionEvent;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;

public class IncreaseMapeSize extends PathFinderAction {

	public IncreaseMapeSize(PathFinderController controller) {
		super(controller);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int mapSize = controller.getModel().getMapSize() + 1;
		if (mapSize <= PathFinderModel.MAX_MAP_SIZE) {
			controller.changeMapSize(mapSize);
		}

	}
}
