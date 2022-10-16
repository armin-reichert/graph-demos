package de.amr.demos.graph.pathfinding.view;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.OptionalInt;
import java.util.function.IntSupplier;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.demos.graph.pathfinding.controller.ExecutionMode;
import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.controller.RenderingStyle;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.renderer.blocks.BlocksMap;
import de.amr.demos.graph.pathfinding.view.renderer.blocks.Cell;
import de.amr.demos.graph.pathfinding.view.renderer.pearls.PearlsCellRenderer;
import de.amr.demos.graph.pathfinding.view.renderer.pearls.PearlsMapRenderer;
import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.Grid8Topology;
import de.amr.graph.grid.ui.animation.DelayedRunner;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.pathfinder.api.GraphSearch;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BidiAStarSearch;
import de.amr.graph.pathfinder.impl.BidiGraphSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;
import de.amr.swing.MySwing;

/**
 * Displays the map together with the information computed during the path finder execution.
 * 
 * @author Armin Reichert
 */
public class MapView extends JPanel {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private PathFinderModel model;
	private PathFinderController controller;
	private MouseController mouse;
	private JPopupMenu contextMenu;

	private JRadioButtonMenuItem rb4Neighbors;
	private JRadioButtonMenuItem rb8Neighbors;
	private JRadioButtonMenuItem rbShowAsBlocks;
	private JRadioButtonMenuItem rbShowAsPearls;
	private JRadioButtonMenuItem rbExecutionManual;
	private JRadioButtonMenuItem rbExecutionAutomatic;
	private JCheckBoxMenuItem cbShowCost;
	private JCheckBoxMenuItem cbShowParent;
	private GridCanvas canvas;
	private IntSupplier fnPathFinderIndex;

	private int getPathFinderIndex() {
		return fnPathFinderIndex.getAsInt();
	}

	public class PathFinderAnimation implements GraphSearchObserver {

		private final DelayedRunner delay = new DelayedRunner();

		public DelayedRunner getDelay() {
			return delay;
		}

		@Override
		public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
			delay.run(() -> canvas.drawGridCell(v));
		}

		@Override
		public void edgeTraversed(int either, int other) {
			delay.run(() -> canvas.drawGridPassage(either, other, true));
		}

		@Override
		public void vertexRemovedFromFrontier(int v) {
			delay.run(() -> canvas.drawGridCell(v));
		}
	}

	private class MouseController extends MouseAdapter {

		private int cellUnderMouse;
		private int draggedCell;

		public MouseController() {
			draggedCell = Graph.NO_VERTEX;
			cellUnderMouse = Graph.NO_VERTEX;
		}

		public int getCellUnderMouse() {
			return cellUnderMouse;
		}

		private int computeCellUnderMouse(MouseEvent e) {
			int col = max(0, min(e.getX() / canvas.getCellSize(), model.getMap().numCols() - 1));
			int row = max(0, min(e.getY() / canvas.getCellSize(), model.getMap().numRows() - 1));
			return model.getMap().cell(col, row);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				int cell = computeCellUnderMouse(e);
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			cellUnderMouse = computeCellUnderMouse(e);
			if (model.getMap().get(cellUnderMouse) == Tile.WALL) {
				return;
			}
			if (e.isShiftDown() && e.isAltDown() && cellUnderMouse != model.getSource()) {
				controller.setSource(cellUnderMouse);
			} else if (!e.isShiftDown() && e.isAltDown() && cellUnderMouse != model.getTarget()) {
				controller.setTarget(cellUnderMouse);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			cellUnderMouse = computeCellUnderMouse(e);
			if (draggedCell != Graph.NO_VERTEX) {
				// end of dragging
				draggedCell = Graph.NO_VERTEX;
				controller.updatePathFinderResults();
			} else if (e.isPopupTrigger()) {
				boolean noWall = model.getMap().get(cellUnderMouse) != Tile.WALL;
				actionSetSourceHere.setEnabled(noWall);
				actionSetTargetHere.setEnabled(noWall);
				contextMenu.show(canvas, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int cell = computeCellUnderMouse(e);
			if (cell != draggedCell) {
				// dragged mouse into new cell
				draggedCell = cell;
				Tile tile = e.isShiftDown() ? Tile.BLANK : Tile.WALL;
				controller.setTileAt(cell, tile);
			}
		}
	}

	// Actions

	private Action actionSetSourceHere = MySwing.action("Search From Here",
			e -> getController().setSource(mouse.getCellUnderMouse()));

	private Action actionSetTargetHere = MySwing.action("Search To Here",
			e -> getController().setTarget(mouse.getCellUnderMouse()));

	public MapView() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
		canvas = new GridCanvas();
		canvas.setBackground(Color.WHITE);
		add(canvas);
	}

	public PathFinderController getController() {
		return controller;
	}

	public void init(PathFinderModel model, PathFinderController controller, IntSupplier fnPathFinderIndex) {
		this.model = model;
		this.controller = controller;
		this.fnPathFinderIndex = fnPathFinderIndex;

		mouse = new MouseController();
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);

		canvas.getActionMap().setParent(controller.getActions());
		InputMap inputMap = canvas.getInputMap();
		inputMap.put(getKeyStroke('+'), controller.actionIncreaseMapSize().getValue(Action.NAME));
		inputMap.put(getKeyStroke('-'), controller.actionDecreaseMapSize().getValue(Action.NAME));
		inputMap.put(getKeyStroke(' '), controller.actionRunPathFinderAnimations().getValue(Action.NAME));
		inputMap.put(getKeyStroke('4'), controller.actionSet4Neighbors().getValue(Action.NAME));
		inputMap.put(getKeyStroke('8'), controller.actionSet8Neighbors().getValue(Action.NAME));

		buildContextMenu(controller);

		controller.runSingleFirstStep(getPathFinderIndex());
	}

	private void buildContextMenu(PathFinderController controller) {
		contextMenu = new JPopupMenu();
		contextMenu.add(controller.actionRunPathFinderAnimations());

		contextMenu.addSeparator();
		rbExecutionManual = new JRadioButtonMenuItem(
				MySwing.action("Manual execution", e -> controller.changeExecutionMode(ExecutionMode.MANUAL)));
		rbExecutionAutomatic = new JRadioButtonMenuItem(
				MySwing.action("Automatic execution", e -> controller.changeExecutionMode(ExecutionMode.VISIBLE)));
		ButtonGroup bgExecutionMode = new ButtonGroup();
		bgExecutionMode.add(rbExecutionAutomatic);
		bgExecutionMode.add(rbExecutionManual);
		contextMenu.add(rbExecutionManual);
		contextMenu.add(rbExecutionAutomatic);

		contextMenu.addSeparator();
		contextMenu.add(actionSetSourceHere);
		JMenu sourceMenu = new JMenu("Search From");
		for (GridPosition position : GridPosition.values()) {
			JMenuItem item = sourceMenu.add(controller.actionSetSource());
			item.setText(position.toString());
			item.putClientProperty("position", position);
		}
		contextMenu.add(sourceMenu);

		contextMenu.addSeparator();
		contextMenu.add(actionSetTargetHere);
		JMenu targetMenu = new JMenu("Search To");
		for (GridPosition position : GridPosition.values()) {
			JMenuItem item = targetMenu.add(controller.actionSetTarget());
			item.setText(position.toString());
			item.putClientProperty("position", position);
		}
		contextMenu.add(targetMenu);

		contextMenu.addSeparator();
		rb4Neighbors = new JRadioButtonMenuItem(controller.actionSet4Neighbors());
		rb8Neighbors = new JRadioButtonMenuItem(controller.actionSet8Neighbors());
		ButtonGroup bgNeighbors = new ButtonGroup();
		bgNeighbors.add(rb4Neighbors);
		bgNeighbors.add(rb8Neighbors);
		contextMenu.add(rb4Neighbors);
		contextMenu.add(rb8Neighbors);

		contextMenu.addSeparator();
		rbShowAsBlocks = new JRadioButtonMenuItem(
				MySwing.action("Blocks", e -> controller.changeStyle(RenderingStyle.BLOCKS)));
		rbShowAsPearls = new JRadioButtonMenuItem(
				MySwing.action("Pearls", e -> controller.changeStyle(RenderingStyle.PEARLS)));
		ButtonGroup bgStyke = new ButtonGroup();
		bgStyke.add(rbShowAsBlocks);
		bgStyke.add(rbShowAsPearls);
		contextMenu.add(rbShowAsBlocks);
		contextMenu.add(rbShowAsPearls);

		contextMenu.addSeparator();

		cbShowCost = new JCheckBoxMenuItem("Show Cost");
		cbShowCost.setSelected(controller.isShowingCost());
		cbShowCost.setAction(controller.actionToggleShowCost());
		contextMenu.add(cbShowCost);

		cbShowParent = new JCheckBoxMenuItem("Show Parent");
		cbShowParent.setSelected(controller.isShowingParent());
		cbShowParent.setAction(controller.actionToggleShowParent());
		contextMenu.add(cbShowParent);

		contextMenu.addSeparator();
		contextMenu.add(controller.actionResetScene());
	}

	public GridCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		updateMap(false);
	}

	public void updateMapCell(int cell) {
		canvas.drawGridCell(cell);
	}

	public void updateView() {
		rb4Neighbors.setSelected(model.getMap().getTopology() == Grid4Topology.get());
		rb8Neighbors.setSelected(model.getMap().getTopology() == Grid8Topology.get());
		cbShowCost.setSelected(controller.isShowingCost());
		cbShowParent.setSelected(controller.isShowingParent());
		rbShowAsBlocks.setSelected(controller.getStyle() == RenderingStyle.BLOCKS);
		rbShowAsPearls.setSelected(controller.getStyle() == RenderingStyle.PEARLS);
		rbExecutionAutomatic.setSelected(controller.getExecutionMode() == ExecutionMode.VISIBLE);
		rbExecutionManual.setSelected(controller.getExecutionMode() == ExecutionMode.MANUAL);
		canvas.clear();
		canvas.replaceRenderer(createMapRenderer());
		canvas.drawGrid();
		requestFocusInWindow();
		LOGGER.trace(() -> "(%d) MapView updated: %s".formatted(updateCnt, this));
	}

	static int updateCnt;

	public void updateMap(boolean updateView) {
		int cellSize = getHeight() / model.getMapSize();
		if (cellSize < 2) {
			return;
		}
		canvas.setCellSize(cellSize, false);
		canvas.setGrid(model.getMap(), false);
		if (updateView) {
			updateView();
		}
	}

	public ObservableGraphSearch getPathFinder() {
		return model.getPathFinder(getPathFinderIndex());
	}

	// Rendering

	private static final Color MAP_BACKGROUND = new Color(180, 180, 180);
	private static final Color WALL_BACKGROUND = new Color(139, 69, 19);
	private static final Color SOURCE_BACKGROUND = Color.BLUE;
	private static final Color SOURCE_FOREGROUND = Color.WHITE;
	private static final Color TARGET_BACKGROUND = Color.GREEN.darker();
	private static final Color TARGET_FOREGROUND = Color.WHITE;
	private static final Color MEETING_POINT_BACKGROUND = Color.GRAY;
	private static final Color MEETING_POINT_FOREGROUND = Color.WHITE;
	private static final Color SOLUTION_BACKGROUND = Color.RED.brighter();
	private static final Color SOLUTION_FOREGROUND = Color.WHITE;
	private static final Color NEXT_CELL_BACKGROUND = new Color(152, 255, 152);
	private static final Color UNVISITED_CELL_BACKGROUND = Color.WHITE;
	private static final Color VISITED_CELL_BACKGROUND = Color.YELLOW;
	private static final Color COMPLETED_CELL_BACKGROUND = Color.ORANGE;

	private GridRenderer createMapRenderer() {
		RenderingStyle style = controller.getStyle();
		if (style == RenderingStyle.BLOCKS) {
			Cell cell = createMapCell();
			cell.parent = getPathFinder()::getParent;
			cell.showCost = controller::isShowingCost;
			cell.showParent = controller::isShowingParent;
			cell.cellTextColor = this::computeTextColor;
			cell.gridBackground = MAP_BACKGROUND;
			cell.fontFamily = "Arial Narrow";
			cell.cellSize = canvas::getCellSize;
			cell.cellBackground = this::computeCellBackground;
			BlocksMap blocksMap = new BlocksMap(cell);
			blocksMap.fnCellSize = canvas::getCellSize;
			blocksMap.fnGridBgColor = () -> MAP_BACKGROUND;
			return blocksMap;
		} else if (style == RenderingStyle.PEARLS) {
			PearlsMapRenderer pmr = new PearlsMapRenderer(new PearlsCellRendererAdapter());
			pmr.fnCellSize = canvas::getCellSize;
			pmr.fnGridBgColor = () -> MAP_BACKGROUND;
			pmr.fnCellBgColor = this::computeCellBackground;
			pmr.fnPassageWidth = (u, v) -> Math.max(canvas.getCellSize() * 5 / 100, 1);
			pmr.fnPassageColor = (cell,
					dir) -> partOfSolution(cell) && partOfSolution(model.getMap().neighbor(cell, dir).get()) ? SOLUTION_BACKGROUND
							: UNVISITED_CELL_BACKGROUND;
			return pmr;
		}
		throw new IllegalArgumentException("Unknown style: " + style);
	}

	private Cell createMapCell() {
		var impl = getPathFinder().getClass();
		if (impl == AStarSearch.class) {
			var astar = (AStarSearch) getPathFinder();
			return new Cell(astar::getScore, astar::getEstimatedCostToTarget, astar::getCost);
		}
		if (impl == BidiAStarSearch.class) {
			var bidiAstar = (BidiAStarSearch) getPathFinder();
			return new Cell(bidiAstar::getScore, bidiAstar::getEstimatedCost, bidiAstar::getCost);
		}
		if (impl == BestFirstSearch.class) {
			var bestfs = (BestFirstSearch) getPathFinder();
			return new Cell(null, bestfs::getEstimatedCost, bestfs::getCost);
		}
		if (impl == DijkstraSearch.class) {
			var dijkstra = (DijkstraSearch) getPathFinder();
			return new Cell(null, dijkstra::getEstimatedCostToTarget, dijkstra::getCost);
		}
		return new Cell(null, null, cell -> getPathFinder().getCost(cell));
	}

	private Color computeCellBackground(int cell) {
		if (cell < 0 || cell >= model.getMap().numVertices()) {
			throw new IllegalArgumentException("Illegal cell " + cell);
		}
		if (model.getMap().get(cell) == Tile.WALL) {
			return WALL_BACKGROUND;
		}
		if (cell == model.getSource()) {
			return SOURCE_BACKGROUND;
		}
		if (cell == model.getTarget()) {
			return TARGET_BACKGROUND;
		}
		if (partOfSolution(cell)) {
			return SOLUTION_BACKGROUND;
		}
		if (getPathFinder() instanceof BidiGraphSearch) {
			BidiGraphSearch<?, ?> bidi = (BidiGraphSearch<?, ?>) getPathFinder();
			if (cell == bidi.getMeetingPoint()) {
				return MEETING_POINT_BACKGROUND;
			}
		}
		// TODO this code sometimes causes strange exceptions
		if (getPathFinder().getState(model.getTarget()) == TraversalState.UNVISITED) {
			OptionalInt nextVertex = getPathFinder().getNextVertex();
			if (nextVertex.isPresent() && nextVertex.getAsInt() == cell) {
				return NEXT_CELL_BACKGROUND;
			}
		}
		if (getPathFinder().getState(cell) == TraversalState.COMPLETED) {
			return COMPLETED_CELL_BACKGROUND;
		}
		if (getPathFinder().getState(cell) == TraversalState.VISITED) {
			return VISITED_CELL_BACKGROUND;
		}
		return UNVISITED_CELL_BACKGROUND;
	}

	private Color computeTextColor(int cell) {
		if (cell == model.getSource()) {
			return SOURCE_FOREGROUND;
		}
		if (cell == model.getTarget()) {
			return TARGET_FOREGROUND;
		}
		if (partOfSolution(cell)) {
			return SOLUTION_FOREGROUND;
		}
		if (getPathFinder() instanceof BidiGraphSearch) {
			BidiGraphSearch<?, ?> bidi = (BidiGraphSearch<?, ?>) getPathFinder();
			if (cell == bidi.getMeetingPoint()) {
				return MEETING_POINT_FOREGROUND;
			}
		}
		return getPathFinder().getState(cell) == TraversalState.UNVISITED ? Color.LIGHT_GRAY : Color.BLUE;
	}

	private boolean partOfSolution(int cell) {
		return model.getResultAtIndex(getPathFinderIndex()).pathContains(cell);
	}

	private class PearlsCellRendererAdapter extends PearlsCellRenderer {

		@Override
		public int getCellSize() {
			return canvas.getCellSize();
		}

		@Override
		public Color getGridBackground() {
			return MAP_BACKGROUND;
		}

		@Override
		public Color getBackground(int cell) {
			return computeCellBackground(cell);
		}

		@Override
		public GraphSearch getPathFinder() {
			return MapView.this.getPathFinder();
		}

		@Override
		public Color getTextColor(int cell) {
			if (cell == model.getSource() || cell == model.getTarget() || isHighlighted(cell)) {
				return Color.WHITE;
			}
			if (getPathFinder().getState(cell) == TraversalState.UNVISITED) {
				return Color.LIGHT_GRAY;
			}
			return Color.BLUE;
		}

		@Override
		public boolean isHighlighted(int cell) {
			return partOfSolution(cell);
		}

		@Override
		public boolean showCost() {
			return controller.isShowingCost();
		}

		@Override
		public boolean showParent() {
			return controller.isShowingParent();
		}
	}
}