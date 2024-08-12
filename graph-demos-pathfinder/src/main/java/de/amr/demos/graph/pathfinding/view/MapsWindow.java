package de.amr.demos.graph.pathfinding.view;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;
import de.amr.swing.MySwing;
import net.miginfocom.swing.MigLayout;
import org.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static de.amr.swing.MySwing.selectComboNoAction;

public class MapsWindow extends JFrame {

	private PathFinderModel model;
	private PathFinderController controller;
	private MapView leftMapView;
	private MapView rightMapView;
	private ResizeHandler resizeHandler = new ResizeHandler();
	private JPanel panelLeftMap;
	private JPanel panelRightMap;
	private JComboBox<String> comboLeftPathFinder;
	private JComboBox<String> comboRightPathFinder;
	private JLabel lblResultsRight;
	private JLabel lblResultsLeft;

	private class ResizeHandler extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			var containerSize = e.getComponent().getSize();
			int containerWidth = containerSize.width;
			int containerHeight = containerSize.height;
			Logger.info("Container resized to %s", containerSize);
			var newSize = Math.min(0.45 * containerWidth, 0.85 * containerHeight);
			resizeMapViews((int) newSize);
		}
	}

	public void updateWindow() {
		updateTitle();
		selectComboNoAction(comboLeftPathFinder, controller.getLeftPathFinderIndex());
		selectComboNoAction(comboRightPathFinder, controller.getRightPathFinderIndex());
		lblResultsLeft.setText(formatResult(controller.getLeftPathFinderIndex()));
		lblResultsRight.setText(formatResult(controller.getRightPathFinderIndex()));
	}

	public void resizeMapViews(int newSize) {
		if (resizeMapView(leftMapView, newSize)) {
			Logger.info("Resized left view to %s", leftMapView.getSize());
		}
		if (resizeMapView(rightMapView, newSize)) {
			Logger.info("Resized right view to %s", rightMapView.getSize());
		}
	}

	private boolean resizeMapView(MapView mapView, int size) {
		var dim = new Dimension(size, size);
		if (!dim.equals(mapView.getSize())) {
			mapView.setSize(dim);
			mapView.setPreferredSize(dim);
			mapView.updateMap(true);
			return true;
		}
		return false;
	}

	private void updateTitle() {
		setTitle(String.format("Path Finder Demo Map (%d x %d = %d cells)", model.getMap().numCols(),
				model.getMap().numRows(), model.getMap().numVertices()));
	}

	private String formatResult(int pathFinderIndex) {
		PathFinderResult result = model.getResultAtIndex(pathFinderIndex);
		return String.format("Path length: %d, Cost: %.2f, Touched cells %d, Time: %.0f ms", result.getPathLength(),
				result.getCost(), result.getNumTouchedVertices(), result.getRunningTimeMillis());
	}

	public MapsWindow() {
		getContentPane().setBackground(new Color(255, 255, 255));
		setTitle("Path Finder Demo Map");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][grow][]"));
		comboLeftPathFinder = new JComboBox<>();
		comboLeftPathFinder.setModel(
				new DefaultComboBoxModel<>(new String[] { "Breadth-First Search", "Dijkstra Search", "AStar Search" }));
		comboLeftPathFinder.setSelectedIndex(0);
		getContentPane().add(comboLeftPathFinder, "cell 0 0,alignx center");
		comboRightPathFinder = new JComboBox<>();
		comboRightPathFinder.setModel(
				new DefaultComboBoxModel<>(new String[] { "Breadth-First Search", "Dijkstra Search", "AStar Search" }));
		comboRightPathFinder.setSelectedIndex(1);
		getContentPane().add(comboRightPathFinder, "cell 1 0,alignx center");
		panelLeftMap = new JPanel();
		panelLeftMap.setOpaque(false);
		getContentPane().add(panelLeftMap, "cell 0 1,grow");
		panelLeftMap.setLayout(new MigLayout("", "[]", "[]"));
		panelRightMap = new JPanel();
		panelRightMap.setOpaque(false);
		getContentPane().add(panelRightMap, "cell 1 1,grow");
		panelRightMap.setLayout(new MigLayout("", "[]", "[]"));
		lblResultsLeft = new JLabel("");
		getContentPane().add(lblResultsLeft, "cell 0 2,alignx center,aligny top");
		lblResultsRight = new JLabel("");
		getContentPane().add(lblResultsRight, "cell 1 2,alignx center,aligny top");
	}

	public MapsWindow(PathFinderController controller, MapView leftMapView, MapView rightMapView) {
		this();

		this.controller = controller;
		this.model = controller.getModel();
		this.leftMapView = leftMapView;
		this.rightMapView = rightMapView;

		getContentPane().addComponentListener(resizeHandler);

		updateTitle();

		panelLeftMap.add(leftMapView, "cell 0 0,grow");
		panelRightMap.add(rightMapView, "cell 0 0,grow");

		comboLeftPathFinder.setModel(new DefaultComboBoxModel<>(model.getPathFinderNames()));
		comboLeftPathFinder.setSelectedIndex(controller.getLeftPathFinderIndex());
		comboLeftPathFinder.setAction(MySwing.action("", e -> {
			int newSelection = comboLeftPathFinder.getSelectedIndex();
			if (newSelection != comboRightPathFinder.getSelectedIndex()) {
				controller.changeLeftPathFinder(newSelection);
			} else {
				updateWindow();
			}
		}));

		comboRightPathFinder.setModel(new DefaultComboBoxModel<>(model.getPathFinderNames()));
		comboRightPathFinder.setSelectedIndex(controller.getRightPathFinderIndex());
		comboRightPathFinder.setAction(MySwing.action("", e -> {
			int newSelection = comboRightPathFinder.getSelectedIndex();
			if (newSelection != comboLeftPathFinder.getSelectedIndex()) {
				controller.changeRightPathFinder(newSelection);
			} else {
				updateWindow();
			}
		}));
	}
}