/*
 * BlastGraph: a comparative genomics tool
 * Copyright (C) 2013  Yanbo Ye (yeyanbo289@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * 
 */
package org.bigwiv.blastgraph.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.command.FiltCommand;
import org.bigwiv.blastgraph.global.Global;


/**
 * @author yeyanbo
 * 
 */
public class FiltDialog extends JDialog implements ActionListener, ItemListener {
	private FiltCommand command;
	private String[] filtType = { "Evalue", "Coverage", "Coverage2",
			"ScoreDensity", "PercentageIdentity", "PercentagePositive", "CoverageLength"};
	private JComboBox filtTypeBox;
	private JTextField cutoffField;
	private JLabel filtTypeLabel, cutoffLabel;
	private JLabel filtTypeTip, cutoffTip;
	private JLabel applyToLabel;
	private JRadioButton allRadio, curGraphRadio;
	private JButton filtButton;

	public FiltDialog(JFrame frame, FiltCommand command) {
		super(frame);
		this.command = command;
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
	}

	private void initComponents() {
		setTitle("Filt Graph Edges");

		JComponent pane;
		pane = (JComponent) getContentPane();
		pane.setLayout(new GridBagLayout());

		GridBagManager.reset();
		GridBagManager.GRID_BAG.fill = GridBagConstraints.HORIZONTAL;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.FIRST_LINE_START;
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.insets = new Insets(2, 2, 1, 1);

		applyToLabel = new JLabel("Apply to:");
		allRadio = new JRadioButton("All");
		curGraphRadio = new JRadioButton("Current");
		curGraphRadio.setSelected(true);

		ButtonGroup group = new ButtonGroup();
		group.add(allRadio);
		group.add(curGraphRadio);

		filtTypeLabel = new JLabel("Filt by:");
		filtTypeBox = new JComboBox(filtType);
		filtTypeBox.addItemListener(this);
		filtTypeTip = new JLabel("evalue of hit");
		filtTypeTip.setForeground(Color.gray);

		cutoffLabel = new JLabel("Cutoff:");
		cutoffField = new JTextField();
		cutoffField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				checkFiltButtonStatus();
			}
		});
		cutoffTip = new JLabel("10, 0.1, 1e-10 etc.");
		cutoffTip.setForeground(Color.gray);

		filtButton = new JButton("Filt");
		filtButton.setEnabled(false);
		filtButton.addActionListener(this);

		GridBagManager.add(pane, filtTypeLabel, 0, 0, 1, 1);
		GridBagManager.add(pane, cutoffLabel, 0, 1, 1, 1);
		GridBagManager.add(pane, filtTypeTip, 3, 0, 1, 1);
		GridBagManager.add(pane, cutoffTip, 3, 1, 1, 1);
		GridBagManager.add(pane, applyToLabel, 0, 2, 1, 1);
		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(pane, filtTypeBox, 1, 0, 2, 1);
		GridBagManager.add(pane, cutoffField, 1, 1, 2, 1);
		GridBagManager.add(pane, curGraphRadio, 1, 2, 1, 1);
		GridBagManager.add(pane, allRadio, 2, 2, 1, 1);

		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.NONE;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.CENTER;
		GridBagManager.add(pane, filtButton, 0, 3, 3, 1);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
	}

	private void checkFiltButtonStatus() {
		int index = filtTypeBox.getSelectedIndex();
		String text = cutoffField.getText();
		Pattern pattern;
		switch (index) {
		case 0:
			pattern = Pattern.compile("1[Ee]-[0-9]+|[0-9]+\\.[0-9]+|[0-9]+");
			filtButton.setEnabled(pattern.matcher(text).matches());
			break;
		case 1:
			pattern = Pattern.compile("0\\.[0-9]+|[0-1]");
			filtButton.setEnabled(pattern.matcher(text).matches());
			break;
		case 2:
			pattern = Pattern.compile("0\\.[0-9]+|[0-1]");
			filtButton.setEnabled(pattern.matcher(text).matches());
			break;
		case 3:
			pattern = Pattern.compile("[0-2]\\.[0-9]+|[0-3]");
			filtButton.setEnabled(pattern.matcher(text).matches());
			break;
		case 4:
			pattern = Pattern
					.compile("([0-9]|[1-9][0-9])\\.[0-9]+|[0-9]|[1-9][0-9]|100");
			filtButton.setEnabled(pattern.matcher(text).matches());
			break;
		case 5:
			pattern = Pattern
					.compile("([0-9]|[1-9][0-9])\\.[0-9]+|[0-9]|[1-9][0-9]|100");
			filtButton.setEnabled(pattern.matcher(text).matches());
			break;
		case 6:
			pattern = Pattern
					.compile("[0-9]+");
			filtButton.setEnabled(pattern.matcher(text).matches());
			break;
		default:
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		int index = filtTypeBox.getSelectedIndex();
		boolean isAll = allRadio.isSelected();
		double cutoff = Double.parseDouble(cutoffField.getText());
		if (button.equals(filtButton)) {
			if (index == 0) {
				command.setFiltBy(FiltCommand.EVALUE);
				command.setCutoff(cutoff);
			} else if (index == 1) {
				command.setFiltBy(FiltCommand.COVERAGE);
				command.setCutoff(cutoff);
			} else if (index == 2) {
				command.setFiltBy(FiltCommand.COVERAGE2);
				command.setCutoff(cutoff);
			} else if (index == 3) {
				command.setFiltBy(FiltCommand.SCORE_DENSITY);
				command.setCutoff(cutoff);
			} else if (index == 4) {
				command.setFiltBy(FiltCommand.PERCENTAGE_IDENTITIES);
				command.setCutoff(cutoff);
			} else if (index == 5){
				command.setFiltBy(FiltCommand.PERCENTAGE_POSITIVES);
				command.setCutoff(cutoff);
			} else if (index == 6){
				command.setFiltBy(FiltCommand.COVERAGE_LENGTH);
				command.setCutoff(cutoff);
			}

			if (isAll) {
				command.setIndex(-1);
			}

		}
		Global.COMMAND_MANAGER.putCommand(command,
				CommandManager.CURRENT_THREAD);
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		int index = filtTypeBox.getSelectedIndex();
		if (index == 0) {
			filtTypeTip.setText("Evalue of Hit");
			cutoffTip.setText("10, 0.1, 1e-10 etc.");
		} else if (index == 1) {
			filtTypeTip.setText("(QC*SC)/(QL*SL)");
			cutoffTip.setText("0.25, range(0~1)");
		} else if (index == 2) {
			filtTypeTip.setText("Min(QC,SC)/Max(QL,SL)");
			cutoffTip.setText("0.5, range(0~1)");
		}else if (index == 3) {
			filtTypeTip.setText("Average ScoreDensity of HSPs");
			cutoffTip.setText("0.25, range(0~3)");
		} else if (index == 4) {
			filtTypeTip.setText("Average PercentageIdentity of HSPs");
			cutoffTip.setText("range(0~100)");
		} else if(index == 5) {
			filtTypeTip.setText("Average PercentagePositive of HSPs");
			cutoffTip.setText("range(0~100)");
		}else if(index == 6){
			filtTypeTip.setText("(QC + SC)/2");
			cutoffTip.setText("range(10~)");
		}
		checkFiltButtonStatus();
		this.pack();
	}
}
