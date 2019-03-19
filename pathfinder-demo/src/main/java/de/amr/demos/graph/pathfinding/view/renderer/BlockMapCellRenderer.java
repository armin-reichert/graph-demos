package de.amr.demos.graph.pathfinding.view.renderer;

import static de.amr.graph.pathfinder.api.Path.INFINITE_COST;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.ui.rendering.GridCellRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * Renderes the map cells as "blocks". Depending on the assiciated path finder algorithm, different
 * information is displayed.
 * 
 * @author Armin Reichert
 */
public abstract class BlockMapCellRenderer implements GridCellRenderer {

	private Font font = new Font("Arial Narrow", Font.PLAIN, 12);
	private int inset;
	private int cellSize;

	public BlockMapCellRenderer(int cellSize) {
		this.cellSize = cellSize;
		this.inset = cellSize / 10;
	}

	public abstract GridGraph<Tile, Double> getMap();

	public abstract GraphSearch<?> getPathFinder();

	public abstract Color getCellBackground(int cell);

	public abstract Color getTextColor(int cell);

	public abstract boolean showCost();

	public abstract boolean showParentDirection();

	public abstract boolean hasHighlightedBackground(int cell);

	private String formatValue(double value) {
		// display real value multiplied by 10
		return value == INFINITE_COST ? "" : String.format("%.0f", 10 * value);
	}

	private void setRelativeFontSize(Graphics2D g, int percent) {
		g.setFont(font.deriveFont((float) cellSize * percent / 100));
	}

	private Rectangle2D bounds(Graphics2D g, String text) {
		return g.getFontMetrics().getStringBounds(text, g);
	}

	@Override
	public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {

		int col = grid.col(cell);
		int row = grid.row(cell);
		int x = col * cellSize;
		int y = row * cellSize;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw cell background and cell border
		g.translate(x, y);
		g.setColor(getCellBackground(cell));
		g.fillRect(0, 0, cellSize, cellSize);
		g.setColor(new Color(160, 160, 160));
		g.drawRect(0, 0, cellSize, cellSize);
		g.translate(-x, -y);

		if (getMap().get(cell) == Tile.WALL) {
			return;
		}

		if (!showCost()) {
			return;
		}

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(font);
		g.translate(x, y);
		if (getPathFinder().getClass() == AStarSearch.class) {
			drawContentAStar(g, cell, (AStarSearch) getPathFinder());
		}
		else if (getPathFinder().getClass() == BestFirstSearch.class) {
			drawContentBestFirstSearch(g, cell, (BestFirstSearch) getPathFinder());
		}
		else {
			drawContentAny(g, cell, getPathFinder());
		}
		g.translate(-x, -y);
	}

	private void drawContentAStar(Graphics2D g, int cell, AStarSearch astar) {
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);

		// G-value
		String gCost = formatValue(astar.getCost(cell));
		setRelativeFontSize(g, 30);
		textBox = bounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, inset, (int) textBox.getHeight());

		// H-value
		String hCost = formatValue(astar.getEstimatedCost(cell));
		setRelativeFontSize(g, 30);
		textBox = bounds(g, hCost);
		g.setColor(textColor);
		g.drawString(hCost, (int) (cellSize - textBox.getWidth() - inset), (int) textBox.getHeight());

		// F-value
		String fCost = formatValue(astar.getScore(cell));
		setRelativeFontSize(g, 50);
		textBox = bounds(g, fCost);
		if (!hasHighlightedBackground(cell)) {
			g.setColor(Color.MAGENTA);
		}
		g.drawString(fCost, (int) (cellSize - textBox.getWidth()) / 2, cellSize - inset);
	}

	private void drawContentBestFirstSearch(Graphics2D g, int cell, BestFirstSearch bfs) {
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);

		// H-value
		String hCost = formatValue(bfs.getEstimatedCost(cell));
		setRelativeFontSize(g, 30);
		textBox = bounds(g, hCost);
		if (bfs.getState(cell) != TraversalState.UNVISITED && !hasHighlightedBackground(cell)) {
			g.setColor(Color.MAGENTA);
		}
		g.drawString(hCost, inset, (int) textBox.getHeight());

		// G-value
		String gCost = formatValue(bfs.getCost(cell));
		setRelativeFontSize(g, 50);
		textBox = bounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, (int) (cellSize - textBox.getWidth()) / 2, cellSize - inset);
	}

	private void drawContentAny(Graphics2D g, int cell, GraphSearch<?> pf) {
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);

		// G-value
		String gCost = formatValue(getPathFinder().getCost(cell));
		setRelativeFontSize(g, 50);
		textBox = bounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, (int) (cellSize - textBox.getWidth()) / 2,
				(int) (cellSize + textBox.getHeight() - g.getFontMetrics().getDescent()) / 2);
	}
}