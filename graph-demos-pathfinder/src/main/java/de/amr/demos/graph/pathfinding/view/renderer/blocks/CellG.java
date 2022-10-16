package de.amr.demos.graph.pathfinding.view.renderer.blocks;

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
		drawCenteredCelltext(g, cell, formatScaledValue(gValue.apply(cell), 10));
	}
}