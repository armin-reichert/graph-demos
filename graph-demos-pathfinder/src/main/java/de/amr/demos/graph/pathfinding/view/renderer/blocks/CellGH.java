package de.amr.demos.graph.pathfinding.view.renderer.blocks;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.function.Function;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * Grid cell displaying g-values ("cost") and h-values ("estimated cost").
 * 
 * @author Armin Reichert
 */
public class CellGH extends Cell {

	private Function<Integer, Double> gValue;
	private Function<Integer, Double> hValue;

	public CellGH(Function<Integer, Double> gValue, Function<Integer, Double> hValue) {
		this.gValue = gValue;
		this.hValue = hValue;
	}

	@Override
	protected void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		final int cs = cellSize.getAsInt();
		final int margin = Math.max(cs / 20, 3);
		int fontSize;
		String text;

		// H-value (top-right, small)
		text = formatScaledValue(hValue.apply(cell), 10);
		fontSize = availableFontSize(g, 0.3f);
		if (fontSize >= MIN_FONT_SIZE) {
			g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
			g.setColor(cellTextColor.apply(cell));
			var textBounds = getBounds(g, text);
			g.drawString(text, (int) (cs - textBounds.getWidth()), cs / 3);
		}

		// G-value (center, large)
		text = formatScaledValue(gValue.apply(cell), 10);
		fontSize = availableFontSize(g, showParent.getAsBoolean() ? 0.3f : 0.5f);
		if (fontSize >= MIN_FONT_SIZE) {
			g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
			g.setColor(cellTextColor.apply(cell));
			var textBounds = getBounds(g, text);
			g.drawString(text, cs / 2 - (int) (textBounds.getWidth() / 2), cs - margin);
		}
	}
}