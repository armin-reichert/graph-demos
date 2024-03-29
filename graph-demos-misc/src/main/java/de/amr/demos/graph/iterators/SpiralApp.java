package de.amr.demos.graph.iterators;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.core.api.TraversalState.VISITED;

import de.amr.graph.grid.traversals.Spiral;
import de.amr.graph.grid.ui.SwingGridSampleApp;

public class SpiralApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new SpiralApp());
	}

	public SpiralApp() {
		super(2);
		setAppName("Spiral");
	}

	@Override
	public void run() {
		getGrid().vertices().forEach(cell -> {
			getGrid().set(cell, COMPLETED);
		});
		Spiral spiral = new Spiral(getGrid());
		Integer prevCell = null;
		for (Integer cell : spiral) {
			getGrid().set(cell, VISITED);
			if (prevCell != null) {
				getGrid().set(prevCell, UNVISITED);
			}
			prevCell = cell;
		}
		sleep(2000);
		System.exit(0);
	}
}
