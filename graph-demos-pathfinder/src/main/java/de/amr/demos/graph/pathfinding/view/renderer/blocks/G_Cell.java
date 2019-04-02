package de.amr.demos.graph.pathfinding.view.renderer.blocks;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * Grid cell displaying a g-value ("cost so far").
 * 
 * @author Armin Reichert
 */
public class G_Cell extends MapCell {

	private Function<Integer, Double> gValue;

	public G_Cell(Function<Integer, Double> gValue) {
		this.gValue = gValue;
	}

	@Override
	protected void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		String gCostText = formatScaledValue(gValue.apply(cell), 10);
		if (adjustFontSize(g, 0.5f) >= MIN_FONT_SIZE) {
			Rectangle2D textBounds = getBounds(g, gCostText);
			g.setColor(cellTextColor.apply(cell));
			g.drawString(gCostText, (int) (cellSize.getAsInt() - textBounds.getWidth()) / 2,
					(int) (cellSize.getAsInt() + textBounds.getHeight() - g.getFontMetrics().getDescent()) / 2);
		}
	}
}