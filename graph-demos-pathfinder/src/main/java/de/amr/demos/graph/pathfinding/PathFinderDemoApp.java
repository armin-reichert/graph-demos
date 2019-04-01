package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.view.MapView;
import de.amr.demos.graph.pathfinding.view.MapWindow;
import de.amr.demos.graph.pathfinding.view.ConfigurationView;
import de.amr.demos.graph.pathfinding.view.ConfigurationWindow;
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
		PathFinderModel model = new PathFinderModel(23, Top8.get());

		ConfigurationView pathFinderView = new ConfigurationView();
		MapView mapView = new MapView();

		PathFinderController controller = new PathFinderController(model);
		controller.setPathFinderView(pathFinderView);
		controller.setMapView(mapView);
		controller.startSelectedPathFinder();

		MapWindow mapWindow = new MapWindow(mapView);
		mapWindow.pack();
		mapWindow.setLocationRelativeTo(null);
		mapWindow.setVisible(true);

		ConfigurationWindow pathFinderWindow = new ConfigurationWindow(pathFinderView);
		pathFinderWindow.pack();
		pathFinderWindow.setLocation(20, 20);
		pathFinderWindow.setVisible(true);
	}
}