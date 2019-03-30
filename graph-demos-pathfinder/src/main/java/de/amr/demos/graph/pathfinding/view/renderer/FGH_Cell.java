package de.amr.demos.graph.pathfinding.view.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * Grid cell displaying f-values ("score"), g-values ("cost") and h-values ("estimated cost").
 * 
 * @author Armin Reichert
 */
public class FGH_Cell extends MapCell {

	private Function<Integer, Double> fValue, gValue, hValue;

	public FGH_Cell(Function<Integer, Double> fValue, Function<Integer, Double> gValue,
			Function<Integer, Double> hValue) {
		this.fValue = fValue;
		this.gValue = gValue;
		this.hValue = hValue;
	}

	@Override
	protected void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		Rectangle2D textBounds;
		Color textColor = cellTextColor.apply(cell);
		int cs = cellSize.getAsInt();
		int margin = Math.max(cs / 20, 3);

		// G-value
		String gValueText = formatScaledValue(gValue.apply(cell), 10);
		textSize(g, 30);
		textBounds = getBounds(g, gValueText);
		g.setColor(textColor);
		g.drawString(gValueText, margin, (int) textBounds.getHeight());

		// H-value
		String hValueText = formatScaledValue(hValue.apply(cell), 10);
		textSize(g, 30);
		textBounds = getBounds(g, hValueText);
		g.setColor(textColor);
		g.drawString(hValueText, (int) (cs - textBounds.getWidth() - margin), (int) textBounds.getHeight());

		// F-value
		String fValueText = formatScaledValue(fValue.apply(cell), 10);
		textSize(g, showParent.getAsBoolean() ? 30 : 50);
		textBounds = getBounds(g, fValueText);
		g.setColor(textColor);
		g.drawString(fValueText, (int) (cs - textBounds.getWidth()) / 2, cs - margin);
	}
}