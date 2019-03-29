package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.view.MapView;
import de.amr.demos.graph.pathfinding.view.MapWindow;
import de.amr.demos.graph.pathfinding.view.PathFinderView;
import de.amr.demos.graph.pathfinding.view.PathFinderWindow;
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

		PathFinderView pathFinderView = new PathFinderView();
		MapView mapView = new MapView();

		Controller controller = new Controller(model);
		controller.setPathFinderView(pathFinderView);
		controller.setMapView(mapView);
		controller.startSelectedPathFinder();

		MapWindow mapWindow = new MapWindow(mapView);
		mapWindow.pack();
		mapWindow.setLocationRelativeTo(null);
		mapWindow.setVisible(true);

		PathFinderWindow pathFinderWindow = new PathFinderWindow(pathFinderView);
		pathFinderWindow.pack();
		pathFinderWindow.setLocation(20, 20);
		pathFinderWindow.setVisible(true);
	}
}