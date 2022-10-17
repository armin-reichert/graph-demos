package de.amr.demos.graph.pathfinding.view.renderer.blocks;

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
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.core.api.Graph;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.ui.rendering.GridCellRenderer;

/**
 * @author Armin Reichert
 */
public class Cell implements GridCellRenderer {

	public static final int MIN_FONT_SIZE = 8;

	public IntFunction<Integer> parent;
	public BooleanSupplier showCost;
	public BooleanSupplier showParent;
	public IntSupplier cellSize;
	public Color gridBackground;
	public IntFunction<Color> cellBackground;
	public IntFunction<Color> cellTextColor;
	public String fontFamily;

	private IntFunction<Double> fnLeftUpperValue;
	private IntFunction<Double> fnRightUpperValue;
	private IntFunction<Double> fnCenterValue;

	public Cell(IntFunction<Double> fnLeftUpperValue, IntFunction<Double> fnRightUpperValue,
			IntFunction<Double> fnCenterValue) {
		this.fnLeftUpperValue = fnLeftUpperValue;
		this.fnRightUpperValue = fnRightUpperValue;
		this.fnCenterValue = fnCenterValue;
	}

	private String formatScaledValue(double value, double factor) {
		return value == INFINITE_COST ? "\u221e" : String.format("%.0f", factor * value);
	}

	private Rectangle2D getBounds(Graphics2D g, String text) {
		return g.getFontMetrics().getStringBounds(text, g);
	}

	private Area createNeedle() {
		int r = cellSize.getAsInt() / 10;
		Area needle = new Area(new Ellipse2D.Double(-r, -r, 2 * r, 2 * r));
		Polygon p = new Polygon();
		p.addPoint(-r, -r / 6);
		p.addPoint(r, -r / 6);
		p.addPoint(0, -(cellSize.getAsInt() * 25 / 100));
		needle.add(new Area(p));
		return needle;
	}

	private void drawCellContent(Graphics2D g, int cell) {
		if (fnLeftUpperValue != null) {
			drawLeftUpperCellText(g, cell, formatScaledValue(fnLeftUpperValue.apply(cell), 10));
		}
		if (fnRightUpperValue != null) {
			drawRightUpperCellText(g, cell, formatScaledValue(fnRightUpperValue.apply(cell), 10));
		}
		if (fnCenterValue != null) {
			drawCenteredCellText(g, cell, formatScaledValue(fnCenterValue.apply(cell), 10));
		}
	}

	private void drawLeftUpperCellText(Graphics2D g, int cell, String text) {
		final int cs = cellSize.getAsInt();
		var fontSize = (int) (cs * 0.25f);
		if (fontSize >= MIN_FONT_SIZE) {
			var font = new Font(fontFamily, Font.PLAIN, fontSize);
			g.setFont(font);
			g.setColor(cellTextColor.apply(cell));
			g.drawString(text, 2, cs / 3);
		}
	}

	private void drawRightUpperCellText(Graphics2D g, int cell, String text) {
		final int cs = cellSize.getAsInt();
		var fontSize = (int) (cs * 0.25f);
		if (fontSize >= MIN_FONT_SIZE) {
			var font = new Font(fontFamily, Font.PLAIN, fontSize);
			g.setFont(font);
			g.setColor(cellTextColor.apply(cell));
			var textBounds = getBounds(g, text);
			g.drawString(text, (int) (cs - textBounds.getWidth()), cs / 3);
		}
	}

	private void drawCenteredCellText(Graphics2D g, int cell, String text) {
		final int cs = cellSize.getAsInt();
		var fontSize = (int) (showParent.getAsBoolean() ? cs * 0.25f : cs * 0.5f);
		if (fontSize >= MIN_FONT_SIZE) {
			g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
			g.setColor(cellTextColor.apply(cell));
			var textBounds = getBounds(g, text);
			g.drawString(text, cs / 2 - (int) (textBounds.getWidth() / 2), cs - 2);
		}
	}

	@Override
	public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		final int x = grid.col(cell) * cellSize.getAsInt();
		final int y = grid.row(cell) * cellSize.getAsInt();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.translate(x, y);
		drawCellBackground(g, grid, cell);
		g.translate(-x, -y);
		if (grid.get(cell) == Tile.WALL) {
			return;
		}
		if (showCost.getAsBoolean()) {
			g.translate(x, y);
			drawCellContent(g, cell);
			g.translate(-x, -y);
		}
		if (showParent.getAsBoolean()) {
			g.translate(x, y);
			drawNeedle(g, grid, cell, parent.apply(cell), createNeedle());
			g.translate(-x, -y);
		}
	}

	private void drawCellBackground(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		int cs = cellSize.getAsInt();
		g.setColor(cellBackground.apply(cell));
		g.fillRect(0, 0, cs, cs);
		g.setColor(gridBackground);
		boolean lastCol = grid.col(cell) == grid.numCols() - 1;
		g.drawRect(0, 0, lastCol ? cs - 1 : cs, cs);
		boolean lastRow = grid.row(cell) == grid.numRows() - 1;
		g.drawRect(0, 0, cs, lastRow ? cs - 1 : cs);
	}

	private void drawNeedle(Graphics2D g, GridGraph2D<?, ?> grid, int cell, int parent, Area needle) {
		if (parent == Graph.NO_VERTEX) {
			return;
		}
		grid.direction(cell, parent).ifPresent(dir -> {
			Graphics2D g2 = (Graphics2D) g.create();
			int cs = cellSize.getAsInt();
			Stroke stroke = new BasicStroke(Math.max(1, cs / 20), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			g2.setColor(Color.DARK_GRAY);
			g2.setStroke(stroke);
			g2.translate(cs / 2, cs / 2);
			// direction constants start at North and then go clock-wise
			g2.rotate(Math.toRadians((grid.getTopology() == Grid4Topology.get() ? 90 : 45) * dir));
			g2.fill(needle);
			g2.dispose();
		});
	}
}