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
public class Controller {

	private final PathFinderModel model;
	private PathFinderView pathFinderView;
	private MapView mapView;

	private PathFinderAlgorithm selectedAlgorithm;
	private ExecutionMode executionMode;

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			if (mapView != null) {
				PathFinderAnimation animation = mapView.createAnimation();
				animation.setFnDelay(pathFinderView::getAnimationDelay);
				model.getPathFinder(selectedAlgorithm).addObserver(animation);
				model.runPathFinder(selectedAlgorithm);
				model.getPathFinder(selectedAlgorithm).removeObserver(animation);
			}
			return null;
		}

		@Override
		protected void done() {
			// redraw map to show path
			if (mapView != null) {
				mapView.updateView();
			}
		}
	}

	private <T> T runTaskAndUpdateView(Supplier<T> task) {
		T result = task.get();
		if (pathFinderView != null) {
			pathFinderView.updateView();
		}
		return result;
	}

	private void runTaskAndUpdateView(Runnable task) {
		task.run();
		if (pathFinderView != null) {
			pathFinderView.updateView();
		}
		if (mapView != null) {
			mapView.updateView();
		}
	}

	public Controller(PathFinderModel model) {
		this.model = model;
		selectedAlgorithm = PathFinderAlgorithm.values()[0];
		executionMode = ExecutionMode.MANUAL;
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
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return selectedAlgorithm;
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
				model.runPathFinder(selectedAlgorithm);
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

	public void runPathFinderAnimation() {
		model.clearResult(selectedAlgorithm);
		if (mapView != null) {
			mapView.updateView();
		}
		new PathFinderAnimationTask().execute();
	}

	// step-wise execution

	public void startSelectedPathFinder() {
		runTaskAndUpdateView(() -> {
			model.clearResult(selectedAlgorithm);
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
		updateMap();
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