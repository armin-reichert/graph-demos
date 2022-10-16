package de.amr.demos.graph.pathfinding.view.renderer.blocks;

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
		drawRightUpperCellText(g, cell, formatScaledValue(hValue.apply(cell), 10));
		drawCenteredCelltext(g, cell, formatScaledValue(gValue.apply(cell), 10));
	}
}