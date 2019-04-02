package de.amr.demos.graph.pathfinding.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;

public class MapWindow extends JFrame {

	public MapWindow() {
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

	public MapWindow(MapView leftMapView, MapView rightMapView) {
		this();
		getContentPane().removeAll();
		getContentPane().add(leftMapView, "cell 0 0,grow");
		getContentPane().add(rightMapView, "cell 1 0,grow");
	}
}