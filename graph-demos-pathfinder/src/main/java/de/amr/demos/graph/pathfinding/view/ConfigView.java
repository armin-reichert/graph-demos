package de.amr.demos.graph.pathfinding.view;

import static de.amr.swing.Swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.demos.graph.pathfinding.controller.ExecutionMode;
import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.controller.RenderingStyle;
import de.amr.demos.graph.pathfinding.controller.TopologySelection;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.graph.grid.impl.Top4;
import de.amr.swing.Swing;
import net.miginfocom.swing.MigLayout;

/**
 * View for editing settings and showing path-finder results.
 * 
 * @author Armin Reichert
 */
public class ConfigView extends JPanel {

	private PathFinderModel model;
	private PathFinderController controller;

	private JPanel panelLayout;
	private JSpinner spinnerMapSize;
	private JComboBox<TopologySelection> comboTopology;
	private JComboBox<ExecutionMode> comboExecutionMode;
	private ResultsTable tableResults;
	private JCheckBox cbShowCost;
	private JCheckBox cbShowParent;
	private JLabel lblPathFinding;
	private JComboBox<RenderingStyle> comboStyle;
	private JSlider sliderDelay;
	private JScrollPane scrollPaneTableResults;
	private HelpPanel helpPanel;
	private JButton btnRunAnimation;
	private JButton btnFinishAnimation;
	private JLabel lblStepbystep;
	private JLabel lblNewLabel;
	private JLabel lblTotalCells;
	private JLabel lblDelay;

	public ConfigView() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));

		panelLayout = new JPanel();
		panelLayout.setOpaque(false);
		panelLayout.setMinimumSize(new Dimension(550, 10));
		add(panelLayout);
		panelLayout.setLayout(new MigLayout("", "[][10][]", "[][][][][][][][][][][][grow,bottom]"));

		JLabel lblMap = new JLabel("Map");
		lblMap.setBorder(new EmptyBorder(3, 0, 3, 0));
		panelLayout.add(lblMap, "cell 0 0 3 1,alignx leading");
		lblMap.setForeground(SystemColor.textHighlight);
		lblMap.setFont(new Font("Arial Black", Font.PLAIN, 14));

		cbShowCost = new JCheckBox("Show Cost");
		cbShowCost.setAction(actionShowCost);
		panelLayout.add(cbShowCost, "flowx,cell 2 4");

		cbShowParent = new JCheckBox("Show Parent");
		cbShowParent.setAction(actionShowParent);
		panelLayout.add(cbShowParent, "cell 2 4");

		lblPathFinding = new JLabel("Path Finding");
		lblPathFinding.setBorder(new EmptyBorder(12, 0, 3, 0));
		lblPathFinding.setForeground(SystemColor.textHighlight);
		lblPathFinding.setFont(new Font("Arial Black", Font.PLAIN, 14));
		panelLayout.add(lblPathFinding, "cell 0 5 3 1,alignx leading");

		JLabel lblMapSize = new JLabel("Rows/Cols");
		panelLayout.add(lblMapSize, "cell 0 1,alignx trailing");

		spinnerMapSize = new JSpinner();
		lblMapSize.setLabelFor(spinnerMapSize);
		panelLayout.add(spinnerMapSize, "flowx,cell 2 1");

		lblNewLabel = new JLabel("Execution Mode");
		panelLayout.add(lblNewLabel, "cell 0 6,alignx trailing");

		comboExecutionMode = new JComboBox<>();
		lblNewLabel.setLabelFor(comboExecutionMode);
		panelLayout.add(comboExecutionMode, "cell 2 6,growx");

		lblStepbystep = new JLabel("Step-By-Step");
		panelLayout.add(lblStepbystep, "cell 0 7,alignx trailing,aligny baseline");

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panelLayout.add(panel, "flowx,cell 2 7,alignx left,aligny baseline");

		JButton btnStart = new JButton();
		lblStepbystep.setLabelFor(btnStart);
		btnStart.setAction(actionStartSelectedPathFinder);
		panel.add(btnStart);

		JButton btnStep1 = new JButton();
		btnStep1.setAction(actionStepPathFinders);
		btnStep1.putClientProperty("numSteps", 1);
		btnStep1.setText("+1");
		panel.add(btnStep1);

		JButton btnSteps5 = new JButton();
		btnSteps5.setAction(actionStepPathFinders);
		btnSteps5.putClientProperty("numSteps", 5);
		btnSteps5.setText("+5");
		panel.add(btnSteps5);

		JButton btnSteps10 = new JButton();
		btnSteps10.setAction(actionStepPathFinders);
		btnSteps10.putClientProperty("numSteps", 10);
		btnSteps10.setText("+10");
		panel.add(btnSteps10);

		JButton btnSteps50 = new JButton();
		btnSteps50.setAction(actionStepPathFinders);
		btnSteps50.putClientProperty("numSteps", 50);
		btnSteps50.setText("+50");
		panel.add(btnSteps50);

		btnFinishAnimation = new JButton();
		btnFinishAnimation.setAction(actionFinishPathFinders);
		panel.add(btnFinishAnimation);

		JLabel lblTopology = new JLabel("Topology");
		panelLayout.add(lblTopology, "flowy,cell 0 2,alignx trailing");

		comboTopology = new JComboBox<>();
		lblTopology.setLabelFor(comboTopology);
		panelLayout.add(comboTopology, "cell 2 2,growx");

		JLabel lblStyle = new JLabel("Style");
		panelLayout.add(lblStyle, "cell 0 3,alignx trailing");

		comboStyle = new JComboBox<>();
		lblStyle.setLabelFor(comboStyle);
		comboStyle.setAction(actionSelectStyle);
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		panelLayout.add(comboStyle, "cell 2 3,growx");

		btnRunAnimation = new JButton();
		panelLayout.add(btnRunAnimation, "cell 2 8,alignx center");
		btnRunAnimation.setText("Run Path Finder");

		lblDelay = new JLabel("Delay [ms]");
		panelLayout.add(lblDelay, "cell 0 9,alignx trailing,aligny center");

		sliderDelay = new JSlider();
		lblDelay.setLabelFor(sliderDelay);
		panelLayout.add(sliderDelay, "cell 2 9,growx");
		sliderDelay.setMajorTickSpacing(500);
		sliderDelay.setMinorTickSpacing(100);
		sliderDelay.setPaintTicks(true);
		sliderDelay.setPaintLabels(true);
		sliderDelay.setToolTipText("Delay [ms]");
		sliderDelay.setValue(0);
		sliderDelay.setMaximum(1000);

		scrollPaneTableResults = new JScrollPane();
		panelLayout.add(scrollPaneTableResults, "cell 0 10 3 1,growx");

		tableResults = new ResultsTable();
		tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableResults.setPreferredScrollableViewportSize(new Dimension(500, 300));
		tableResults.setFillsViewportHeight(true);
		tableResults.setEnabled(false);
		tableResults.setShowVerticalLines(false);
		scrollPaneTableResults.setViewportView(tableResults);

		helpPanel = new HelpPanel();
		helpPanel.setMinimumSize(new Dimension(2, 150));
		panelLayout.add(helpPanel, "cell 0 11 3 1,growx");

		lblTotalCells = new JLabel("(### cells)");
		panelLayout.add(lblTotalCells, "cell 2 1");
	}

	/*
	 * Initialization using model and controller.
	 */
	public void init(PathFinderModel model, PathFinderController controller) {
		this.model = model;
		this.controller = controller;

		btnRunAnimation.setAction(controller.actionRunPathFinderAnimations());

		// path finder results table
		tableResults.init(model);
		int minTableWidth = 400, minTableHeight = model.numResults() * 20; // TODO
		scrollPaneTableResults.setPreferredSize(new Dimension(minTableWidth, minTableHeight));

		// others controls
		spinnerMapSize.setModel(new SpinnerNumberModel(model.getMapSize(), PathFinderModel.MIN_MAP_SIZE,
				PathFinderModel.MAX_MAP_SIZE, 1));
		spinnerMapSize.addChangeListener(onMapSizeChange);

		sliderDelay.setValue(controller.getAnimationDelay());
		sliderDelay.addChangeListener(onDelayChange);

		comboTopology.setModel(new DefaultComboBoxModel<>(TopologySelection.values()));
		comboTopology.setSelectedItem(model.getMap().getTopology() == Top4.get() ? TopologySelection._4_NEIGHBORS
				: TopologySelection._8_NEIGHBORS);
		comboTopology.setAction(actionSelectTopology);

		comboExecutionMode.setModel(new DefaultComboBoxModel<>(ExecutionMode.values()));
		comboExecutionMode.setSelectedItem(controller.getExecutionMode());
		comboExecutionMode.setAction(actionSelectExecutionMode);

		cbShowCost.setSelected(controller.isShowingCost());
		cbShowParent.setSelected(controller.isShowingParent());

		updateViewState();
	}

	// Actions and change listeners

	public void updateView() {
		tableResults.dataChanged();
		selectComboNoAction(comboTopology, model.getMap().getTopology() == Top4.get() ? 0 : 1);
		spinnerMapSize.setValue(model.getMapSize());
		cbShowCost.setSelected(controller.isShowingCost());
		cbShowParent.setSelected(controller.isShowingParent());
		selectComboNoAction(comboStyle, controller.getStyle());
		updateViewState();

		System.out.println("ConfigView updated: " + this);
	}

	private void updateViewState() {
		lblTotalCells.setText(String.format("(%d cells, min: %d, max: %d)",
				model.getMapSize() * model.getMapSize(), PathFinderModel.MIN_MAP_SIZE, PathFinderModel.MAX_MAP_SIZE));
		boolean manual = controller.getExecutionMode() == ExecutionMode.MANUAL;
		actionStartSelectedPathFinder.setEnabled(manual);
		actionStepPathFinders.setEnabled(manual);
		actionFinishPathFinders.setEnabled(manual);
		btnRunAnimation.setEnabled(manual);
		scrollPaneTableResults.setVisible(controller.getExecutionMode() == ExecutionMode.ALL);
		cbShowCost.setVisible(comboStyle.getSelectedItem() == RenderingStyle.BLOCKS);
	}

	public PathFinderController getController() {
		return controller;
	}

	private Action actionSelectTopology = Swing.action("Select Topology", e -> {
		getController().changeTopology(comboSelection(comboTopology));
	});

	private Action actionSelectExecutionMode = Swing.action("Select Execution Mode", e -> {
		getController().changeExecutionMode(comboSelection(comboExecutionMode));
	});

	private Action actionSelectStyle = Swing.action("Select Map Style", e -> {
		getController().changeStyle(comboSelection(comboStyle));
	});

	private Action actionStartSelectedPathFinder = Swing.action("Start", e -> {
		getController().runBothFirstStep(true);
		updateViewState();
	});

	private Action actionStepPathFinders = Swing.action("Steps", e -> {
		JComponent source = (JComponent) e.getSource();
		int numSteps = (Integer) source.getClientProperty("numSteps");
		getController().runBothNumSteps(numSteps);
		updateViewState();
	});

	private Action actionFinishPathFinders = Swing.action("Finish", e -> {
		getController().runBothRemainingSteps();
		updateViewState();
	});

	private Action actionShowCost = Swing.action("Show Cost", e -> {
		getController().showCost(cbShowCost.isSelected());
	});

	private Action actionShowParent = Swing.action("Show Parent", e -> {
		getController().showParent(cbShowParent.isSelected());
	});

	private ChangeListener onMapSizeChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int newSize = (int) spinnerMapSize.getValue();
			getController().changeMapSize(newSize);
		}
	};

	private ChangeListener onDelayChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			controller.setAnimationDelay(sliderDelay.getValue());
		}
	};
}