package de.amr.demos.graph.sfc;

import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;

import java.util.stream.IntStream;

import de.amr.graph.grid.curves.PeanoCurve;
import de.amr.graph.grid.ui.SwingGridSampleApp;

public class PeanoCurveApp extends SwingGridSampleApp {

	static final int MAX_DEPTH = 5;
	static final int MIN_CELL_SIZE = 4;

	static int cellSize(int depth) {
		return MIN_CELL_SIZE * (int) Math.pow(3, depth);
	}

	public static void main(String[] args) {
		launch(new PeanoCurveApp());
	}

	public PeanoCurveApp() {
		super(cellSize(MAX_DEPTH), cellSize(MAX_DEPTH), MIN_CELL_SIZE);
		setAppName("Peano Curve");
	}

	@Override
	public void run() {
		IntStream.rangeClosed(1, MAX_DEPTH).forEach(depth -> {
			setCellSize(cellSize(MAX_DEPTH - depth));
			new PeanoCurve(depth).traverse(getGrid(), getGrid().cell(BOTTOM_LEFT), this::addEdge);
			floodFill(BOTTOM_LEFT);
			sleep(1000);
		});
	}
}