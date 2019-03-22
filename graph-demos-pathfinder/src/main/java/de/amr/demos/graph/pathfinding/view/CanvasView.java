package de.amr.demos.graph.pathfinding.view;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.renderer.BlocksCellRenderer;
import de.amr.demos.graph.pathfinding.view.renderer.BlocksMapRenderer;
import de.amr.demos.graph.pathfinding.view.renderer.RenderingStyle;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearch;
import de.amr.graph.pathfinder.api.GraphSearchObserver;

/**
 * View showing the map with the path finder data and animations.
 * 
 * @author Armin Reichert
 */
public class CanvasView extends JPanel {

	private static final Color GRID_BACKGROUND = new Color(160, 160, 160);

	private PathFinderModel model;
	private Controller controller;
	private RenderingStyle style;
	private boolean showCost;
	private boolean showParent;
	private int cellUnderMouse;
	private GridCanvas canvas;
	private JPopupMenu contextMenu;

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
			canvas.drawGridCell(v);
		}
	}

	private class MouseHandler extends MouseAdapter {

		private int draggedCell;

		public MouseHandler() {
			draggedCell = -1;
		}

		private int getCellUnderMouse(MouseEvent e) {
			int col = max(0, min(e.getX() / canvas.getCellSize(), model.getMap().numCols() - 1));
			int row = max(0, min(e.getY() / canvas.getCellSize(), model.getMap().numRows() - 1));
			return model.getMap().cell(col, row);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				int cell = getCellUnderMouse(e);
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			cellUnderMouse = getCellUnderMouse(e);
			if (e.isControlDown() && cellUnderMouse != model.getSource()) {
				controller.setSource(cellUnderMouse);
			}
			else if (e.isAltDown() && cellUnderMouse != model.getTarget()) {
				controller.setTarget(cellUnderMouse);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			cellUnderMouse = getCellUnderMouse(e);
			if (draggedCell != -1) {
				// end of dragging
				System.out.println("dragging ends");
				draggedCell = -1;
				controller.maybeRunPathFinder();
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
			int cell = getCellUnderMouse(e);
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
			controller.setSource(position != null ? model.getMap().cell(position) : cellUnderMouse);
		}
	};

	private Action actionSetTarget = new AbstractAction("Search To Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			controller.setTarget(position != null ? model.getMap().cell(position) : cellUnderMouse);
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.resetScene();
		}
	};

	public CanvasView() {
		setBackground(Color.WHITE);
		style = RenderingStyle.BLOCKS;
		cellUnderMouse = -1;
		setLayout(new BorderLayout(0, 0));
		canvas = new GridCanvas();
		canvas.setBackground(Color.WHITE);
		add(canvas);
	}

	public void init(PathFinderModel model, Controller controller) {
		this.model = model;
		this.controller = controller;
		MouseHandler mouse = new MouseHandler();
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);
		createContextMenu();
		int cellSize = getHeight() / model.getMapSize();
		canvas.setCellSize(cellSize, false);
		canvas.setGrid(model.getMap(), false);
		replaceRenderer();
	}

	private void replaceRenderer() {
		canvas.replaceRenderer(createMapRenderer());
		canvas.clear();
		canvas.drawGrid();
	}

	public void setGrid(GridGraph<?, ?> grid) {
		int cellSize = getHeight() / grid.numCols();
		canvas.setGrid(grid, false);
		canvas.setCellSize(cellSize, false);
		replaceRenderer();
	}

	public PathFinderAnimation createAnimation() {
		return new PathFinderAnimation();
	}

	public void setStyle(RenderingStyle style) {
		this.style = style;
		replaceRenderer();
	}

	public RenderingStyle getStyle() {
		return style;
	}

	public void setShowCost(boolean showCost) {
		this.showCost = showCost;
		canvas.drawGrid();
	}

	public boolean isShowCost() {
		return showCost;
	}

	public void setShowParent(boolean showParent) {
		this.showParent = showParent;
		canvas.drawGrid();
	}

	public boolean isShowParent() {
		return showParent;
	}

	public void updateView() {
		canvas.clear();
		canvas.drawGrid();
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

	// Renderer

	private Color computeCellBackground(int cell) {
		if (model.getMap().get(cell) == Tile.WALL) {
			return new Color(139, 69, 19);
		}
		if (cell == model.getSource()) {
			return Color.BLUE;
		}
		if (cell == model.getTarget()) {
			return Color.GREEN.darker();
		}
		if (partOfSolution(cell)) {
			return Color.RED.brighter();
		}
		GraphSearch pf = model.getPathFinder(controller.getSelectedAlgorithm());
		TraversalState cellState = pf.getState(cell);
		if (pf.getState(model.getTarget()) == TraversalState.UNVISITED && pf.getNextVertex().isPresent()
				&& cell == pf.getNextVertex().getAsInt()) {
			return new Color(152, 255, 152);
		}
		if (cellState == TraversalState.COMPLETED) {
			return Color.ORANGE;
		}
		if (cellState == TraversalState.VISITED) {
			return Color.YELLOW;
		}
		return Color.WHITE;
	}

	private boolean partOfSolution(int cell) {
		return model.getRun(controller.getSelectedAlgorithm()).pathContains(cell);
	}

	private class BlocksCellRendererAdapter extends BlocksCellRenderer {

		@Override
		public int getCellSize() {
			return canvas.getCellSize();
		}

		@Override
		public Color getGridBackground() {
			return GRID_BACKGROUND;
		}

		@Override
		public Color getBackground(int cell) {
			return computeCellBackground(cell);
		}

		@Override
		public GraphSearch getPathFinder() {
			return model.getPathFinder(controller.getSelectedAlgorithm());
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
			return showCost;
		}

		@Override
		public boolean showParent() {
			return showParent;
		}
	}

	private GridRenderer createMapRenderer() {
		if (style == RenderingStyle.BLOCKS) {
			BlocksMapRenderer r = new BlocksMapRenderer(new BlocksCellRendererAdapter());
			r.fnCellSize = canvas::getCellSize;
			r.fnGridBgColor = () -> GRID_BACKGROUND;
			return r;
		}
		if (style == RenderingStyle.PEARLS) {
			PearlsGridRenderer r = new PearlsGridRenderer();
			r.fnCellSize = canvas::getCellSize;
			r.fnGridBgColor = () -> GRID_BACKGROUND;
			r.fnCellBgColor = this::computeCellBackground;
			r.fnPassageWidth = (u, v) -> Math.max(canvas.getCellSize() * 5 / 100, 1);
			r.fnPassageColor = (cell,
					dir) -> partOfSolution(cell) && partOfSolution(model.getMap().neighbor(cell, dir).getAsInt())
							? Color.RED.brighter()
							: Color.WHITE;
			return r;
		}
		throw new IllegalArgumentException("Unknown style: " + style);
	}
}