package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.view.ConfigWindow;
import de.amr.demos.graph.pathfinding.view.MapsWindow;
import de.amr.graph.grid.impl.Top8;

/**
 * Demo application for path finder algorithms.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoApp {

	public static final int MAP_VIEW_SIZE = Toolkit.getDefaultToolkit().getScreenSize().width * 30 / 100;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(PathFinderDemoApp::new);
	}

	public PathFinderDemoApp() {
		PathFinderModel model = new PathFinderModel(15, Top8.get());

		PathFinderController controller = new PathFinderController(model, 0, 1);
		controller.getConfigView().init(model, controller);

		ConfigWindow configWindow = new ConfigWindow(controller.getConfigView());
		configWindow.pack();
		configWindow.setLocation(5, 5);
		configWindow.setVisible(true);

		MapsWindow mapWindow = new MapsWindow(controller.getLeftMapView(), controller.getRightMapView());
		mapWindow.pack();
		Point right = configWindow.getLocation();
		right.move(configWindow.getWidth(), 5);
		mapWindow.setLocation(right);
		mapWindow.setVisible(true);
		
		controller.runBothFirstStep();
		
	}
}