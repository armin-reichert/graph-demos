package de.amr.demos.graph.pathfinding.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import net.miginfocom.swing.MigLayout;

public class MapsWindow extends JFrame {

	private MapView leftMapView;
	private MapView rightMapView;
	private ResizeHandler resizeHandler = new ResizeHandler();
	private JPanel panelLeftMap;
	private JPanel panelRightMap;
	private JComboBox<PathFinderAlgorithm> comboLeftPathFinder;
	private JComboBox<PathFinderAlgorithm> comboRightPathFinder;

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

	public MapsWindow() {
		getContentPane().setBackground(Color.WHITE);
		setTitle("Path Finder Demo Map");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[grow][]"));
		panelLeftMap = new JPanel();
		panelLeftMap.setOpaque(false);
		getContentPane().add(panelLeftMap, "cell 0 0,grow");
		panelLeftMap.setLayout(new MigLayout("", "[]", "[]"));
		panelRightMap = new JPanel();
		panelRightMap.setOpaque(false);
		getContentPane().add(panelRightMap, "cell 1 0,grow");
		panelRightMap.setLayout(new MigLayout("", "[]", "[]"));
		comboLeftPathFinder = new JComboBox<>();
		getContentPane().add(comboLeftPathFinder, "cell 0 1,growx");
		comboRightPathFinder = new JComboBox<>();
		getContentPane().add(comboRightPathFinder, "cell 1 1,growx");
	}

	public MapsWindow(MapView leftMapView, MapView rightMapView) {
		this();
		this.leftMapView = leftMapView;
		this.rightMapView = rightMapView;
		panelLeftMap.add(leftMapView, "cell 0 0,grow");
		panelRightMap.add(rightMapView, "cell 0 0,grow");
		getContentPane().addComponentListener(resizeHandler);
	}
}