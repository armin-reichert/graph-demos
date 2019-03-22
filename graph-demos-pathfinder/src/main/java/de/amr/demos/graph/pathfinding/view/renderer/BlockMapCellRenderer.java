package de.amr.demos.graph.pathfinding.view.renderer;

import static de.amr.graph.pathfinder.api.Path.INFINITE_COST;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.ui.rendering.GridCellRenderer;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * Renders map cells as "blocks". Depending on the associated path finder algorithm, different
 * information is displayed inside each cell.
 * 
 * @author Armin Reichert
 */
public abstract class BlockMapCellRenderer implements GridCellRenderer {

	private final Font font = new Font("Arial Narrow", Font.PLAIN, 12);
	private final Color gridBackground;
	private final int inset;
	private final int cellSize;
	private final Area needle;

	public BlockMapCellRenderer(int cellSize, Color gridBackground) {
		this.cellSize = cellSize;
		this.inset = Math.max(cellSize / 20, 3);
		this.gridBackground = gridBackground;
		this.needle = createNeedle(cellSize);
	}

	private static Area createNeedle(int cellSize) {
		int r = cellSize / 10;
		Area needle = new Area(new Ellipse2D.Double(-r, -r, 2 * r, 2 * r));
		Polygon p = new Polygon();
		p.addPoint(-r, -r / 6);
		p.addPoint(r, -r / 6);
		p.addPoint(0, -(cellSize * 25 / 100));
		needle.add(new Area(p));
		return needle;
	}

	public abstract GraphSearch<?> getPathFinder();

	public abstract Color getCellBackground(int cell);

	public abstract Color getTextColor(int cell);

	public abstract boolean showCost();

	public abstract boolean showParent();

	public abstract boolean hasHighlightedBackground(int cell);

	private String formatScaledValue(double value, double factor) {
		// display real value multiplied by 10
		return value == INFINITE_COST ? "" : String.format("%.0f", factor * value);
	}

	private void setRelativeFontSize(Graphics2D g, int percent) {
		g.setFont(font.deriveFont((float) cellSize * percent / 100));
	}

	private Rectangle2D bounds(Graphics2D g, String text) {
		return g.getFontMetrics().getStringBounds(text, g);
	}

	@Override
	public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		int x = grid.col(cell) * cellSize;
		int y = grid.row(cell) * cellSize;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.translate(x, y);

		// draw cell background
		g.setColor(getCellBackground(cell));
		g.fillRect(0, 0, cellSize, cellSize);

		// draw cell border
		g.setColor(gridBackground);
		if (grid.col(cell) == grid.numCols() - 1) {
			g.drawRect(0, 0, cellSize - 1, cellSize);
		}
		else {
			g.drawRect(0, 0, cellSize, cellSize);
		}
		if (grid.row(cell) == grid.numRows() - 1) {
			g.drawRect(0, 0, cellSize, cellSize - 1);
		}
		else {
			g.drawRect(0, 0, cellSize, cellSize);
		}

		g.translate(-x, -y);

		if (grid.get(cell) == Tile.WALL) {
			return;
		}

		// draw compass needle pointing to parent cell
		if (showParent()) {
			drawNeedle(g, grid, cell);
		}

		// draw path finder dependent content
		if (showCost()) {
			g.translate(x, y);
			if (getPathFinder().getClass() == AStarSearch.class) {
				drawContentAStar(g, cell, (AStarSearch) getPathFinder());
			}
			else if (getPathFinder().getClass() == BestFirstSearch.class) {
				drawContentBestFirstSearch(g, cell, (BestFirstSearch) getPathFinder());
			}
			else {
				drawContent(g, cell, getPathFinder());
			}
			g.translate(-x, -y);
		}
	}

	private void drawContentAStar(Graphics2D g, int cell, AStarSearch astar) {
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);
		g.setFont(font);

		// G-value
		String gCost = formatScaledValue(astar.getCost(cell), 10);
		setRelativeFontSize(g, 30);
		textBox = bounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, inset, (int) textBox.getHeight());

		// H-value
		String hCost = formatScaledValue(astar.getEstimatedCost(cell), 10);
		setRelativeFontSize(g, 30);
		textBox = bounds(g, hCost);
		g.setColor(textColor);
		g.drawString(hCost, (int) (cellSize - textBox.getWidth() - inset), (int) textBox.getHeight());

		// F-value
		String fCost = formatScaledValue(astar.getScore(cell), 10);
		setRelativeFontSize(g, showParent() ? 30 : 50);
		textBox = bounds(g, fCost);
		if (!hasHighlightedBackground(cell)) {
			g.setColor(Color.MAGENTA);
		}
		g.drawString(fCost, (int) (cellSize - textBox.getWidth()) / 2, cellSize - inset);
	}

	private void drawContentBestFirstSearch(Graphics2D g, int cell, BestFirstSearch bfs) {
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);
		g.setFont(font);

		// H-value
		String hCost = formatScaledValue(bfs.getEstimatedCost(cell), 10);
		setRelativeFontSize(g, 30);
		textBox = bounds(g, hCost);
		if (bfs.getState(cell) != TraversalState.UNVISITED && !hasHighlightedBackground(cell)) {
			g.setColor(Color.MAGENTA);
		}
		g.drawString(hCost, inset, (int) textBox.getHeight());

		// G-value
		String gCost = formatScaledValue(bfs.getCost(cell), 10);
		setRelativeFontSize(g, 50);
		textBox = bounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, (int) (cellSize - textBox.getWidth()) / 2, cellSize - inset);
	}

	private void drawContent(Graphics2D g, int cell, GraphSearch<?> pf) {
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);
		g.setFont(font);

		// G-value
		String gCost = formatScaledValue(getPathFinder().getCost(cell), 10);
		setRelativeFontSize(g, 50);
		textBox = bounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, (int) (cellSize - textBox.getWidth()) / 2,
				(int) (cellSize + textBox.getHeight() - g.getFontMetrics().getDescent()) / 2);
	}

	private void drawNeedle(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		int parent = getPathFinder().getParent(cell);
		if (parent == -1) {
			return;
		}
		int x = grid.col(cell) * cellSize;
		int y = grid.row(cell) * cellSize;
		float thickness = Math.max(1, cellSize / 20);
		int rot = grid.getTopology() == Top4.get() ? 90 : 45;
		grid.direction(cell, parent).ifPresent(dir -> {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.DARK_GRAY);
			g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.translate(x + cellSize / 2, y + cellSize / 2);
			// direction constants start at North and then go clock-wise
			g2.rotate(Math.toRadians(rot * dir));
			g2.fill(needle);
			g2.dispose();
		});
	}
}