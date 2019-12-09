package de.amr.demos.graph.sfc;

import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;
import static de.amr.graph.grid.api.GridPosition.BOTTOM_RIGHT;
import static de.amr.graph.grid.api.GridPosition.TOP_LEFT;
import static de.amr.graph.grid.api.GridPosition.TOP_RIGHT;
import static de.amr.graph.grid.impl.Grid4Topology.E;
import static de.amr.graph.grid.impl.Grid4Topology.N;
import static de.amr.graph.grid.impl.Grid4Topology.S;
import static de.amr.graph.grid.impl.Grid4Topology.W;
import static de.amr.graph.util.GraphUtils.log;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.curves.HilbertCurve;
import de.amr.graph.grid.ui.SwingGridSampleApp;

/**
 * Creates Hilbert curves of different sizes and shows an animation of the creation and a flood-fill
 * of the underlying graph.
 * 
 * @author Armin Reichert
 */
public class HilbertCurveApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new HilbertCurveApp());
	}

	private HilbertCurveApp() {
		super(512, 512, 256);
		setAppName("Hilbert Curve");
	}

	private int[] getOrientation(GridPosition startPosition) {
		switch (startPosition) {
		case TOP_RIGHT:
			return new int[] { N, E, S, W };
		case TOP_LEFT:
			return new int[] { N, W, S, E };
		case BOTTOM_RIGHT:
			return new int[] { E, S, W, N };
		case BOTTOM_LEFT:
			return new int[] { W, S, E, N };
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void run() {
		Stream.of(TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT).forEach(start -> {
			IntStream.of(256, 128, 64, 32, 16, 8, 4, 2).forEach(cellSize -> {
				setCellSize(cellSize);
				int[] dir = getOrientation(start);
				HilbertCurve hilbert = new HilbertCurve(log(2, getGrid().numCols()), dir[0], dir[1], dir[2], dir[3]);
				hilbert.traverse(getGrid(), getGrid().cell(start), this::addEdge);
				floodFill(start);
				sleep(1000);
			});
		});
	}
}