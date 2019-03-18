package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.util.Optional;

import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.MainView;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * Demo application controller.
 * 
 * @author Armin Reichert
 */
public class Controller {

	private final PathFinderModel model;
	private MainView view;

	private PathFinderAlgorithm selectedAlgorithm;
	private ExecutionMode executionMode;

	private class PathFinderAnimation extends AbstractAnimation implements GraphSearchObserver {

		@Override
		public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
			delayed(() -> view.getCanvasView().drawGridCell(v));
		}

		@Override
		public void edgeTraversed(int either, int other) {
			delayed(() -> {
				view.getCanvasView().drawGridPassage(either, other, true);
			});
		}

		@Override
		public void vertexRemovedFromFrontier(int v) {
			view.getCanvasView().drawGridCell(v);
		}
	}

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			model.newRun(selectedAlgorithm);
			view.getCanvasView().drawGrid();
			PathFinderAnimation animation = new PathFinderAnimation();
			animation.setFnDelay(view::getAnimationDelay);
			model.getPathFinder(selectedAlgorithm).addObserver(animation);
			model.runPathFinder(selectedAlgorithm);
			model.getPathFinder(selectedAlgorithm).removeObserver(animation);
			return null;
		}

		@Override
		protected void done() {
			view.getCanvasView().drawGrid(); // redraw to highlight solution
		}
	}

	public Controller(PathFinderModel model) {
		this.model = model;
		selectedAlgorithm = PathFinderAlgorithm.values()[0];
		executionMode = ExecutionMode.MANUAL;
	}

	public void maybeRunPathFinder() {
		switch (executionMode) {
		case MANUAL:
			model.newRuns();
			updateViewIfPresent();
			break;
		case AUTO_SELECTED:
			runSelectedPathFinder();
			break;
		case AUTO_ALL:
			runAllPathFinders();
			break;
		}
	}

	public void newRuns() {
		model.newRuns();
		updateViewIfPresent();
	}

	public void runSelectedPathFinder() {
		model.runPathFinder(selectedAlgorithm);
		updateViewIfPresent();
	}

	public void runAllPathFinders() {
		model.runAllPathFinders();
		updateViewIfPresent();
	}

	public void runPathFinderAnimation() {
		new PathFinderAnimationTask().execute();
	}

	// begin step-wise execution

	public void startSelectedPathFinder() {
		model.newRun(selectedAlgorithm);
		GraphSearch<?> pf = model.getPathFinder(selectedAlgorithm);
		pf.init();
		pf.start(model.getSource(), model.getTarget());
		updateViewIfPresent();
	}

	public Path runSelectedPathFinderSteps(int numSteps) {
		GraphSearch<?> pf = model.getPathFinder(selectedAlgorithm);
		if (pf.getState(model.getSource()) == TraversalState.UNVISITED) {
			startSelectedPathFinder();
		}
		while (pf.canExplore() && numSteps > 0) {
			if (pf.exploreVertex()) {
				Path path = Path.constructPath(model.getSource(), model.getTarget(), pf);
				model.storeResult(selectedAlgorithm, path, 0);
				updateViewIfPresent();
				return path; // found path
			}
			numSteps -= 1;
		}
		updateViewIfPresent();
		return Path.EMPTY_PATH;
	}

	public Path finishSelectedPathFinder() {
		return runSelectedPathFinderSteps(Integer.MAX_VALUE);
	}

	// end step-wise execution

	public void resetScene() {
		model.clearMap();
		maybeRunPathFinder();
	}

	public void selectAlgorithm(PathFinderAlgorithm algorithm) {
		selectedAlgorithm = algorithm;
		maybeRunPathFinder();
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void setView(MainView view) {
		this.view = view;
	}

	public Optional<MainView> getView() {
		return Optional.ofNullable(view);
	}

	private void updateViewIfPresent() {
		getView().ifPresent(MainView::updateView);
	}

	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	public void resizeMap(int size) {
		model.resizeMap(size);
		view.updateCanvas();
		maybeRunPathFinder();
	}

	public void setTopology(Topology topology) {
		model.setTopology(topology);
		view.updateCanvas();
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