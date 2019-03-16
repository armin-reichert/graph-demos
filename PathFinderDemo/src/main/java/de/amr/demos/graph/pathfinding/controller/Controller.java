package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.MainView;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.TraversalState;

/**
 * Demo application controller.
 * 
 * @author Armin Reichert
 */
public class Controller {

	private final PathFinderModel model;
	private MainView view;

	private PathFinderAlgorithm selectedAlgorithm;
	private boolean autoRunPathFinders;

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
		autoRunPathFinders = false;
	}

	private void maybeRunPathFinder() {
		if (autoRunPathFinders) {
			model.runAllPathFinders();
		} else {
			model.newRun(selectedAlgorithm);
		}
		view.updateView();
	}

	public void newRuns() {
		model.newRuns();
		view.updateView();
	}

	public void runAllPathFinders() {
		model.runAllPathFinders();
		view.updateView();
	}

	public void runPathFinderAnimation() {
		new PathFinderAnimationTask().execute();
	}

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

	public boolean isAutoRunPathFinders() {
		return autoRunPathFinders;
	}

	public void setAutoRunPathFinders(boolean autoRunPathFinders) {
		this.autoRunPathFinders = autoRunPathFinders;
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
		maybeRunPathFinder();
	}

	public void setTarget(int target) {
		model.setTarget(target);
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