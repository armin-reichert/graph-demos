package de.amr.demos.graph.sfc;

import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;
import static de.amr.graph.grid.curves.CurveUtils.traverse;

import java.util.stream.IntStream;

import de.amr.graph.grid.curves.PeanoCurve;
import de.amr.graph.grid.ui.SwingGridSampleApp;

public class PeanoCurveApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new PeanoCurveApp());
	}

	public PeanoCurveApp() {
		super(4 * 243, 4 * 243, 4);
		setAppName("Peano Curve");
	}

	@Override
	public void run() {
		IntStream.rangeClosed(1, 5).forEach(n -> {
			setCellSize(4 * (int) Math.pow(3, 5 - n));
			traverse(new PeanoCurve(n), getGrid(), getGrid().cell(BOTTOM_LEFT), this::addEdge);
			floodFill(BOTTOM_LEFT);
			sleep(1000);
		});
	}
}