package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MapWindow extends JFrame {
	
	
	public MapWindow() {
		setTitle("Path Finder Demo Map");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	public MapWindow(MapView view) {
		this();
		getContentPane().add(view, BorderLayout.CENTER);
	}
}