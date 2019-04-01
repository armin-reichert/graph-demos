package de.amr.demos.graph.pathfinding.view.renderer.pearls;

import java.awt.Graphics2D;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;

public class PearlsMapRenderer extends PearlsGridRenderer {

	public PearlsMapRenderer(PearlsCellRenderer cellRenderer) {
		super(cellRenderer);
	}

	@Override
	public void drawPassage(Graphics2D g, GridGraph2D<?, ?> grid, int either, int other, boolean visible) {
		super.drawPassage(g, grid, either, other, visible);
		drawCellContent(g, grid, either);
		drawCellContent(g, grid, other);
	}
}
