package de.amr.demos.graph.rendering;

import java.awt.EventQueue;

import javax.swing.JFrame;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import java.awt.BorderLayout;
import java.awt.Color;


public class GridCanvasTestApp extends JFrame {

	public static void main(String[] args) {
		EventQueue.invokeLater(GridCanvasTestApp::new);
	}
	
	
	public GridCanvasTestApp() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		GridCanvas gridCanvas = new GridCanvas();
		gridCanvas.setCentered(true);
		gridCanvas.setCellSize(40);
		gridCanvas.setBackground(Color.ORANGE);
		getContentPane().add(gridCanvas, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

}
