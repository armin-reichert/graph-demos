package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.RenderingStyle;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.MapView;
import de.amr.demos.graph.pathfinding.view.MapView.PathFinderAnimation;
import de.amr.demos.graph.pathfinding.view.PathFinderView;
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
public class PathFinderController {

	private final PathFinderModel model;
	private PathFinderView pathFinderView;
	private MapView mapView;

	// controller state
	private PathFinderAlgorithm algorithm;
	private ExecutionMode executionMode;
	private int animationDelay;

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			PathFinderAnimation animation = mapView.new PathFinderAnimation();
			animation.setFnDelay(() -> (int) Math.sqrt(animationDelay));
			model.getPathFinder(algorithm).addObserver(animation);
			model.runPathFinder(algorithm);
			model.getPathFinder(algorithm).removeObserver(animation);
			return null;
		}

		@Override
		protected void done() {
			mapView.updateView();
		}
	}

	private void updateViews() {
		if (pathFinderView != null) {
			pathFinderView.updateView();
		}
		if (mapView != null) {
			mapView.updateView();
		}
	}

	private <T> T runTaskAndUpdateView(Supplier<T> task) {
		T result = task.get();
		updateViews();
		return result;
	}

	private void runTaskAndUpdateView(Runnable task) {
		task.run();
		updateViews();
	}

	public PathFinderController(PathFinderModel model) {
		this.model = model;
		algorithm = PathFinderAlgorithm.values()[0];
		executionMode = ExecutionMode.MANUAL;
		animationDelay = 0;
	}

	// end step-wise execution

	public Optional<PathFinderView> getMainView() {
		return Optional.ofNullable(pathFinderView);
	}

	public Optional<MapView> getMapView() {
		return Optional.ofNullable(mapView);
	}

	public void setPathFinderView(PathFinderView view) {
		this.pathFinderView = view;
		pathFinderView.init(model, this);
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
		mapView.init(model, this);
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return algorithm;
	}

	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	public int getMapCellSize() {
		if (mapView != null) {
			return mapView.getCanvas().getCellSize();
		}
		return 8; // TODO
	}

	public void maybeRunPathFinder() {
		switch (executionMode) {
		case MANUAL:
			runTaskAndUpdateView(model::clearResults);
			break;
		case AUTO_SELECTED:
			runTaskAndUpdateView(() -> {
				model.clearResults();
				model.runPathFinder(algorithm);
			});
			break;
		case AUTO_ALL:
			runTaskAndUpdateView(model::runAllPathFinders);
			break;
		default:
			throw new IllegalStateException("Illegal execution mode: " + executionMode);
		}
	}

	// animated execution

	public int getAnimationDelay() {
		return animationDelay;
	}

	public void setAnimationDelay(int animationDelay) {
		this.animationDelay = animationDelay;
	}

	public void runPathFinderAnimation() {
		if (mapView != null) {
			model.clearResult(algorithm);
			mapView.updateView();
			new PathFinderAnimationTask().execute();
		}
	}

	// step-wise execution

	public void startSelectedPathFinder() {
		runTaskAndUpdateView(() -> {
			model.clearResult(algorithm);
			GraphSearch pf = model.getPathFinder(algorithm);
			pf.start(model.getSource(), model.getTarget());
		});
	}

	public Path runSelectedPathFinderSteps(int numSteps) {
		return runTaskAndUpdateView(() -> {
			GraphSearch pf = model.getPathFinder(algorithm);
			if (pf.getState(model.getSource()) == TraversalState.UNVISITED) {
				startSelectedPathFinder();
			}
			for (int n = numSteps; n > 0 && pf.canExplore(); --n) {
				boolean found = pf.exploreVertex();
				if (found) {
					Path path = pf.buildPath(model.getTarget());
					model.storeResult(algorithm, path, 0);
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
		updateMap();
		maybeRunPathFinder();
	}

	public void selectAlgorithm(PathFinderAlgorithm pfa) {
		algorithm = pfa;
		maybeRunPathFinder();
	}

	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		maybeRunPathFinder();
	}

	public void resizeMap(int size) {
		model.resizeMap(size);
		updateMap();
		maybeRunPathFinder();
	}

	public void updateMap() {
		if (mapView != null) {
			mapView.setGrid(model.getMap());
		}
	}

	public void selectTopology(TopologySelection topology) {
		model.setTopology(topology == TopologySelection._4_NEIGHBORS ? Top4.get() : Top8.get());
		updateMap();
		maybeRunPathFinder();
	}

	public void setMapStyle(RenderingStyle style) {
		if (mapView != null) {
			mapView.setStyle(style);
		}
	}

	public void showCost(boolean show) {
		if (mapView != null) {
			mapView.setShowCost(show);
		}
	}

	public void showParent(boolean show) {
		if (mapView != null) {
			mapView.setShowParent(show);
		}
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