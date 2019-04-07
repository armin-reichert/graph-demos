package de.amr.demos.graph.pathfinding.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;

public class SwingGoodies {

	public static final Action NULL_ACTION = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	};

	public static Action createAction(String name, ActionListener handler) {
		return new AbstractAction(name) {

			@Override
			public void actionPerformed(ActionEvent e) {
				handler.actionPerformed(e);
			}
		};
	}

	public static <T> T selection(JComboBox<T> combo) {
		return combo.getItemAt(combo.getSelectedIndex());
	}

	public static void selectComboNoAction(JComboBox<?> combo, int index) {
		selectComboNoAction(combo, combo.getModel().getElementAt(index));
	}

	public static void selectComboNoAction(JComboBox<?> combo, Object selection) {
		Action action = combo.getAction();
		combo.setAction(NULL_ACTION);
		combo.setSelectedItem(selection);
		combo.setAction(action);
	}

}