package de.amr.demos.graph.pathfinding.view.renderer;

import java.awt.Graphics2D;
import java.util.Objects;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCellRenderer;
import de.amr.graph.grid.ui.rendering.GridRenderer;

public class BlockMapRenderer extends ConfigurableGridRenderer implements GridRenderer {

	private GridCellRenderer cellRenderer;

	public BlockMapRenderer(GridCellRenderer cellRenderer) {
		this.cellRenderer = Objects.requireNonNull(cellRenderer);
	}

	@Override
	public GridCellRenderer getCellRenderer(int cell) {
		return cellRenderer;
	}

	@Override
	public void drawGrid(Graphics2D g, GridGraph2D<?, ?> grid) {
		grid.vertices().forEach(cell -> cellRenderer.drawCell(g, grid, cell));
	}

	@Override
	public void drawPassage(Graphics2D g, GridGraph2D<?, ?> grid, int either, int other, boolean visible) {
	}
}