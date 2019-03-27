package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.CanvasView.PathFinderAnimation;
import de.amr.demos.graph.pathfinding.view.MainView;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.pathfinder.api.GraphSearch;
import de.amr.graph.pathfinder.api.Path;

/**
 * Demo application controller.
 * 
 * @author Armin Reichert
 */
public class Controller {

	private final PathFinderModel model;
	private MainView mainView;

	private PathFinderAlgorithm selectedAlgorithm;
	private ExecutionMode executionMode;

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			mainView.getCanvasView().ifPresent(canvasView -> {
				PathFinderAnimation animation = canvasView.createAnimation();
				animation.setFnDelay(mainView::getAnimationDelay);
				model.getPathFinder(selectedAlgorithm).addObserver(animation);
				model.runPathFinder(selectedAlgorithm);
				model.getPathFinder(selectedAlgorithm).removeObserver(animation);
			});
			return null;
		}

		@Override
		protected void done() {
			// redraw canvas to show path
			mainView.getCanvasView().ifPresent(canvas -> canvas.updateView());
		}
	}

	private <T> T runTaskAndUpdateView(Supplier<T> task) {
		T result = task.get();
		if (mainView != null) {
			mainView.updateMainView();
		}
		return result;
	}

	private void runTaskAndUpdateView(Runnable task) {
		task.run();
		if (mainView != null) {
			mainView.updateMainView();
		}
	}

	public Controller(PathFinderModel model) {
		this.model = model;
		selectedAlgorithm = PathFinderAlgorithm.values()[0];
		executionMode = ExecutionMode.MANUAL;
	}

	// end step-wise execution

	public Optional<MainView> getMainView() {
		return Optional.ofNullable(mainView);
	}

	public void setMainView(MainView view) {
		this.mainView = view;
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	public void maybeRunPathFinder() {
		switch (executionMode) {
		case MANUAL:
			runTaskAndUpdateView(model::newRuns);
			break;
		case AUTO_SELECTED:
			runTaskAndUpdateView(() -> model.runPathFinder(selectedAlgorithm));
			break;
		case AUTO_ALL:
			runTaskAndUpdateView(model::runAllPathFinders);
			break;
		default:
			throw new IllegalStateException("Illegal execution mode: " + executionMode);
		}
	}

	// animated execution

	public void runPathFinderAnimation() {
		model.newRun(selectedAlgorithm);
		getMainView().ifPresent(mainView -> mainView.updateMainView());
		new PathFinderAnimationTask().execute();
	}

	// step-wise execution

	public void startSelectedPathFinder() {
		runTaskAndUpdateView(() -> {
			model.newRun(selectedAlgorithm);
			GraphSearch pf = model.getPathFinder(selectedAlgorithm);
			pf.start(model.getSource(), model.getTarget());
		});
	}

	public Path runSelectedPathFinderSteps(int numSteps) {
		return runTaskAndUpdateView(() -> {
			GraphSearch pf = model.getPathFinder(selectedAlgorithm);
			if (pf.getState(model.getSource()) == TraversalState.UNVISITED) {
				startSelectedPathFinder();
			}
			for (int n = numSteps; n > 0 && pf.canExplore(); --n) {
				boolean found = pf.exploreVertex();
				if (found) {
					Path path = pf.buildPath(model.getTarget());
					model.storeResult(selectedAlgorithm, path, 0);
					return path; // found path
				}
			}
			return Path.NULL;
		});
	}

	public Path finishSelectedPathFinder() {
		return runSelectedPathFinderSteps(Integer.MAX_VALUE);
	}

	// other actions

	public void resetScene() {
		model.clearMap();
		mainView.updateCanvasView();
		maybeRunPathFinder();
	}

	public void selectAlgorithm(PathFinderAlgorithm algorithm) {
		selectedAlgorithm = algorithm;
		maybeRunPathFinder();
	}

	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		maybeRunPathFinder();
	}

	public void resizeMap(int size) {
		model.resizeMap(size);
		mainView.updateCanvasView();
		maybeRunPathFinder();
	}

	public void selectTopology(TopologySelection topology) {
		model.setTopology(topology == TopologySelection._4_NEIGHBORS ? Top4.get() : Top8.get());
		mainView.updateCanvasView();
		maybeRunPathFinder();
	}

	public void setSource(int source) {
		model.setSource(source);
		model.newRuns();
		maybeRunPathFinder();
	}

	public void setTarget(int target) {
		model.setTarget(target);
		model.newRuns();
		maybeRunPathFinder();
	}

	public void setTileAt(int cell, Tile tile) {
		if (cell != model.getSource() && cell != model.getTarget()) {
			model.setTile(cell, tile);
			maybeRunPathFinder();
		}
	}

	public void flipTileAt(int cell) {
		Tile flippedTile = model.getMap().get(cell) == WALL ? BLANK : WALL;
		setTileAt(cell, flippedTile);
	}
}