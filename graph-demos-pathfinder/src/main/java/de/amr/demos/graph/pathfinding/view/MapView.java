package de.amr.demos.graph.pathfinding.view;

import static de.amr.graph.pathfinder.api.GraphSearch.NO_VERTEX;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntSupplier;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.RenderingStyle;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.renderer.blocks.BlocksMap;
import de.amr.demos.graph.pathfinding.view.renderer.blocks.FGH_Cell;
import de.amr.demos.graph.pathfinding.view.renderer.blocks.GH_Cell;
import de.amr.demos.graph.pathfinding.view.renderer.blocks.G_Cell;
import de.amr.demos.graph.pathfinding.view.renderer.blocks.MapCell;
import de.amr.demos.graph.pathfinding.view.renderer.pearls.PearlsCellRenderer;
import de.amr.demos.graph.pathfinding.view.renderer.pearls.PearlsMapRenderer;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.pathfinder.api.GraphSearch;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BidiAStarSearch;
import de.amr.graph.pathfinder.impl.BidiGraphSearch;

/**
 * Displays the map together with the information computed during the path finder execution.
 * 
 * @author Armin Reichert
 */
public class MapView extends JPanel {

	private PathFinderModel model;
	private PathFinderController controller;
	private MouseController mouse;
	private JPopupMenu contextMenu;
	private GridCanvas canvas;
	private IntSupplier fnPathFinderIndex;

	private int getPathFinderIndex() {
		return fnPathFinderIndex.getAsInt();
	}

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			PathFinderAnimation animation = new PathFinderAnimation();
			animation.setFnDelay(() -> (int) Math.sqrt(controller.getAnimationDelay()));
			model.runPathFinder(getPathFinderIndex(), animation);
			return null;
		}

		@Override
		protected void done() {
			updateView();
		}
	}

	public class PathFinderAnimation extends AbstractAnimation implements GraphSearchObserver {

		@Override
		public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
			delayed(() -> canvas.drawGridCell(v));
		}

		@Override
		public void edgeTraversed(int either, int other) {
			delayed(() -> canvas.drawGridPassage(either, other, true));
		}

		@Override
		public void vertexRemovedFromFrontier(int v) {
			delayed(() -> canvas.drawGridCell(v));
		}
	}

	private class MouseController extends MouseAdapter {

		private int cellUnderMouse;
		private int draggedCell;

		public MouseController() {
			draggedCell = NO_VERTEX;
			cellUnderMouse = NO_VERTEX;
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
			}
			else if (!e.isShiftDown() && e.isAltDown() && cellUnderMouse != model.getTarget()) {
				controller.setTarget(cellUnderMouse);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			cellUnderMouse = computeCellUnderMouse(e);
			if (draggedCell != NO_VERTEX) {
				// end of dragging
				draggedCell = NO_VERTEX;
				controller.updatePathFinderResults();
			}
			else if (e.isPopupTrigger()) {
				boolean noWall = model.getMap().get(cellUnderMouse) != Tile.WALL;
				actionSetSource.setEnabled(noWall);
				actionSetTarget.setEnabled(noWall);
				contextMenu.show(canvas, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int cell = computeCellUnderMouse(e);
			if (cell != draggedCell) {
				// dragged mouse into new cell
				draggedCell = cell;
				controller.setTileAt(cell, e.isShiftDown() ? Tile.BLANK : Tile.WALL);
			}
		}
	}

	private Action actionSetSource = new AbstractAction("Search From Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			controller.setSource(position != null ? model.getMap().cell(position) : mouse.getCellUnderMouse());
		}
	};

	private Action actionSetTarget = new AbstractAction("Search To Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			controller.setTarget(position != null ? model.getMap().cell(position) : mouse.getCellUnderMouse());
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.resetScene();
		}
	};

	public MapView() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
		canvas = new GridCanvas();
		canvas.setBackground(Color.WHITE);
		add(canvas);
	}

	public void init(PathFinderModel model, PathFinderController controller, IntSupplier fnPathFinderIndex,
			int size) {
		this.model = model;
		this.controller = controller;
		this.fnPathFinderIndex = fnPathFinderIndex;
		mouse = new MouseController();
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);
		createContextMenu();
		setSize(size, size);
		setPreferredSize(new Dimension(size, size));
		updateMap();
	}

	public void runPathFinderAnimation() {
		new PathFinderAnimationTask().execute();
	}

	public void updateView() {
		canvas.clear();
		canvas.replaceRenderer(createMapRenderer());
		canvas.drawGrid();
	}

	public void updateMap() {
		int cellSize = getHeight() / model.getMapSize();
		if (cellSize < 2) {
			return;
		}
		canvas.setCellSize(cellSize, false);
		canvas.setGrid(model.getMap(), false);
		updateView();
	}

	public ObservableGraphSearch getPathFinder() {
		return model.getPathFinder(getPathFinderIndex());
	}

	// Context menu

	private void createContextMenu() {
		contextMenu = new JPopupMenu();
		contextMenu.add(actionSetSource);
		JMenu sourceMenu = new JMenu("Search From");
		for (GridPosition position : GridPosition.values()) {
			JMenuItem item = sourceMenu.add(actionSetSource);
			item.setText(position.toString());
			item.putClientProperty("position", position);
		}
		contextMenu.add(sourceMenu);
		contextMenu.addSeparator();
		contextMenu.add(actionSetTarget);
		JMenu targetMenu = new JMenu("Search To");
		for (GridPosition position : GridPosition.values()) {
			JMenuItem item = targetMenu.add(actionSetTarget);
			item.setText(position.toString());
			item.putClientProperty("position", position);
		}
		contextMenu.add(targetMenu);
		contextMenu.addSeparator();
		contextMenu.add(actionResetScene);
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
			MapCell cell = createMapCell();
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
		}
		else if (style == RenderingStyle.PEARLS) {
			PearlsMapRenderer pearlsMap = new PearlsMapRenderer(new PearlsCellRendererAdapter());
			pearlsMap.fnCellSize = canvas::getCellSize;
			pearlsMap.fnGridBgColor = () -> MAP_BACKGROUND;
			pearlsMap.fnCellBgColor = this::computeCellBackground;
			pearlsMap.fnPassageWidth = (u, v) -> Math.max(canvas.getCellSize() * 5 / 100, 1);
			pearlsMap.fnPassageColor = (cell,
					dir) -> partOfSolution(cell) && partOfSolution(model.getMap().neighbor(cell, dir).getAsInt())
							? SOLUTION_BACKGROUND
							: UNVISITED_CELL_BACKGROUND;
			return pearlsMap;
		}
		throw new IllegalArgumentException("Unknown style: " + style);
	}

	private MapCell createMapCell() {
		if (getPathFinder().getClass() == AStarSearch.class) {
			AStarSearch pathFinder = (AStarSearch) getPathFinder();
			return new FGH_Cell(cell -> pathFinder.getScore(cell), cell -> pathFinder.getCost(cell),
					cell -> pathFinder.getEstimatedCost(cell));
		}
		if (getPathFinder().getClass() == BidiAStarSearch.class) {
			BidiAStarSearch pathFinder = (BidiAStarSearch) getPathFinder();
			return new FGH_Cell(cell -> pathFinder.getScore(cell), cell -> pathFinder.getCost(cell),
					cell -> pathFinder.getEstimatedCost(cell));
		}
		if (getPathFinder().getClass() == BestFirstSearch.class) {
			BestFirstSearch pathFinder = (BestFirstSearch) getPathFinder();
			return new GH_Cell(cell -> pathFinder.getCost(cell), cell -> pathFinder.getEstimatedCost(cell));
		}
		return new G_Cell(cell -> getPathFinder().getCost(cell));
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
		// TODO this code causes strange exceptions
		// if (getPathFinder().getState(model.getTarget()) == TraversalState.UNVISITED
		// && getPathFinder().getNextVertex().isPresent()
		// && cell == getPathFinder().getNextVertex().getAsInt()) {
		// return NEXT_CELL_BACKGROUND;
		// }
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
		return model.getResult(getPathFinderIndex()).pathContains(cell);
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