package de.amr.demos.graph.pathfinding.view;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.AStarSearch;

/**
 * Table with path finder results.
 * 
 * @author Armin Reichert
 */
public class ResultsTable extends JTable {

	private static class ColumnSpec {

		final String title;
		final Class<?> type;
		final Function<Object, String> formatter;

		ColumnSpec(String title, Class<?> type, Function<Object, String> formatter) {
			this.title = title;
			this.type = type;
			this.formatter = formatter;
		}

		ColumnSpec(String title, Class<?> type) {
			this(title, type, Object::toString);
		}
	}

	private static final ColumnSpec[] COLUMNS = {
		//@formatter:off
		new ColumnSpec(
				"Algorithm", 
				String.class), 
		new ColumnSpec(
				"Time [ms]", 
				Float.class, 
				time -> String.format("%.1f", time)), 
		new ColumnSpec(
				"Path length", 
				Integer.class, 
				len -> (int) len == -1 ? "\u221e" : String.format("%d", (int)len)), 
		new ColumnSpec(
				"Path cost", 
				Double.class,
			// display real value multiplied by 10
				cost -> (double) cost == Path.INFINITE_COST ? "\u221e" : String.format("%.0f", 10 * (double) cost)),
		new ColumnSpec(
				"Loss", 
				Double.class, 
				loss -> loss.equals(Path.INFINITE_COST) ? "" : String.format("%.0f %%", loss)),
		new ColumnSpec(
				"Touched", Long.class), 
		new ColumnSpec(
				"Closed", Long.class), 
		//@formatter:on
	};

	private static class ResultsTableModel extends AbstractTableModel {

		private final PathFinderModel model;

		public ResultsTableModel(PathFinderModel model) {
			this.model = model;
		}

		@Override
		public int getRowCount() {
			return PathFinderAlgorithm.values().length;
		}

		@Override
		public int getColumnCount() {
			return COLUMNS.length;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMNS[column].title;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return COLUMNS[column].type;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PathFinderResult result = model.getResultAtIndex(rowIndex);
			switch (columnIndex) {
			case 0:
				return result.getPathFinderName();
			case 1:
				return result.getRunningTimeMillis();
			case 2:
				return result.getPathLength();
			case 3:
				return result.getCost();
			case 4:
				var astar = model.getPathFinderByClass(AStarSearch.class);
				if (astar.isPresent()) {
					double cost = result.getCost();
					var astarResult = model.getResult(astar.get());
					double optimum = astarResult.get().getCost();
					if (cost != 0 && optimum != 0) {
						return 100 * (result.getCost() - optimum) / optimum;
					}
				}
				return Path.INFINITE_COST;
			case 5:
				return result.getNumTouchedVertices();
			case 6:
				return result.getNumClosedVertices();
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	private void formatColumns() {
		for (int i = 0; i < COLUMNS.length; ++i) {
			final ColumnSpec column = COLUMNS[i];
			getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
						int rowIndex, int columnIndex) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, columnIndex);
					if (value == null) {
						return this;
					}
					if (column.type == Float.class || column.type == Double.class || column.type == Integer.class
							|| column.type == Long.class) {
						setHorizontalAlignment(TRAILING);
						setText(column.formatter.apply(value));
					}
					return this;
				}
			});
		}
		getColumnModel().getColumn(0).setPreferredWidth(160);
	}

	public void init(PathFinderModel model) {
		setModel(new ResultsTableModel(model));
		formatColumns();
	}

	public void dataChanged() {
		if (getModel() != null) {
			ResultsTableModel tm = (ResultsTableModel) getModel();
			tm.fireTableDataChanged();
		}
	}
}