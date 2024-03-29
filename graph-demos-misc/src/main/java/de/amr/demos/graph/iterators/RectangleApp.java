package de.amr.demos.graph.iterators;

import static de.amr.graph.core.api.TraversalState.COMPLETED;

import de.amr.graph.grid.shapes.Rectangle;
import de.amr.graph.grid.traversals.ExpandingRectangle;
import de.amr.graph.grid.ui.SwingGridSampleApp;

public class RectangleApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new RectangleApp());
	}

	public RectangleApp() {
		super(2);
		setAppName("Rectangles");
	}

	@Override
	public void run() {
		Rectangle startRectangle = new Rectangle(getGrid(), getGrid().cell(0, 0), 1, 1);
		ExpandingRectangle expRect = new ExpandingRectangle(startRectangle);
		expRect.setExpandHorizontally(true);
		expRect.setExpandVertically(true);
		expRect.setExpansionRate(1);
		expRect.setMaxExpansion(getGrid().numCols());
		for (Integer cell : expRect) {
			getGrid().set(cell, COMPLETED);
		}
		sleep(2000);
		System.exit(0);
	}
}
