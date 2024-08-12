package de.amr.demos.graph.pathfinding.view;

import de.amr.demos.graph.pathfinding.controller.ExecutionMode;
import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.controller.RenderingStyle;
import de.amr.demos.graph.pathfinding.controller.TopologySelection;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.swing.MySwing;
import net.miginfocom.swing.MigLayout;
import org.tinylog.Logger;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

import static de.amr.swing.MySwing.comboSelection;
import static de.amr.swing.MySwing.selectComboNoAction;

/**
 * View for editing settings and showing path-finder results.
 * 
 * @author Armin Reichert
 */
public class ConfigView extends JPanel {

	private PathFinderModel model;
	private PathFinderController controller;

	private final JTabbedPane tabbedPane;
	private final JPanel tabMap;
	private final JPanel tabPathFinding;
	private final JPanel bottomPane;
	private final JSpinner spinnerMapSize;
	private final JComboBox<TopologySelection> comboTopology;
	private final JComboBox<ExecutionMode> comboExecutionMode;
	private final ResultsTable tableResults;
	private final JCheckBox cbShowCost;
	private final JCheckBox cbShowParent;
	private final JComboBox<RenderingStyle> comboStyle;
	private final JSlider sliderDelay;
	private final JScrollPane scrollPaneTableResults;
	private final HelpPanel helpPanel;
	private final JButton btnRunAnimation;
	private final JButton btnFinishAnimation;
	private final JLabel lblStepbystep;
	private final JLabel lblNewLabel;
	private final JLabel lblTotalCells;
	private final JLabel lblDelay;

	private Action actionSelectTopology;
	private Action actionSelectExecutionMode;
	private Action actionSelectStyle;
	private Action actionStartSelectedPathFinder;
	private Action actionStepPathFinders;
	private Action actionFinishPathFinders;
	private Action actionShowCost;
	private Action actionShowParent;
	private ChangeListener onMapSizeChange;
	private ChangeListener onDelayChange;

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


		createActions();
	}

	private void createActions() {
		actionSelectTopology = MySwing.action("Select Topology", e -> {
			controller.changeTopology(comboSelection(comboTopology));
		});

		actionSelectExecutionMode = MySwing.action("Select Execution Mode", e -> {
			controller.changeExecutionMode(comboSelection(comboExecutionMode));
		});

		actionSelectStyle = MySwing.action("Select Map Style", e -> {
			controller.changeStyle(comboSelection(comboStyle));
		});

		actionStartSelectedPathFinder = MySwing.action("Start", e -> {
			controller.runBothFirstStep(true);
			updateViewState();
		});

		actionStepPathFinders = MySwing.action("Steps", e -> {
			JComponent source = (JComponent) e.getSource();
			int numSteps = (Integer) source.getClientProperty("numSteps");
			controller.runBothNumSteps(numSteps);
			updateViewState();
		});

		actionFinishPathFinders = MySwing.action("Finish", e -> {
			controller.runBothRemainingSteps();
			updateViewState();
		});

		actionShowCost = MySwing.action("Show Cost", e -> controller.showCost(cbShowCost.isSelected()));

		actionShowParent = MySwing.action("Show Parent",
			e -> controller.showParent(cbShowParent.isSelected()));

		onMapSizeChange = e -> controller.changeMapSize((int) spinnerMapSize.getValue());
		onDelayChange = e -> controller.setAnimationDelay(sliderDelay.getValue());

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
		int minTableWidth = 400;
		int minTableHeight = model.numResults() * 20;
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

	public void updateView() {
		tableResults.dataChanged();
		selectComboNoAction(comboTopology, model.getMap().getTopology() == Grid4Topology.get() ? 0 : 1);
		spinnerMapSize.setValue(model.getMapSize());
		cbShowCost.setSelected(controller.isShowingCost());
		cbShowParent.setSelected(controller.isShowingParent());
		selectComboNoAction(comboStyle, controller.getStyle());
		updateViewState();
		Logger.trace("ConfigView updated: %s", this);
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
}