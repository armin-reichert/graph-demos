package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.ConfigView;
import de.amr.demos.graph.pathfinding.view.ConfigWindow;
import de.amr.demos.graph.pathfinding.view.MapView;
import de.amr.demos.graph.pathfinding.view.MapView.PathFinderAnimation;
import de.amr.demos.graph.pathfinding.view.MapsWindow;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.Grid8Topology;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;
import de.amr.util.StopWatch;

/**
 * Demo application controller.
 * 
 * @author Armin Reichert
 */
public class PathFinderController {

	private final PathFinderModel model;

	private ConfigWindow configWindow;
	private ConfigView configView;

	private MapsWindow mapsWindow;
	private MapView leftMapView;
	private MapView rightMapView;

	// controller state
	private int leftPathFinderIndex;
	private int rightPathFinderIndex;
	private RenderingStyle style;
	private ExecutionMode executionMode;
	private int animationDelay;
	private boolean showingCost;
	private boolean showingParent;

	// actions

	private ActionMap actions = new ActionMap();

	public ActionMap getActions() {
		return actions;
	}

	private Action getAction(String name, ActionListener handler) {
		Action action = actions.get(name);
		if (action == null) {
			action = new AbstractAction(name) {

				@Override
				public void actionPerformed(ActionEvent e) {
					handler.actionPerformed(e);
				}
			};
			actions.put(name, action);
		}
		return action;
	}

	public Action actionDecreaseMapSize() {
		return getAction("-", e -> {
			int mapSize = getModel().getMapSize() - 1;
			if (mapSize >= PathFinderModel.MIN_MAP_SIZE) {
				changeMapSize(mapSize);
			}
		});
	}

	public Action actionIncreaseMapSize() {
		return getAction("+", e -> {
			int mapSize = getModel().getMapSize() + 1;
			if (mapSize <= PathFinderModel.MAX_MAP_SIZE) {
				changeMapSize(mapSize);
			}
		});
	}

	public Action actionRunPathFinderAnimations() {
		return getAction("Run Path Finder Animations", e -> runPathFinderAnimations());
	}

	public Action actionSet4Neighbors() {
		return getAction("4 Neighbors", e -> changeTopology(TopologySelection._4_NEIGHBORS));
	}

	public Action actionSet8Neighbors() {
		return getAction("8 Neighbors", e -> changeTopology(TopologySelection._8_NEIGHBORS));
	}

	public Action actionResetScene() {
		return getAction("Reset Scene", e -> resetScene());
	}

	public Action actionSetSource() {
		return getAction("Set Source", e -> {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			setSource(model.getMap().cell(position));
		});
	}

	public Action actionSetTarget() {
		return getAction("Set Target", e -> {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			setTarget(model.getMap().cell(position));
		});
	}

	public Action actionToggleShowCost() {
		return getAction("Show Cost", e -> {
			showCost(!showingCost);
		});
	}

	public Action actionToggleShowParent() {
		return getAction("Show Parent", e -> {
			showParent(!showingParent);
		});
	}

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		private final StopWatch watch = new StopWatch();
		private final int pathFinderIndex;
		private final MapView mapView;

		public PathFinderAnimationTask(int pathFinderIndex, MapView mapView) {
			this.pathFinderIndex = pathFinderIndex;
			this.mapView = mapView;
		}

		@Override
		protected Void doInBackground() throws Exception {
			PathFinderAnimation animation = mapView.new PathFinderAnimation();
			animation.getDelay().setMillis(PathFinderController.this::getAnimationDelay);
			watch.start();
			model.runPathFinder(pathFinderIndex, animation);
			return null;
		}

		@Override
		protected void done() {
			watch.stop();
			ObservableGraphSearch pathFinder = model.getPathFinder(pathFinderIndex);
			model.setResult(pathFinder, pathFinder.buildPath(pathFinder.getTarget()), watch.getMillis());
			mapView.updateView();
			mapsWindow.updateWindow();
		}
	}

	public PathFinderController(PathFinderModel model, PathFinderAlgorithm leftAlgorithm,
			PathFinderAlgorithm rightAlgorithm) {
		this.model = model;
		leftPathFinderIndex = leftAlgorithm.ordinal();
		rightPathFinderIndex = rightAlgorithm.ordinal();
		style = RenderingStyle.BLOCKS;
		executionMode = ExecutionMode.MANUAL;
		animationDelay = 0;
		showingCost = true;
		showingParent = false;
	}

	public void createAndShowUI() {
		configView = new ConfigView();
		leftMapView = new MapView();
		rightMapView = new MapView();

		configView.init(model, this);
		leftMapView.init(model, this, this::getLeftPathFinderIndex);
		rightMapView.init(model, this, this::getRightPathFinderIndex);

		configWindow = new ConfigWindow(configView);
		configWindow.setLocation(0, 0);
		configWindow.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (screenSize.width - configWindow.getWidth()) / 2 * 95 / 100;
		int height = Math.min(width, screenSize.height);
		leftMapView.setSize(width, height);
		leftMapView.updateView();
		rightMapView.setSize(width, height);
		rightMapView.updateView();

		mapsWindow = new MapsWindow(this, leftMapView, rightMapView);
		mapsWindow.setLocation(configWindow.getWidth(), 0);
		mapsWindow.pack();

		configWindow.setVisible(true);
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
		if (pathFinderIndex != rightPathFinderIndex) {
			leftPathFinderIndex = pathFinderIndex;
			updatePathFinderResults();
		}
	}

	public void changeRightPathFinder(int pathFinderIndex) {
		if (pathFinderIndex != leftPathFinderIndex) {
			rightPathFinderIndex = pathFinderIndex;
			updatePathFinderResults();
		}
	}

	private void updateViews() {
		configView.updateView();
		leftMapView.updateView();
		rightMapView.updateView();
		mapsWindow.updateWindow();
	}

	private void updateMaps(boolean updateViews) {
		leftMapView.updateMap(updateViews);
		rightMapView.updateMap(updateViews);
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
			runPathFinderAnimation(leftPathFinderIndex, leftMapView);
			runPathFinderAnimation(rightPathFinderIndex, rightMapView);
			break;
		case ALL:
			model.runAllPathFinders();
			break;
		default:
			break;
		}
		updateViews();
	}

	// animated execution

	private void runPathFinderAnimation(int pathFinderIndex, MapView mapView) {
		mapView.getCanvas().clear();
		mapView.getCanvas().drawGrid();
		new PathFinderAnimationTask(pathFinderIndex, mapView).execute();
	}

	public synchronized int getAnimationDelay() {
		return animationDelay;
	}

	public synchronized void setAnimationDelay(int animationDelay) {
		this.animationDelay = animationDelay;
	}

	public void runPathFinderAnimations() {
		model.clearResult(leftPathFinderIndex);
		model.clearResult(rightPathFinderIndex);
		runPathFinderAnimation(leftPathFinderIndex, leftMapView);
		runPathFinderAnimation(rightPathFinderIndex, rightMapView);
	}

	// step-wise execution

	public void runBothFirstStep(boolean updateViews) {
		runSingleFirstStep(leftPathFinderIndex);
		runSingleFirstStep(rightPathFinderIndex);
		if (updateViews) {
			updateViews();
		}
	}

	public void runBothNumSteps(int numSteps) {
		runSingleNumSteps(leftMapView, model.getPathFinder(leftPathFinderIndex), numSteps);
		runSingleNumSteps(rightMapView, model.getPathFinder(rightPathFinderIndex), numSteps);
	}

	public void runBothRemainingSteps() {
		runBothNumSteps(Integer.MAX_VALUE);
	}

	public void runSingleFirstStep(int pathFinderIndex) {
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
		updateMaps(false);
		updatePathFinderResults();
	}

	public void changeExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		updatePathFinderResults();
	}

	public void changeMapSize(int size) {
		model.setMapSize(size);
		model.clearResults();
		updateMaps(false);
		updatePathFinderResults();
	}

	public void changeTopology(TopologySelection topology) {
		model.setMapTopology(topology == TopologySelection._4_NEIGHBORS ? Grid4Topology.get() : Grid8Topology.get());
		model.clearResults();
		updateMaps(false);
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
		if (model.getMap().get(source) != WALL) {
			model.setSource(source);
			updatePathFinderResults();
		}
	}

	public void setTarget(int target) {
		if (model.getMap().get(target) != WALL) {
			model.setTarget(target);
			updatePathFinderResults();
		}
	}

	public void setTileAt(int cell, Tile tile) {
		if (cell != model.getSource() && cell != model.getTarget()) {
			model.setMapContent(cell, tile);
			if (executionMode == ExecutionMode.MANUAL) {
				// if path finder was already executed, clear it
				if (getLeftPathFinder().getState(model.getSource()) == TraversalState.COMPLETED) {
					model.clearResults();
					leftMapView.updateView();
					rightMapView.updateView();
				}
				leftMapView.updateMapCell(cell);
				rightMapView.updateMapCell(cell);
			} else {
				updatePathFinderResults();
			}
		}
	}

	public void flipTileAt(int cell) {
		setTileAt(cell, model.getMap().get(cell) == WALL ? BLANK : WALL);
	}
}