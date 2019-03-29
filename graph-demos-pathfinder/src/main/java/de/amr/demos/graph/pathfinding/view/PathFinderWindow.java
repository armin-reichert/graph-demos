package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

public class PathFinderWindow extends JFrame {

	public PathFinderWindow() {
		getContentPane().setBackground(Color.WHITE);
		setTitle("Path Finder Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	public void setView(PathFinderView view) {
		getContentPane().removeAll();
		getContentPane().add(view, BorderLayout.CENTER);
	}
}