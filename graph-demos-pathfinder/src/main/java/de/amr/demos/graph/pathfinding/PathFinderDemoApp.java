package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.view.ConfigurationWindow;
import de.amr.demos.graph.pathfinding.view.MapWindow;
import de.amr.graph.grid.impl.Top8;

/**
 * Demo application for path finder algorithms.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoApp {

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
		controller.getConfigurationView().init(model, controller);
		controller.startPathFinders();

		ConfigurationWindow pathFinderWindow = new ConfigurationWindow(controller.getConfigurationView());
		pathFinderWindow.pack();
		pathFinderWindow.setLocation(20, 20);
		pathFinderWindow.setVisible(true);
		
		MapWindow mapWindow = new MapWindow(controller.getLeftMapView(), controller.getRightMapView());
		mapWindow.pack();
		Point right = pathFinderWindow.getLocationOnScreen();
		right.move(pathFinderWindow.getWidth() + 20, 20);
		mapWindow.setLocation(right);
		mapWindow.setVisible(true);
	}
}