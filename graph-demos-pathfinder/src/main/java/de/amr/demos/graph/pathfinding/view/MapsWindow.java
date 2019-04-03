package de.amr.demos.graph.pathfinding.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class MapsWindow extends JFrame {

	private MapView leftMapView;
	private MapView rightMapView;
	private ResizeHandler resizeHandler = new ResizeHandler();

	private class ResizeHandler implements ComponentListener {

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			int leftMapSize = Math.min(leftMapView.getWidth(), leftMapView.getHeight());
			int rightMapSize = Math.min(rightMapView.getWidth(), rightMapView.getHeight());
			int size = Math.min(leftMapSize, rightMapSize);
			leftMapView.setSize(size, size);
			leftMapView.setPreferredSize(new Dimension(size, size));
			leftMapView.updateMap();
			rightMapView.setSize(size, size);
			rightMapView.setPreferredSize(new Dimension(size, size));
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

		getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[grow]"));

		JLabel lblLeft = new JLabel("LEFT");
		lblLeft.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblLeft, "cell 0 0,grow");

		JLabel lblRight = new JLabel("RIGHT");
		lblRight.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblRight, "cell 1 0,grow");
	}

	public MapsWindow(MapView leftMapView, MapView rightMapView) {
		this();
		this.leftMapView = leftMapView;
		this.rightMapView = rightMapView;
		leftMapView.addComponentListener(resizeHandler);
		rightMapView.addComponentListener(resizeHandler);
		getContentPane().removeAll();
		getContentPane().add(leftMapView, "cell 0 0,grow");
		getContentPane().add(rightMapView, "cell 1 0,grow");
	}
}