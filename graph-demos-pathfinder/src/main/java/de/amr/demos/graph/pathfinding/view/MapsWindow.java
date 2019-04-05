package de.amr.demos.graph.pathfinding.view;

import static de.amr.demos.graph.pathfinding.view.SwingGoodies.selectComboNoAction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import net.miginfocom.swing.MigLayout;

public class MapsWindow extends JFrame {

	private PathFinderController controller;
	private MapView leftMapView;
	private MapView rightMapView;
	private ResizeHandler resizeHandler = new ResizeHandler();
	private JPanel panelLeftMap;
	private JPanel panelRightMap;
	private JComboBox<String> comboLeftPathFinder;
	private JComboBox<String> comboRightPathFinder;

	private class ResizeHandler implements ComponentListener {

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			int size = Math.min(panelLeftMap.getWidth(), panelLeftMap.getHeight()) * 98 / 100;
			Dimension dim = new Dimension(size, size);
			leftMapView.setSize(dim);
			leftMapView.setPreferredSize(dim);
			leftMapView.updateMap();

			size = Math.min(panelRightMap.getWidth(), panelRightMap.getHeight()) * 98 / 100;
			dim = new Dimension(size, size);
			rightMapView.setSize(dim);
			rightMapView.setPreferredSize(dim);
			rightMapView.updateMap();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
	}

	public void updateWindow() {
		selectComboNoAction(comboLeftPathFinder, controller.getLeftPathFinderIndex());
		selectComboNoAction(comboRightPathFinder, controller.getRightPathFinderIndex());
	}

	public MapsWindow() {
		getContentPane().setBackground(Color.WHITE);
		setTitle("Path Finder Demo Map");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][grow]"));
		panelLeftMap = new JPanel();
		panelLeftMap.setOpaque(false);
		getContentPane().add(panelLeftMap, "cell 0 1,grow");
		panelLeftMap.setLayout(new MigLayout("", "[]", "[]"));
		panelRightMap = new JPanel();
		panelRightMap.setOpaque(false);
		getContentPane().add(panelRightMap, "cell 1 1,grow");
		panelRightMap.setLayout(new MigLayout("", "[]", "[]"));
		comboLeftPathFinder = new JComboBox<>();
		getContentPane().add(comboLeftPathFinder, "cell 0 0,growx");
		comboRightPathFinder = new JComboBox<>();
		getContentPane().add(comboRightPathFinder, "cell 1 0,growx");
	}

	public MapsWindow(PathFinderController controller, MapView leftMapView, MapView rightMapView) {
		this();

		this.controller = controller;
		this.leftMapView = leftMapView;
		this.rightMapView = rightMapView;

		panelLeftMap.add(leftMapView, "cell 0 0,grow");
		panelRightMap.add(rightMapView, "cell 0 0,grow");
		getContentPane().addComponentListener(resizeHandler);

		PathFinderModel model = controller.getModel();
		comboLeftPathFinder.setModel(new DefaultComboBoxModel<>(model.getPathFinderNames()));
		comboLeftPathFinder.setSelectedIndex(model.getPathFinderIndex(controller.getLeftPathFinder()));
		comboLeftPathFinder.setAction(SwingGoodies.createAction("", e -> {
			int newSelection = comboLeftPathFinder.getSelectedIndex();
			if (newSelection != comboRightPathFinder.getSelectedIndex()) {
				controller.changeLeftPathFinder(newSelection);
			}
			else {
				updateWindow();
			}
		}));

		comboRightPathFinder.setModel(new DefaultComboBoxModel<>(model.getPathFinderNames()));
		comboRightPathFinder.setSelectedIndex(model.getPathFinderIndex(controller.getRightPathFinder()));
		comboRightPathFinder.setAction(SwingGoodies.createAction("", e -> {
			int newSelection = comboRightPathFinder.getSelectedIndex();
			if (newSelection != comboLeftPathFinder.getSelectedIndex()) {
				controller.changeRightPathFinder(newSelection);
			}
			else {
				updateWindow();
			}
		}));
	}
}