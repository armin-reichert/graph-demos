package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;

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
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.RenderingStyle;
import de.amr.graph.grid.impl.Top4;
import net.miginfocom.swing.MigLayout;

/**
 * View with settings and results for the path finder demo application.
 * 
 * @author Armin Reichert
 */
public class ConfigurationView extends JPanel {

	private Action actionNone = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	};

	private Action actionSelectTopology = new AbstractAction("Select Topology") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.changeTopology(selection(comboTopology));
		}
	};

	private Action actionSelectAlgorithmLeft = new AbstractAction("Select Left Algorithm") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.changeLeftPathFinder(comboAlgorithmLeft.getSelectedIndex());
		}
	};

	private Action actionSelectAlgorithmRight = new AbstractAction("Select Right Algorithm") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.changeRightPathFinder(comboAlgorithmRight.getSelectedIndex());
		}
	};

	private Action actionSelectExecutionMode = new AbstractAction("Select Execution Mode") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.changeExecutionMode(selection(comboExecutionMode));
		}
	};

	private Action actionSelectStyle = new AbstractAction("Select Map Style") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.changeStyle(selection(comboStyle));
		}
	};

	private Action actionRunPathFinderAnimations = new AbstractAction("Animate") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.runPathFinderAnimations();
		}
	};

	private Action actionStartSelectedPathFinder = new AbstractAction("Start") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.startPathFinders();
			updateViewState();
		}
	};

	private Action actionStepPathFinders = new AbstractAction("Steps") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent source = (JComponent) e.getSource();
			int numSteps = (Integer) source.getClientProperty("numSteps");
			controller.runPathFinderSteps(numSteps);
			updateViewState();
		}
	};

	private Action actionFinishPathFinders = new AbstractAction("Finish") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.finishPathFinders();
			updateViewState();
		}
	};

	private Action actionShowCost = new AbstractAction("Show Cost") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.showCost(cbShowCost.isSelected());
		}
	};

	private Action actionShowParent = new AbstractAction("Show Parent") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.showParent(cbShowParent.isSelected());
		}
	};

	private ChangeListener onMapSizeChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int newSize = (int) spinnerMapSize.getValue();
			controller.changeMapSize(newSize);
		}
	};

	private PathFinderModel model;
	private PathFinderController controller;

	private JPanel panelActions;
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
	private JButton btnFinish;
	private JLabel lblStepbystep;
	private JLabel lblAnimation;
	private JPanel panel_1;
	private JLabel lblNewLabel;
	private JLabel lblTotalCells;
	private JComboBox<String> comboAlgorithmRight;
	private JComboBox<String> comboAlgorithmLeft;

	public ConfigurationView() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));

		panelActions = new JPanel();
		panelActions.setOpaque(false);
		panelActions.setMinimumSize(new Dimension(550, 10));
		add(panelActions);
		panelActions.setLayout(new MigLayout("", "[grow,center][grow]", "[][][][][][][][][][][][grow,bottom]"));

		JLabel lblMap = new JLabel("Map");
		panelActions.add(lblMap, "cell 0 0 2 1,alignx leading");
		lblMap.setForeground(SystemColor.textHighlight);
		lblMap.setFont(new Font("Arial Black", Font.PLAIN, 14));

		cbShowCost = new JCheckBox("Show Cost");
		cbShowCost.setAction(actionShowCost);
		panelActions.add(cbShowCost, "flowx,cell 1 4");

		cbShowParent = new JCheckBox("Show Parent");
		cbShowParent.setAction(actionShowParent);
		panelActions.add(cbShowParent, "cell 1 4");

		lblPathFinding = new JLabel("Path Finding");
		lblPathFinding.setForeground(SystemColor.textHighlight);
		lblPathFinding.setFont(new Font("Arial Black", Font.PLAIN, 14));
		panelActions.add(lblPathFinding, "cell 0 5 2 1,alignx leading");

		JLabel lblMapSize = new JLabel("Rows/Cols");
		panelActions.add(lblMapSize, "cell 0 1,alignx trailing");

		spinnerMapSize = new JSpinner();
		lblMapSize.setLabelFor(spinnerMapSize);
		panelActions.add(spinnerMapSize, "flowx,cell 1 1");

		comboAlgorithmLeft = new JComboBox<>();
		panelActions.add(comboAlgorithmLeft, "flowx,cell 1 6,growx");

		comboAlgorithmRight = new JComboBox<>();
		panelActions.add(comboAlgorithmRight, "cell 1 6,growx");

		lblNewLabel = new JLabel("Execution Mode");
		panelActions.add(lblNewLabel, "cell 0 7,alignx trailing");

		comboExecutionMode = new JComboBox<>();
		lblNewLabel.setLabelFor(comboExecutionMode);
		panelActions.add(comboExecutionMode, "cell 1 7,growx");

		lblStepbystep = new JLabel("Step-By-Step Execution");
		panelActions.add(lblStepbystep, "cell 0 8,alignx trailing");

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panelActions.add(panel, "flowx,cell 1 8,alignx left");

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

		lblAnimation = new JLabel("Animated Execution");
		panelActions.add(lblAnimation, "cell 0 9,alignx trailing,aligny center");

		JLabel lblAlgorithm = new JLabel("Algorithms");
		panelActions.add(lblAlgorithm, "cell 0 6,alignx trailing");

		JLabel lblTopology = new JLabel("Topology");
		panelActions.add(lblTopology, "flowy,cell 0 2,alignx trailing");

		comboTopology = new JComboBox<>();
		lblTopology.setLabelFor(comboTopology);
		panelActions.add(comboTopology, "cell 1 2,growx");

		JLabel lblStyle = new JLabel("Style");
		panelActions.add(lblStyle, "cell 0 3,alignx trailing");

		comboStyle = new JComboBox<>();
		lblStyle.setLabelFor(comboStyle);
		comboStyle.setAction(actionSelectStyle);
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		panelActions.add(comboStyle, "cell 1 3,growx");

		scrollPaneTableResults = new JScrollPane();
		panelActions.add(scrollPaneTableResults, "cell 0 10 2 1,growx");

		tableResults = new ResultsTable();
		tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableResults.setPreferredScrollableViewportSize(new Dimension(500, 300));
		tableResults.setFillsViewportHeight(true);
		tableResults.setEnabled(false);
		tableResults.setShowVerticalLines(false);
		scrollPaneTableResults.setViewportView(tableResults);

		helpPanel = new HelpPanel();
		helpPanel.setMinimumSize(new Dimension(500, 120));
		panelActions.add(helpPanel, "cell 0 11 2 1,growx");

		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panelActions.add(panel_1, "flowx,cell 1 9,growx,aligny center");
		panel_1.setLayout(new MigLayout("", "[][grow]", "[]"));

		JButton btnRun = new JButton();
		lblAnimation.setLabelFor(btnRun);
		panel_1.add(btnRun, "cell 0 0,aligny center");
		btnRun.setAction(actionRunPathFinderAnimations);
		btnRun.setText("Run");

		sliderDelay = new JSlider();
		sliderDelay.setToolTipText("Delay");
		panel_1.add(sliderDelay, "cell 1 0,growx,aligny center");
		sliderDelay.setValue(0);
		sliderDelay.setMaximum(100 * 100);

		lblTotalCells = new JLabel("(### cells)");
		panelActions.add(lblTotalCells, "cell 1 1");
	}

	private static <T> T selection(JComboBox<T> combo) {
		return combo.getItemAt(combo.getSelectedIndex());
	}

	// TODO hack to avoid firing action events
	private void selectComboNoAction(JComboBox<?> combo, int index) {
		Action action = combo.getAction();
		combo.setAction(actionNone);
		combo.setSelectedIndex(index);
		combo.setAction(action);
	}

	public void updateView() {
		tableResults.dataChanged();
		lblTotalCells.setText(String.format("(%d cells)", model.getMapSize() * model.getMapSize()));
		selectComboNoAction(comboAlgorithmLeft, controller.getLeftPathFinderIndex());
		selectComboNoAction(comboAlgorithmRight, controller.getRightPathFinderIndex());
		updateViewState();
	}

	private void updateViewState() {
		boolean manual = controller.getExecutionMode() == ExecutionMode.MANUAL;
		actionStartSelectedPathFinder.setEnabled(manual);
		actionStepPathFinders.setEnabled(manual);
		actionFinishPathFinders.setEnabled(manual);
		actionRunPathFinderAnimations.setEnabled(manual);
		sliderDelay.setEnabled(manual);
		scrollPaneTableResults.setVisible(controller.getExecutionMode() == ExecutionMode.ALL);
		cbShowCost.setVisible(comboStyle.getSelectedItem() == RenderingStyle.BLOCKS);
	}

	public void init(PathFinderModel model, PathFinderController controller) {
		this.model = model;
		this.controller = controller;

		// path finder results table
		tableResults.init(model);
		Dimension tableSize = new Dimension(500, PathFinderAlgorithm.values().length * 20);
		scrollPaneTableResults.setMinimumSize(tableSize);
		scrollPaneTableResults.setPreferredSize(tableSize);
		scrollPaneTableResults.setSize(tableSize);

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

		updateViewState();
	}
}