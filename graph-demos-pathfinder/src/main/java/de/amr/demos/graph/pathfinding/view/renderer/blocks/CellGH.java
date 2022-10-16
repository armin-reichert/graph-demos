package de.amr.demos.graph.pathfinding.view.renderer.blocks;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * Grid cell displaying g-values ("cost") and h-values ("estimated cost").
 * 
 * @author Armin Reichert
 */
public class CellGH extends Cell {

	private Function<Integer, Double> gValue, hValue;

	public CellGH(Function<Integer, Double> gValue, Function<Integer, Double> hValue) {
		this.gValue = gValue;
		this.hValue = hValue;
	}

	@Override
	protected void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		Rectangle2D textBounds;
		Color textColor = cellTextColor.apply(cell);
		int cs = cellSize.getAsInt();
		int margin = Math.max(cs / 20, 3);
		int fontSize;

		// G-value
		String gValueText = formatScaledValue(gValue.apply(cell), 10);
		fontSize = availableFontSize(g, 0.5f);
		if (fontSize >= MIN_FONT_SIZE) {
			textBounds = getBounds(g, gValueText);
			g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
			g.setColor(textColor);
			g.drawString(gValueText, (int) (cs - textBounds.getWidth()) / 2, cs - margin);
		}

		// H-value
		String hValueText = formatScaledValue(hValue.apply(cell), 10);
		fontSize = availableFontSize(g, 0.3f);
		if (fontSize >= MIN_FONT_SIZE) {
			textBounds = getBounds(g, hValueText);
			g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
			g.setColor(textColor);
			g.drawString(hValueText, (int) (cs - textBounds.getWidth() - margin), (int) textBounds.getHeight());
		}
	}
}