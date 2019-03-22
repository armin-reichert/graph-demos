package de.amr.demos.graph.pathfinding.view.renderer;

import static de.amr.graph.pathfinder.api.Path.INFINITE_COST;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.ui.rendering.GridCellRenderer;
import de.amr.graph.pathfinder.api.GraphSearch;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;

/**
 * Renders map cells as "blocks". Depending on the current path finder algorithm, different
 * information is displayed inside each cell.
 * 
 * @author Armin Reichert
 */
public abstract class BlocksCellRenderer implements GridCellRenderer {

	private final Font font = new Font("Arial Narrow", Font.PLAIN, 12);
	private final int inset;
	private final Area needle;

	public BlocksCellRenderer() {
		this.inset = Math.max(getCellSize() / 20, 3);
		this.needle = createNeedle();
	}

	public abstract int getCellSize();

	public abstract GraphSearch getPathFinder();

	public abstract Color getGridBackground();

	public abstract Color getBackground(int cell);

	public abstract boolean isHighlighted(int cell);

	public abstract Color getTextColor(int cell);

	public abstract boolean showCost();

	public abstract boolean showParent();

	private Area createNeedle() {
		int r = getCellSize() / 10;
		Area needle = new Area(new Ellipse2D.Double(-r, -r, 2 * r, 2 * r));
		Polygon p = new Polygon();
		p.addPoint(-r, -r / 6);
		p.addPoint(r, -r / 6);
		p.addPoint(0, -(getCellSize() * 25 / 100));
		needle.add(new Area(p));
		return needle;
	}

	private String formatScaledValue(double value, double factor) {
		// display real value multiplied by 10
		return value == INFINITE_COST ? "" : String.format("%.0f", factor * value);
	}

	private void setRelativeFontSize(Graphics2D g, int percent) {
		g.setFont(font.deriveFont((float) getCellSize() * percent / 100));
	}

	private Rectangle2D getBounds(Graphics2D g, String text) {
		return g.getFontMetrics().getStringBounds(text, g);
	}

	@Override
	public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		final int x = grid.col(cell) * getCellSize();
		final int y = grid.row(cell) * getCellSize();
		g.translate(x, y);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// cell background
		g.setColor(getBackground(cell));
		g.fillRect(0, 0, getCellSize(), getCellSize());
		// cell border
		g.setColor(getGridBackground());
		boolean lastCol = grid.col(cell) == grid.numCols() - 1;
		g.drawRect(0, 0, lastCol ? getCellSize() - 1 : getCellSize(), getCellSize());
		boolean lastRow = grid.row(cell) == grid.numRows() - 1;
		g.drawRect(0, 0, getCellSize(), lastRow ? getCellSize() - 1 : getCellSize());
		// cell content
		drawCellContent(g, cell, grid);
		g.translate(-x, -y);
	}

	private void drawCellContent(Graphics2D g, int cell, GridGraph2D<?, ?> grid) {
		if (grid.get(cell) == Tile.WALL) {
			return;
		}
		// draw compass needle pointing to parent cell
		if (showParent()) {
			drawNeedle(g, grid, cell);
		}
		// draw path finder dependent content
		if (showCost()) {
			if (getPathFinder().getClass() == AStarSearch.class) {
				drawContentAStar(g, cell);
			}
			else if (getPathFinder().getClass() == BestFirstSearch.class) {
				drawContentBestFirstSearch(g, cell);
			}
			else {
				drawContentGeneric(g, cell);
			}
		}
	}

	private void drawContentAStar(Graphics2D g, int cell) {
		AStarSearch astar = (AStarSearch) getPathFinder();
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);
		g.setFont(font);

		// G-value
		String gCost = formatScaledValue(astar.getCost(cell), 10);
		setRelativeFontSize(g, 30);
		textBox = getBounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, inset, (int) textBox.getHeight());

		// H-value
		String hCost = formatScaledValue(astar.getEstimatedCost(cell), 10);
		setRelativeFontSize(g, 30);
		textBox = getBounds(g, hCost);
		g.setColor(textColor);
		g.drawString(hCost, (int) (getCellSize() - textBox.getWidth() - inset), (int) textBox.getHeight());

		// F-value
		String fCost = formatScaledValue(astar.getScore(cell), 10);
		setRelativeFontSize(g, showParent() ? 30 : 50);
		textBox = getBounds(g, fCost);
		if (!isHighlighted(cell)) {
			g.setColor(Color.MAGENTA);
		}
		g.drawString(fCost, (int) (getCellSize() - textBox.getWidth()) / 2, getCellSize() - inset);
	}

	private void drawContentBestFirstSearch(Graphics2D g, int cell) {
		BestFirstSearch bfs = (BestFirstSearch) getPathFinder();
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);
		g.setFont(font);

		// H-value
		String hCost = formatScaledValue(bfs.getEstimatedCost(cell), 10);
		setRelativeFontSize(g, 30);
		textBox = getBounds(g, hCost);
		if (bfs.getState(cell) != TraversalState.UNVISITED && !isHighlighted(cell)) {
			g.setColor(Color.MAGENTA);
		}
		g.drawString(hCost, inset, (int) textBox.getHeight());

		// G-value
		String gCost = formatScaledValue(bfs.getCost(cell), 10);
		setRelativeFontSize(g, 50);
		textBox = getBounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, (int) (getCellSize() - textBox.getWidth()) / 2, getCellSize() - inset);
	}

	private void drawContentGeneric(Graphics2D g, int cell) {
		Rectangle2D textBox;
		Color textColor = getTextColor(cell);
		g.setFont(font);

		// G-value
		String gCost = formatScaledValue(getPathFinder().getCost(cell), 10);
		setRelativeFontSize(g, 50);
		textBox = getBounds(g, gCost);
		g.setColor(textColor);
		g.drawString(gCost, (int) (getCellSize() - textBox.getWidth()) / 2,
				(int) (getCellSize() + textBox.getHeight() - g.getFontMetrics().getDescent()) / 2);
	}

	private void drawNeedle(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		int parent = getPathFinder().getParent(cell);
		if (parent == -1) {
			return;
		}
		Stroke stroke = new BasicStroke(Math.max(1, getCellSize() / 20), BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND);
		grid.direction(cell, parent).ifPresent(dir -> {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.DARK_GRAY);
			g2.setStroke(stroke);
			g2.translate(getCellSize() / 2, getCellSize() / 2);
			// direction constants start at North and then go clock-wise
			g2.rotate(Math.toRadians((grid.getTopology() == Top4.get() ? 90 : 45) * dir));
			g2.fill(needle);
			g2.dispose();
		});
	}
}