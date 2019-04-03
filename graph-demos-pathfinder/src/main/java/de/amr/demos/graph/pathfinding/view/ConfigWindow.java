package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

public class ConfigWindow extends JFrame {

	public ConfigWindow() {
		getContentPane().setBackground(Color.WHITE);
		setTitle("Path Finder Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	public ConfigWindow(ConfigView pathFinderView) {
		this();
		getContentPane().add(pathFinderView, BorderLayout.CENTER);
	}
}