package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.PathFinderDemoApp.MAP_VIEW_SIZE;
import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.awt.Point;

import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.RenderingStyle;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.ConfigView;
import de.amr.demos.graph.pathfinding.view.ConfigWindow;
import de.amr.demos.graph.pathfinding.view.MapView;
import de.amr.demos.graph.pathfinding.view.MapsWindow;
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

	private final ConfigView configView;
	private ConfigWindow configWindow;

	private MapsWindow mapsWindow;
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
		configView = new ConfigView();
		leftMapView = new MapView();
		rightMapView = new MapView();
	}

	public void createAndshowUI() {
		configView.init(model, this);
		leftMapView.init(model, this, this::getLeftPathFinderIndex, MAP_VIEW_SIZE);
		rightMapView.init(model, this, this::getRightPathFinderIndex, MAP_VIEW_SIZE);

		configWindow = new ConfigWindow(configView);
		configWindow.pack();

		mapsWindow = new MapsWindow(this, leftMapView, rightMapView);
		mapsWindow.pack();

		configWindow.setLocation(5, 5);
		configWindow.setVisible(true);

		Point right = configWindow.getLocation();
		right.move(configWindow.getWidth(), 5);
		mapsWindow.setLocation(right);
		mapsWindow.setVisible(true);
	}

	public PathFinderModel getModel() {
		return model;
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
		mapsWindow.updateWindow();
	}

	public void changeRightPathFinder(int pathFinderIndex) {
		rightPathFinderIndex = pathFinderIndex;
		updatePathFinderResults();
		mapsWindow.updateWindow();
	}

	public MapView getLeftMapView() {
		return leftMapView;
	}

	public MapView getRightMapView() {
		return rightMapView;
	}

	public ConfigView getConfigView() {
		return configView;
	}

	private void updateViews() {
		configView.updateView();
		leftMapView.updateView();
		rightMapView.updateView();
		mapsWindow.updateWindow();
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

	public void runBothFirstStep() {
		runSingleFirstStep(leftPathFinderIndex);
		runSingleFirstStep(rightPathFinderIndex);
		updateViews();
	}

	public void runBothNumSteps(int numSteps) {
		runSingleNumSteps(leftMapView, model.getPathFinder(leftPathFinderIndex), numSteps);
		runSingleNumSteps(rightMapView, model.getPathFinder(rightPathFinderIndex), numSteps);
	}

	public void runBothRemainingSteps() {
		runBothNumSteps(Integer.MAX_VALUE);
	}

	private void runSingleFirstStep(int pathFinderIndex) {
		model.clearResult(pathFinderIndex);
		model.getPathFinder(pathFinderIndex).start(model.getSource(), model.getTarget());
	}

	private Path runSingleNumSteps(MapView mapView, ObservableGraphSearch pathFinder, int numSteps) {
		Path path = pathFinder.buildPath(model.getTarget());
		for (int n = numSteps; n > 0 && path == Path.NULL && pathFinder.canExplore(); --n) {
			if (pathFinder.exploreVertex()) {
				path = pathFinder.buildPath(model.getTarget());
				break;
			}
		}
		model.setResult(pathFinder, path, 0);
		mapView.updateView();
		return path;
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