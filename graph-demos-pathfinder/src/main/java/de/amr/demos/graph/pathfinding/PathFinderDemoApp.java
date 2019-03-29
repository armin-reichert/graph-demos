package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.view.PathFinderView;
import de.amr.demos.graph.pathfinding.view.PathFinderWindow;
import de.amr.demos.graph.pathfinding.view.MapView;
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
		PathFinderModel model = new PathFinderModel(23, Top8.get());
		Controller controller = new Controller(model);
		
		PathFinderView pathFinderView = new PathFinderView();
		pathFinderView.init(model, controller);
		controller.setPathFinderView(pathFinderView);
		PathFinderWindow pathFinderWindow = new PathFinderWindow();
		pathFinderWindow.setView(pathFinderView);
		
		MapView mapView = new MapView();
		mapView.init(model, controller);
		controller.setMapView(mapView);
		MapWindow mapWindow = new MapWindow();
		mapWindow.setView(mapView);
		
		controller.startSelectedPathFinder();
		
		mapWindow.pack();
		mapWindow.setLocationRelativeTo(null);
		mapWindow.setVisible(true);
		
		pathFinderWindow.pack();
		pathFinderWindow.setLocation(20, 20);
		pathFinderWindow.setVisible(true);
	}
}