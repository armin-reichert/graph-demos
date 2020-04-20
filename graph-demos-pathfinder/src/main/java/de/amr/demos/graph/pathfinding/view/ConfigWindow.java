package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;

public class ConfigWindow extends JFrame {

	public ConfigWindow() {
		setResizable(false);
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		setTitle("Path Finder Demo - Settings");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	public ConfigWindow(ConfigView pathFinderView) {
		this();
		getContentPane().add(pathFinderView, BorderLayout.CENTER);
	}
}