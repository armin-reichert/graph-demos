package de.amr.demos.graph.pathfinding.view.renderer.pearls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.ui.rendering.GridCellRenderer;
import de.amr.graph.pathfinder.api.GraphSearch;

/**
 * Renders map cells as "blocks". Depending on the current path finder algorithm, different
 * information is displayed inside each cell.
 * 
 * @author Armin Reichert
 */
public abstract class PearlsCellRenderer implements GridCellRenderer {

	private final Area needle;

	public PearlsCellRenderer() {
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

	@Override
	public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		int cellSize = getCellSize();
		int x = grid.col(cell) * cellSize;
		int y = grid.row(cell) * cellSize;
		int pearlSize = Math.max(1, cellSize * 66 / 100);
		int offset = (cellSize - pearlSize) / 2;
		int arc = pearlSize / 2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground(cell));
		g.translate(x + offset, y + offset);
		g.fillRoundRect(0, 0, pearlSize, pearlSize, arc, arc);
		g.translate(-x - offset, -y - offset);
		g.translate(x, y);
		drawCellContent(g, grid, cell);
		g.translate(-x, -y);
	}

	private void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		if (grid.get(cell) == Tile.WALL) {
			return;
		}
		// draw compass needle pointing to parent cell
		if (showParent()) {
			drawNeedle(g, grid, cell);
		}
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
			g2.rotate(Math.toRadians((grid.getTopology() == Grid4Topology.get() ? 90 : 45) * dir));
			g2.fill(needle);
			g2.dispose();
		});
	}
}