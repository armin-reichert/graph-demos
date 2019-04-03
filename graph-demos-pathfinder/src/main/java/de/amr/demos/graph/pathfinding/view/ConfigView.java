package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

import javax.swing.AbstractAction;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.demos.graph.pathfinding.controller.ExecutionMode;
import de.amr.demos.graph.pathfinding.controller.PathFinderController;
import de.amr.demos.graph.pathfinding.controller.TopologySelection;
import de.amr.demos.graph.pathfinding.controller.action.RunPathFinderAnimations;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.RenderingStyle;
import de.amr.graph.grid.impl.Top4;
import net.miginfocom.swing.MigLayout;

/**
 * View with settings and results for the path finder demo application.
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
	private JButton btnRun;
	private JButton btnFinish;
	private JLabel lblStepbystep;
	private JLabel lblAnimation;
	private JPanel panel_1;
	private JLabel lblNewLabel;
	private JLabel lblTotalCells;
	private JComboBox<String> comboAlgorithmRight;
	private JComboBox<String> comboAlgorithmLeft;

	// Actions

	private Action action(String name, BiConsumer<PathFinderController, ActionEvent> handler) {
		return new AbstractAction(name) {

			@Override
			public void actionPerformed(ActionEvent e) {
				handler.accept(controller, e);
			}
		};
	}

	private Action actionDoNothing = action("Do Nothing", (controller, e) -> {
	});

	private Action actionSelectTopology = action("Select Topology", (controller, e) -> {
		controller.changeTopology(selection(comboTopology));
	});

	private Action actionSelectAlgorithmLeft = action("Select Left Algorithm", (controller, e) -> {
		controller.changeLeftPathFinder(comboAlgorithmLeft.getSelectedIndex());
	});

	private Action actionSelectAlgorithmRight = action("Select Right Algorithm", (controller, e) -> {
		controller.changeRightPathFinder(comboAlgorithmRight.getSelectedIndex());
	});

	private Action actionSelectExecutionMode = action("Select Execution Mode", (controller, e) -> {
		controller.changeExecutionMode(selection(comboExecutionMode));
	});

	private Action actionSelectStyle = action("Select Map Style", (controller, e) -> {
		controller.changeStyle(selection(comboStyle));
	});

	private Action actionStartSelectedPathFinder = action("Start", (controller, e) -> {
		controller.runBothFirstStep();
		updateViewState();
	});

	private Action actionStepPathFinders = action("Steps", (controller, e) -> {
		JComponent source = (JComponent) e.getSource();
		int numSteps = (Integer) source.getClientProperty("numSteps");
		controller.runBothNumSteps(numSteps);
		updateViewState();
	});

	private Action actionFinishPathFinders = action("Finish", (controller, e) -> {
		controller.runBothRemainingSteps();
		updateViewState();
	});

	private Action actionShowCost = action("Show Cost", (controller, e) -> {
		controller.showCost(cbShowCost.isSelected());
	});

	private Action actionShowParent = action("Show Parent", (controller, e) -> {
		controller.showParent(cbShowParent.isSelected());
	});

	private ChangeListener onMapSizeChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int newSize = (int) spinnerMapSize.getValue();
			controller.changeMapSize(newSize);
		}
	};

	public ConfigView() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));

		panelLayout = new JPanel();
		panelLayout.setOpaque(false);
		panelLayout.setMinimumSize(new Dimension(550, 10));
		add(panelLayout);
		panelLayout.setLayout(new MigLayout("", "[][10:10:10][]", "[][][][][][][][][][][][grow,bottom]"));

		JLabel lblMap = new JLabel("Map");
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
		lblPathFinding.setForeground(SystemColor.textHighlight);
		lblPathFinding.setFont(new Font("Arial Black", Font.PLAIN, 14));
		panelLayout.add(lblPathFinding, "cell 0 5 3 1,alignx leading");

		JLabel lblMapSize = new JLabel("Rows/Cols");
		panelLayout.add(lblMapSize, "cell 0 1,alignx trailing");

		spinnerMapSize = new JSpinner();
		lblMapSize.setLabelFor(spinnerMapSize);
		panelLayout.add(spinnerMapSize, "flowx,cell 2 1");

		comboAlgorithmLeft = new JComboBox<>();
		panelLayout.add(comboAlgorithmLeft, "flowx,cell 2 6,growx");

		comboAlgorithmRight = new JComboBox<>();
		panelLayout.add(comboAlgorithmRight, "cell 2 6,growx");

		lblNewLabel = new JLabel("Execution Mode");
		panelLayout.add(lblNewLabel, "cell 0 7,alignx trailing");

		comboExecutionMode = new JComboBox<>();
		lblNewLabel.setLabelFor(comboExecutionMode);
		panelLayout.add(comboExecutionMode, "cell 2 7,growx");

		lblStepbystep = new JLabel("Step-By-Step");
		panelLayout.add(lblStepbystep, "cell 0 8,alignx trailing");

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panelLayout.add(panel, "flowx,cell 2 8,alignx left");

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

		btnFinish = new JButton();
		btnFinish.setAction(actionFinishPathFinders);
		panel.add(btnFinish);

		lblAnimation = new JLabel("Animation");
		panelLayout.add(lblAnimation, "cell 0 9,alignx trailing,aligny center");

		JLabel lblAlgorithm = new JLabel("Algorithms");
		panelLayout.add(lblAlgorithm, "cell 0 6,alignx trailing");

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

		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panelLayout.add(panel_1, "flowx,cell 2 9,growx,aligny center");
		panel_1.setLayout(new MigLayout("", "[][grow]", "[]"));

		btnRun = new JButton();
		lblAnimation.setLabelFor(btnRun);
		panel_1.add(btnRun, "cell 0 0,aligny center");
		btnRun.setText("Run");

		sliderDelay = new JSlider();
		sliderDelay.setToolTipText("Delay");
		panel_1.add(sliderDelay, "cell 1 0,growx,aligny center");
		sliderDelay.setValue(0);
		sliderDelay.setMaximum(100 * 100);

		lblTotalCells = new JLabel("(### cells)");
		panelLayout.add(lblTotalCells, "cell 2 1");
	}

	public void init(PathFinderModel model, PathFinderController controller) {
		this.model = model;
		this.controller = controller;

		// path finder results table
		tableResults.init(model);
		int minTableWidth = 400, minTableHeight = model.numResults() * 20; // TODO
		scrollPaneTableResults.setPreferredSize(new Dimension(minTableWidth, minTableHeight));

		// others controls
		spinnerMapSize.setModel(new SpinnerNumberModel(model.getMapSize(), PathFinderModel.MIN_MAP_SIZE,
				PathFinderModel.MAX_MAP_SIZE, 1));
		spinnerMapSize.addChangeListener(onMapSizeChange);

		sliderDelay.setValue(controller.getAnimationDelay());
		sliderDelay.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setAnimationDelay(sliderDelay.getValue());
			}
		});

		comboTopology.setModel(new DefaultComboBoxModel<>(TopologySelection.values()));
		comboTopology.setSelectedItem(model.getMap().getTopology() == Top4.get() ? TopologySelection._4_NEIGHBORS
				: TopologySelection._8_NEIGHBORS);
		comboTopology.setAction(actionSelectTopology);

		comboAlgorithmLeft.setModel(new DefaultComboBoxModel<>(model.getPathFinderNames()));
		comboAlgorithmLeft.setSelectedIndex(model.getPathFinderIndex(controller.getLeftPathFinder()));
		comboAlgorithmLeft.setAction(actionSelectAlgorithmLeft);

		comboAlgorithmRight.setModel(new DefaultComboBoxModel<>(model.getPathFinderNames()));
		comboAlgorithmRight.setSelectedIndex(model.getPathFinderIndex(controller.getRightPathFinder()));
		comboAlgorithmRight.setAction(actionSelectAlgorithmRight);

		comboExecutionMode.setModel(new DefaultComboBoxModel<>(ExecutionMode.values()));
		comboExecutionMode.setSelectedItem(controller.getExecutionMode());
		comboExecutionMode.setAction(actionSelectExecutionMode);

		cbShowCost.setSelected(controller.isShowingCost());
		cbShowParent.setSelected(controller.isShowingParent());

		btnRun.setAction(new RunPathFinderAnimations(controller));

		updateViewState();
	}

	public void updateView() {
		tableResults.dataChanged();
		lblTotalCells.setText(String.format("(%d cells)", model.getMapSize() * model.getMapSize()));
		selectComboNoAction(comboAlgorithmLeft, controller.getLeftPathFinderIndex());
		selectComboNoAction(comboAlgorithmRight, controller.getRightPathFinderIndex());
		selectComboNoAction(comboTopology, model.getMap().getTopology() == Top4.get() ? 0 : 1);
		spinnerMapSize.setValue(model.getMapSize());
		updateViewState();
	}

	private void updateViewState() {
		boolean manual = controller.getExecutionMode() == ExecutionMode.MANUAL;
		actionStartSelectedPathFinder.setEnabled(manual);
		actionStepPathFinders.setEnabled(manual);
		actionFinishPathFinders.setEnabled(manual);
		btnRun.setEnabled(manual);
		scrollPaneTableResults.setVisible(controller.getExecutionMode() == ExecutionMode.ALL);
		cbShowCost.setVisible(comboStyle.getSelectedItem() == RenderingStyle.BLOCKS);
	}

	private static <T> T selection(JComboBox<T> combo) {
		return combo.getItemAt(combo.getSelectedIndex());
	}

	// TODO hack to avoid firing action events
	private void selectComboNoAction(JComboBox<?> combo, int index) {
		Action action = combo.getAction();
		combo.setAction(actionDoNothing);
		combo.setSelectedIndex(index);
		combo.setAction(action);
	}
}