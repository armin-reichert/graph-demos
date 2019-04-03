package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.PathFinderDemoApp.MAP_VIEW_SIZE;
import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.RenderingStyle;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.ConfigView;
import de.amr.demos.graph.pathfinding.view.MapView;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;

/**
 * Demo application controller.
 * 
 * @author Armin Reichert
 */
public class PathFinderController {

	private final PathFinderModel model;
	private int leftPathFinderIndex;
	private int rightPathFinderIndex;

	private final ConfigView configurationView;
	private final MapView leftMapView;
	private final MapView rightMapView;

	// controller state
	private RenderingStyle style;
	private ExecutionMode executionMode;
	private int animationDelay;
	private boolean showingCost;
	private boolean showingParent;

	public PathFinderController(PathFinderModel model, int leftPathFinderIndex, int rightPathFinderIndex) {
		this.model = model;
		this.leftPathFinderIndex = leftPathFinderIndex;
		this.rightPathFinderIndex = rightPathFinderIndex;
		style = RenderingStyle.BLOCKS;
		executionMode = ExecutionMode.MANUAL;
		animationDelay = 0;
		showingCost = true;
		showingParent = false;
		configurationView = new ConfigView();
		leftMapView = new MapView();
		leftMapView.init(model, this, this::getLeftPathFinderIndex, MAP_VIEW_SIZE);
		rightMapView = new MapView();
		rightMapView.init(model, this, this::getRightPathFinderIndex, MAP_VIEW_SIZE);
	}

	public int getLeftPathFinderIndex() {
		return leftPathFinderIndex;
	}

	public int getRightPathFinderIndex() {
		return rightPathFinderIndex;
	}

	public ObservableGraphSearch getLeftPathFinder() {
		return model.getPathFinder(leftPathFinderIndex);
	}

	public ObservableGraphSearch getRightPathFinder() {
		return model.getPathFinder(rightPathFinderIndex);
	}

	public void changeLeftPathFinder(int pathFinderIndex) {
		leftPathFinderIndex = pathFinderIndex;
		updatePathFinderResults();
	}

	public void changeRightPathFinder(int pathFinderIndex) {
		rightPathFinderIndex = pathFinderIndex;
		updatePathFinderResults();
	}

	public MapView getLeftMapView() {
		return leftMapView;
	}

	public MapView getRightMapView() {
		return rightMapView;
	}

	public ConfigView getConfigView() {
		return configurationView;
	}

	private void updateViews() {
		configurationView.updateView();
		leftMapView.updateView();
		rightMapView.updateView();
	}

	private void updateMaps() {
		leftMapView.updateMap();
		rightMapView.updateMap();
	}

	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	public RenderingStyle getStyle() {
		return style;
	}

	public void updatePathFinderResults() {
		switch (executionMode) {
		case MANUAL:
			model.clearResult(leftPathFinderIndex);
			model.clearResult(rightPathFinderIndex);
			getLeftPathFinder().start(model.getSource(), model.getTarget());
			getRightPathFinder().start(model.getSource(), model.getTarget());
			break;
		case VISIBLE:
			model.clearResult(leftPathFinderIndex);
			model.clearResult(rightPathFinderIndex);
			leftMapView.runPathFinderAnimation();
			rightMapView.runPathFinderAnimation();
			break;
		case ALL:
			model.runAllPathFinders();
			break;
		}
		updateViews();
	}

	// animated execution

	public int getAnimationDelay() {
		return animationDelay;
	}

	public synchronized void setAnimationDelay(int animationDelay) {
		this.animationDelay = animationDelay;
	}

	public void runPathFinderAnimations() {
		model.clearResult(leftPathFinderIndex);
		model.clearResult(rightPathFinderIndex);
		leftMapView.updateView();
		rightMapView.updateView();
		leftMapView.runPathFinderAnimation();
		rightMapView.runPathFinderAnimation();
	}

	// step-wise execution

	public void startPathFinders() {
		startPathFinder(leftPathFinderIndex);
		startPathFinder(rightPathFinderIndex);
		updateViews();
	}

	private void startPathFinder(int pathFinder) {
		model.clearResult(pathFinder);
		model.getPathFinder(pathFinder).start(model.getSource(), model.getTarget());
	}

	public Path[] runPathFinderSteps(int numSteps) {
		Path leftPath = runPathFinderSteps(leftPathFinderIndex, true, numSteps);
		Path rightPath = runPathFinderSteps(rightPathFinderIndex, false, numSteps);
		return new Path[] { leftPath, rightPath };
	}

	private Path runPathFinderSteps(int i, boolean left, int numSteps) {
		ObservableGraphSearch pathFinder = model.getPathFinder(i);
		for (int n = numSteps; n > 0 && pathFinder.canExplore(); --n) {
			boolean found = pathFinder.exploreVertex();
			if (found) {
				Path path = pathFinder.buildPath(model.getTarget());
				model.setResult(pathFinder, path, 0);
				(left ? leftMapView : rightMapView).updateView();
				return path; // found path
			}
		}
		leftMapView.updateView();
		return Path.NULL;
	}

	public Path[] finishPathFinders() {
		return runPathFinderSteps(Integer.MAX_VALUE);
	}

	// other actions

	public void resetScene() {
		model.clearMap();
		model.clearResults();
		updateMaps();
		updatePathFinderResults();
	}

	public void changeExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		updatePathFinderResults();
	}

	public void changeMapSize(int size) {
		model.setMapSize(size);
		model.clearResults();
		updateMaps();
		updatePathFinderResults();
	}

	public void changeTopology(TopologySelection topology) {
		model.setMapTopology(topology == TopologySelection._4_NEIGHBORS ? Top4.get() : Top8.get());
		model.clearResults();
		updateMaps();
		updatePathFinderResults();
	}

	public void changeStyle(RenderingStyle style) {
		this.style = style;
		updateViews();
	}

	public void showCost(boolean b) {
		this.showingCost = b;
		updateViews();
	}

	public boolean isShowingCost() {
		return showingCost;
	}

	public void showParent(boolean b) {
		this.showingParent = b;
		updateViews();
	}

	public boolean isShowingParent() {
		return showingParent;
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
		setTileAt(cell, model.getMap().get(cell) == WALL ? BLANK : WALL);
	}
}