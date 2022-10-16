package de.amr.demos.graph.pathfinding.view.renderer.blocks;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.function.Function;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * Grid cell displaying a g-value ("cost so far").
 * 
 * @author Armin Reichert
 */
public class CellG extends Cell {

	private Function<Integer, Double> gValue;

	public CellG(Function<Integer, Double> gValue) {
		this.gValue = gValue;
	}

	@Override
	protected void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		final int cs = cellSize.getAsInt();
		final int margin = Math.max(cs / 20, 3);
		// G-value (center, large)
		var text = formatScaledValue(gValue.apply(cell), 10);
		var fontSize = showParent.getAsBoolean() ? (int) (0.3 * cs) : (int) (0.5 * cs);
		if (fontSize >= MIN_FONT_SIZE) {
			g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
			g.setColor(cellTextColor.apply(cell));
			var textBounds = getBounds(g, text);
			g.drawString(text, cs / 2 - (int) (textBounds.getWidth() / 2), cs - margin);
		}
	}
}