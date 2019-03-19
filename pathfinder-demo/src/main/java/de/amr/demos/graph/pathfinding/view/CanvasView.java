package de.amr.demos.graph.pathfinding.view;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.renderer.BlockMapCellRenderer;
import de.amr.demos.graph.pathfinding.view.renderer.BlockMapRenderer;
import de.amr.demos.graph.pathfinding.view.renderer.RenderingStyle;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * View showing map and path finder animations.
 * 
 * @author Armin Reichert
 */
public class CanvasView extends GridCanvas {

	private class MouseHandler extends MouseAdapter {

		private int draggedCell;

		// grid cell index associated with event
		private int getCellUnderMouse(MouseEvent e) {
			int col = max(0, min(e.getX() / getCellSize(), model.getMap().numCols() - 1));
			int row = max(0, min(e.getY() / getCellSize(), model.getMap().numRows() - 1));
			return model.getMap().cell(col, row);
		}

		public MouseHandler() {
			draggedCell = -1;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				int cell = getCellUnderMouse(e);
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (draggedCell != -1) {
				// dragging ends
				draggedCell = -1;
				controller.maybeRunPathFinder();
			}
			else if (e.isPopupTrigger()) {
				// open popup menu
				selectedCell = getCellUnderMouse(e);
				boolean blankCellSelected = model.getMap().get(selectedCell) == Tile.BLANK;
				actionSetSource.setEnabled(blankCellSelected);
				actionSetTarget.setEnabled(blankCellSelected);
				contextMenu.show(CanvasView.this, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int cell = getCellUnderMouse(e);
			if (cell != draggedCell) {
				// dragging into new cell
				draggedCell = cell;
				controller.setTileAt(cell, e.isShiftDown() ? Tile.BLANK : Tile.WALL);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int cell = getCellUnderMouse(e);
			if (e.isShiftDown()) {
				if (cell != selectedCell && model.getMap().get(cell) != Tile.WALL) {
					selectedCell = cell;
					controller.setSource(cell);
				}
			}
			else if (e.isControlDown()) {
				if (cell != selectedCell && model.getMap().get(cell) != Tile.WALL) {
					selectedCell = cell;
					controller.setTarget(cell);
				}
			}
		}
	}

	private Action actionSetSource = new AbstractAction("Search From Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			if (position != null) {
				controller.setSource(model.getMap().cell(position));
			}
			else {
				controller.setSource(selectedCell);
			}
		}
	};

	private Action actionSetTarget = new AbstractAction("Search To Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			if (position != null) {
				controller.setTarget(model.getMap().cell(position));
			}
			else {
				controller.setTarget(selectedCell);
			}
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.resetScene();
		}
	};

	private PathFinderModel model;
	private Controller controller;
	private RenderingStyle style;
	private boolean showCost;
	private boolean showParent;
	private JPopupMenu contextMenu;
	private int selectedCell;
	private int fixedHeight;

	public CanvasView(GridGraph2D<?, ?> grid, int initialHeight) {
		super(grid);
		this.fixedHeight = initialHeight;
		style = RenderingStyle.BLOCKS;
		selectedCell = -1;
		MouseHandler mouse = new MouseHandler();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		createContextMenu();
	}

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

	public void init(PathFinderModel model, Controller controller) {
		this.model = model;
		this.controller = controller;
		ConfigurableGridRenderer r = createMapRenderer(fixedHeight / model.getMapSize());
		pushRenderer(r);
	}

	public void fixHeight(int fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

	private int getCellSize() {
		return getRenderer().get().getModel().getCellSize();
	}

	private void replaceRenderer(int cellSize) {
		if (renderStack.size() > 0) {
			renderStack.pop();
		}
		pushRenderer(createMapRenderer(cellSize));
		clear();
		drawGrid();
	}

	@Override
	public void setGrid(GridGraph<?, ?> grid) {
		super.setGrid(grid);
		int cellSize = (int) Math.floor((float) fixedHeight / grid.numCols());
		replaceRenderer(cellSize);
	}

	public void setStyle(RenderingStyle style) {
		this.style = style;
		replaceRenderer(getCellSize());
	}

	public void setShowCost(boolean showCost) {
		this.showCost = showCost;
		drawGrid();
	}

	public void setShowParent(boolean showParent) {
		this.showParent = showParent;
		drawGrid();
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
		GraphSearch<?> pf = model.getPathFinder(controller.getSelectedAlgorithm());
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

	private class BlockCellRenderer extends BlockMapCellRenderer {

		public BlockCellRenderer(int cellSize) {
			super(cellSize);
		}

		@Override
		public Color getCellBackground(int cell) {
			return computeCellBackground(cell);
		}

		@Override
		public GridGraph<Tile, Double> getMap() {
			return model.getMap();
		}

		@Override
		public GraphSearch<?> getPathFinder() {
			return model.getPathFinder(controller.getSelectedAlgorithm());
		}

		@Override
		public Color getTextColor(int cell) {
			if (cell == model.getSource() || cell == model.getTarget() || hasHighlightedBackground(cell)) {
				return Color.WHITE;
			}
			if (getPathFinder().getState(cell) == TraversalState.UNVISITED) {
				return Color.LIGHT_GRAY;
			}
			return Color.BLUE;
		}

		@Override
		public boolean hasHighlightedBackground(int cell) {
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

	private ConfigurableGridRenderer createMapRenderer(int cellSize) {
		if (style == RenderingStyle.BLOCKS) {
			BlockMapRenderer r = new BlockMapRenderer(new BlockCellRenderer(cellSize));
			r.fnCellSize = () -> cellSize;
			r.fnGridBgColor = () -> new Color(160, 160, 160);
			return r;
		}
		if (style == RenderingStyle.PEARLS) {
			PearlsGridRenderer r = new PearlsGridRenderer();
			r.fnCellSize = () -> cellSize;
			r.fnGridBgColor = () -> new Color(160, 160, 160);
			r.fnCellBgColor = this::computeCellBackground;
			r.fnPassageWidth = (u, v) -> Math.max(cellSize * 5 / 100, 1);
			r.fnPassageColor = (cell, dir) -> partOfSolution(cell)
					&& partOfSolution(grid.neighbor(cell, dir).getAsInt()) ? Color.RED.brighter() : Color.WHITE;
			return r;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void drawGridPassage(int either, int other, boolean visible) {
		super.drawGridPassage(either, other, visible);
		// TODO fixme
		if (style == RenderingStyle.PEARLS) {
			drawGridCell(either);
			drawGridCell(other);
		}
	}
}