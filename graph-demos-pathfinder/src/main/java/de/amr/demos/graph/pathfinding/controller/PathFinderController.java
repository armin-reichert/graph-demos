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
	private RenderingStyle style;
	private boolean showCost;
	private boolean showParent;

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			PathFinderAnimation animation = mapView.new PathFinderAnimation();
			animation.setFnDelay(() -> (int) Math.sqrt(animationDelay));
			model.runPathFinder(algorithm, animation);
			return null;
		}

		@Override
		protected void done() {
			mapView.updateView();
		}
	}

	public PathFinderController(PathFinderModel model) {
		this.model = model;
		algorithm = PathFinderAlgorithm.values()[0];
		style = RenderingStyle.BLOCKS;
		executionMode = ExecutionMode.MANUAL;
		animationDelay = 0;
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

	public RenderingStyle getStyle() {
		return style;
	}

	public void updatePathFinderResults() {
		switch (executionMode) {
		case MANUAL:
			model.clearResult(algorithm);
			break;
		case AUTO_SELECTED:
			model.runPathFinder(algorithm);
			break;
		case AUTO_ALL:
			model.runAllPathFinders();
			break;
		default:
			throw new IllegalStateException("Illegal execution mode: " + executionMode);
		}
		updateViews();
	}

	// animated execution

	public int getAnimationDelay() {
		return animationDelay;
	}

	public void setAnimationDelay(int animationDelay) {
		this.animationDelay = animationDelay;
	}

	public void runPathFinderAnimation() {
		model.clearResult(algorithm);
		mapView.updateView();
		new PathFinderAnimationTask().execute();
	}

	// step-wise execution

	public void startSelectedPathFinder() {
		model.clearResult(algorithm);
		GraphSearch pf = model.getPathFinder(algorithm);
		pf.start(model.getSource(), model.getTarget());
		updateViews();
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
					model.setResult(algorithm, path, 0);
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
		updatePathFinderResults();
	}

	public void selectAlgorithm(PathFinderAlgorithm algorithm) {
		this.algorithm = algorithm;
		model.clearResult(algorithm);
		model.getPathFinder(algorithm).start(model.getSource(), model.getTarget());
		updatePathFinderResults();
	}

	public void selectExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		updatePathFinderResults();
	}

	public void selectMapSize(int size) {
		model.setMapSize(size);
		model.clearResults();
		updateMap();
		updatePathFinderResults();
	}

	public void updateMap() {
		if (mapView != null) {
			mapView.updateMap(model.getMap());
		}
	}

	public void selectTopology(TopologySelection topology) {
		model.setMapTopology(topology == TopologySelection._4_NEIGHBORS ? Top4.get() : Top8.get());
		updateMap();
		updatePathFinderResults();
	}

	public void selectStyle(RenderingStyle style) {
		this.style = style;
		updateViews();
	}

	public void showCost(boolean show) {
		this.showCost = show;
		updateViews();
	}

	public boolean isShowCost() {
		return showCost;
	}

	public void showParent(boolean show) {
		this.showParent = show;
		updateViews();
	}

	public boolean isShowParent() {
		return showParent;
	}

	public void setSource(int source) {
		model.setSource(source);
		updatePathFinderResults();
	}

	public void setTarget(int target) {
		model.setTarget(target);
		updatePathFinderResults();
	}

	public void setTileAt(int cell, Tile tile) {
		if (cell != model.getSource() && cell != model.getTarget()) {
			model.setMapContent(cell, tile);
			updatePathFinderResults();
		}
	}

	public void flipTileAt(int cell) {
		Tile flippedTile = model.getMap().get(cell) == WALL ? BLANK : WALL;
		setTileAt(cell, flippedTile);
	}
}