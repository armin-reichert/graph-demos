package de.amr.demos.graph.pathfinding.view.renderer;

import static de.amr.graph.pathfinder.api.Path.INFINITE_COST;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
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
		if (getPathFinder().getClass() == AStarSearch.class) {
			g.translate(x, y);
			drawContentAStar(g, cell, (AStarSearch) getPathFinder());
			g.translate(-x, -y);
		}
		else if (getPathFinder().getClass() == BestFirstSearch.class) {
			g.translate(x, y);
			drawContentBestFirstSearch(g, cell, (BestFirstSearch) getPathFinder());
			g.translate(-x, -y);
		}
		else {
			g.translate(x, y);
			drawContentAny(g, cell, getPathFinder());
			g.translate(-x, -y);
		}

		if (showParentDirection()) {
			drawParentDirection(g, cell, x, y, getPathFinder());
		}
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

	private void drawParentDirection(Graphics2D g, int cell, int x, int y, GraphSearch<?> pf) {
		int parent = pf.getParent(cell);
		if (parent == -1) {
			return;
		}
		int r = cellSize / 10;
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(x + cellSize / 2, y + cellSize / 2);
		g2.setColor(Color.GRAY);
		g2.fillOval(-r, -r, 2 * r, 2 * r);
		int dir = getMap().direction(cell, parent).getAsInt();
		g2.drawString(dir + "", cellSize / 2, cellSize / 2);
		double theta = Math.toRadians(-computeRotationDegrees(dir));
		g2.rotate(theta);
		int lineLength = cellSize * 33 / 100;
		g2.setStroke(new BasicStroke(Math.max(1, cellSize / 20)));
		g2.drawLine(0, -lineLength, 0, 0);
		g2.dispose();
	}

	private int computeRotationDegrees(int dir) {
		if (getMap().getTopology() == Top4.get()) {
			switch (dir) {
			case Top4.N:
				return 0;
			case Top4.W:
				return 90;
			case Top4.S:
				return 180;
			case Top4.E:
				return 270;
			}
		}
		else if (getMap().getTopology() == Top8.get()) {
			switch (dir) {
			case Top8.N:
				return 0;
			case Top8.NW:
				return 45;
			case Top8.W:
				return 90;
			case Top8.SW:
				return 135;
			case Top8.S:
				return 180;
			case Top8.SE:
				return 225;
			case Top8.E:
				return 270;
			case Top8.NE:
				return 315;
			}
		}
		throw new IllegalStateException();
	}
}