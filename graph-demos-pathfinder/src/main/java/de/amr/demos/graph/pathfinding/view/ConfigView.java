package de.amr.demos.graph.pathfinding.view;

import static de.amr.swing.Swing.comboSelection;
import static de.amr.swing.Swing.selectComboNoAction;

import java.awt.Color;
import java.awt.Dimension;

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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.demos.graph.pathfinding.controller.ExecutionMode;
import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.controller.RenderingStyle;
import de.amr.demos.graph.pathfinding.controller.TopologySelection;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.graph.grid.impl.Grid4Topology;
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

	private JPanel bottomPane;
	private JSpinner spinnerMapSize;
	private JComboBox<TopologySelection> comboTopology;
	private JComboBox<ExecutionMode> comboExecutionMode;
	private ResultsTable tableResults;
	private JCheckBox cbShowCost;
	private JCheckBox cbShowParent;
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
		setPreferredSize(new Dimension(450, 600));
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[539px]", "[grow,fill][grow,fill]"));

		bottomPane = new JPanel();
		bottomPane.setOpaque(false);
		add(bottomPane, "cell 0 1,alignx center");
		bottomPane.setLayout(new MigLayout("", "[][10][]", "[grow][grow,center]"));

		scrollPaneTableResults = new JScrollPane();
		scrollPaneTableResults.setPreferredSize(new Dimension(500, 400));
		bottomPane.add(scrollPaneTableResults, "cell 0 0 3 1,grow");

		tableResults = new ResultsTable();
		tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableResults.setFillsViewportHeight(true);
		tableResults.setEnabled(false);
		tableResults.setShowVerticalLines(false);
		scrollPaneTableResults.setViewportView(tableResults);

		helpPanel = new HelpPanel();
		helpPanel.setMinimumSize(new Dimension(2, 150));
		bottomPane.add(helpPanel, "cell 0 1 3 1,growx");

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, "cell 0 0,growx");

		tabMap = new JPanel();
		tabMap.setBackground(Color.WHITE);
		tabbedPane.addTab("Map", null, tabMap, null);
		tabbedPane.setEnabledAt(0, true);
		tabMap.setLayout(new MigLayout("", "[][10px:10px:10px][grow]", "[][][][]"));

		JLabel lblMapSize = new JLabel("Rows/Cols");
		tabMap.add(lblMapSize, "cell 0 0");

		spinnerMapSize = new JSpinner();
		tabMap.add(spinnerMapSize, "flowx,cell 2 0");
		lblMapSize.setLabelFor(spinnerMapSize);

		JLabel lblTopology = new JLabel("Topology");
		tabMap.add(lblTopology, "cell 0 1");

		comboTopology = new JComboBox<>();
		tabMap.add(comboTopology, "cell 2 1,growx");
		lblTopology.setLabelFor(comboTopology);

		JLabel lblStyle = new JLabel("Style");
		tabMap.add(lblStyle, "cell 0 2");

		comboStyle = new JComboBox<>();
		tabMap.add(comboStyle, "cell 2 2,growx");
		comboStyle.setAction(actionSelectStyle);
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		lblStyle.setLabelFor(comboStyle);

		lblTotalCells = new JLabel("(### cells)");
		tabMap.add(lblTotalCells, "cell 2 0");

		cbShowCost = new JCheckBox("Show Cost");
		tabMap.add(cbShowCost, "flowx,cell 2 3");
		cbShowCost.setAction(actionShowCost);

		cbShowParent = new JCheckBox("Show Parent");
		tabMap.add(cbShowParent, "cell 2 3");
		cbShowParent.setAction(actionShowParent);

		tabPathFinding = new JPanel();
		tabPathFinding.setBackground(Color.WHITE);
		tabbedPane.addTab("Pathfinding", null, tabPathFinding, null);
		tabPathFinding.setLayout(new MigLayout("", "[][10px:10px:10px][grow]", "[][][][]"));

		lblNewLabel = new JLabel("Execution Mode");
		tabPathFinding.add(lblNewLabel, "cell 0 0");

		comboExecutionMode = new JComboBox<>();
		tabPathFinding.add(comboExecutionMode, "cell 2 0,growx");
		lblNewLabel.setLabelFor(comboExecutionMode);

		lblStepbystep = new JLabel("Step-By-Step");
		tabPathFinding.add(lblStepbystep, "cell 0 1");

		JButton btnStart = new JButton();
		tabPathFinding.add(btnStart, "flowx,cell 2 1");
		btnStart.setAction(actionStartSelectedPathFinder);
		lblStepbystep.setLabelFor(btnStart);

		JButton btnStep1 = new JButton();
		tabPathFinding.add(btnStep1, "cell 2 1");
		btnStep1.setAction(actionStepPathFinders);
		btnStep1.putClientProperty("numSteps", 1);
		btnStep1.setText("+1");

		JButton btnSteps5 = new JButton();
		tabPathFinding.add(btnSteps5, "cell 2 1");
		btnSteps5.setAction(actionStepPathFinders);
		btnSteps5.putClientProperty("numSteps", 5);
		btnSteps5.setText("+5");

		JButton btnSteps10 = new JButton();
		tabPathFinding.add(btnSteps10, "cell 2 1");
		btnSteps10.setAction(actionStepPathFinders);
		btnSteps10.putClientProperty("numSteps", 10);
		btnSteps10.setText("+10");

		JButton btnSteps50 = new JButton();
		tabPathFinding.add(btnSteps50, "cell 2 1");
		btnSteps50.setAction(actionStepPathFinders);
		btnSteps50.putClientProperty("numSteps", 50);
		btnSteps50.setText("+50");

		btnFinishAnimation = new JButton();
		tabPathFinding.add(btnFinishAnimation, "cell 2 1");
		btnFinishAnimation.setAction(actionFinishPathFinders);

		btnRunAnimation = new JButton();
		tabPathFinding.add(btnRunAnimation, "cell 2 2,alignx center");
		btnRunAnimation.setText("Run Path Finder");

		lblDelay = new JLabel("Delay [ms]");
		tabPathFinding.add(lblDelay, "cell 0 3");

		sliderDelay = new JSlider();
		tabPathFinding.add(sliderDelay, "cell 2 3,growx");
		sliderDelay.setMajorTickSpacing(500);
		sliderDelay.setMinorTickSpacing(100);
		sliderDelay.setPaintTicks(true);
		sliderDelay.setPaintLabels(true);
		sliderDelay.setToolTipText("Delay [ms]");
		sliderDelay.setValue(0);
		sliderDelay.setMaximum(1000);
		lblDelay.setLabelFor(sliderDelay);
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
		spinnerMapSize.setModel(
				new SpinnerNumberModel(model.getMapSize(), PathFinderModel.MIN_MAP_SIZE, PathFinderModel.MAX_MAP_SIZE, 1));
		spinnerMapSize.addChangeListener(onMapSizeChange);

		sliderDelay.setValue(controller.getAnimationDelay());
		sliderDelay.addChangeListener(onDelayChange);

		comboTopology.setModel(new DefaultComboBoxModel<>(TopologySelection.values()));
		comboTopology.setSelectedItem(model.getMap().getTopology() == Grid4Topology.get() ? TopologySelection._4_NEIGHBORS
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
		selectComboNoAction(comboTopology, model.getMap().getTopology() == Grid4Topology.get() ? 0 : 1);
		spinnerMapSize.setValue(model.getMapSize());
		cbShowCost.setSelected(controller.isShowingCost());
		cbShowParent.setSelected(controller.isShowingParent());
		selectComboNoAction(comboStyle, controller.getStyle());
		updateViewState();

		System.out.println("ConfigView updated: " + this);
	}

	private void updateViewState() {
		lblTotalCells.setText(String.format("(%d cells, min: %d, max: %d)", model.getMapSize() * model.getMapSize(),
				PathFinderModel.MIN_MAP_SIZE, PathFinderModel.MAX_MAP_SIZE));
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
	private JTabbedPane tabbedPane;
	private JPanel tabMap;
	private JPanel tabPathFinding;
}