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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.command.FiltCommand;
import org.bigwiv.blastgraph.command.GenomeNumFiltCommand;
import org.bigwiv.blastgraph.global.Global;


/**
 * @author yeyanbo
 *
 */
public class GenomeNumFiltDialog extends JDialog implements ActionListener, ItemListener {
	private GenomeNumFiltCommand command;
	private JLabel genomeNumLabel, maxCutLabel, filtTypeLabel, cutoffLabel;
	private String[] filtType = { "Evalue", "Coverage", "Coverage2"};
	private JTextField genomeNumField, maxCutField;
	private JComboBox filtTypeBox;
	private JTextField cutoffField;
	private JLabel genomeNumTip, maxCutTip, filtTypeTip, cutoffTip;
	//private JLabel applyToLabel;
	//private JRadioButton allRadio, curSubGraphRadio;
	private JButton filtButton;

	public GenomeNumFiltDialog(JFrame frame, GenomeNumFiltCommand command) {
		super(frame);
		this.command = command;
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
	}

	private void initComponents() {
		setTitle("GenomeNumFilt");

		JComponent pane;
		pane = (JComponent) getContentPane();
		pane.setLayout(new GridBagLayout());

		GridBagManager.reset();
		GridBagManager.GRID_BAG.fill = GridBagConstraints.HORIZONTAL;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.FIRST_LINE_START;
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.insets = new Insets(2, 2, 1, 1);
		
//		applyToLabel = new JLabel("Apply to:");
//		allRadio = new JRadioButton("All");
//		allRadio.setSelected(true);
//		curSubGraphRadio = new JRadioButton("Current subGraph");
//		
//		ButtonGroup group = new ButtonGroup();
//		group.add(allRadio);
//		group.add(curSubGraphRadio);
		
		genomeNumLabel = new JLabel("Genome Num:");
		genomeNumField = new JTextField();
		genomeNumField.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent arg0) {
				checkFiltButtonStatus();
			}
			
		});
		genomeNumTip = new JLabel("Num of Genomes in this graph");
		genomeNumTip.setForeground(Color.gray);
		
		maxCutLabel = new JLabel("Max Cut:");
		maxCutField = new JTextField();
		maxCutField.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent arg0) {
				checkFiltButtonStatus();
			}
			
		});
		maxCutTip = new JLabel("Max multiple to genomeNum a subgraph can have");
		maxCutTip.setForeground(Color.gray);
		
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

		GridBagManager.add(pane, genomeNumLabel, 0, 0, 1, 1);
		GridBagManager.add(pane, genomeNumTip, 3, 0, 1, 1);
		GridBagManager.add(pane, maxCutLabel, 0, 1, 1, 1);
		GridBagManager.add(pane, maxCutTip, 3, 1, 1, 1);
		GridBagManager.add(pane, filtTypeLabel, 0, 2, 1, 1);
		GridBagManager.add(pane, filtTypeTip, 3, 2, 1, 1);
		GridBagManager.add(pane, cutoffLabel, 0, 3, 1, 1);
		GridBagManager.add(pane, cutoffTip, 3, 3, 1, 1);
//		GridBagManager.add(pane, applyToLabel, 0, 2, 1, 1);
		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(pane, genomeNumField, 1, 0, 2, 1);
		GridBagManager.add(pane, maxCutField, 1, 1, 2, 1);
		GridBagManager.add(pane, filtTypeBox, 1, 2, 2, 1);
		GridBagManager.add(pane, cutoffField, 1, 3, 2, 1);
//		GridBagManager.add(pane, allRadio, 1, 2, 1, 1);
//		GridBagManager.add(pane, curSubGraphRadio, 2, 2, 1, 1);
		
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.NONE;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.CENTER;
		GridBagManager.add(pane, filtButton, 1, 4, 2, 1);
		

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
	}

	private void checkFiltButtonStatus() {
		boolean b1, b2, b3 = false;
		Pattern pattern;
		
		pattern = Pattern.compile("[1-9][0-9]*");
		String genomeNum = genomeNumField.getText();
		b1 = pattern.matcher(genomeNum).matches();

		pattern = Pattern.compile("[0-9]\\.[0-9]+|[0-9]+");
		String maxCut = maxCutField.getText();
		b2 = pattern.matcher(maxCut).matches();
		
		int index = filtTypeBox.getSelectedIndex();
		String cutoff = cutoffField.getText();
		switch (index) {
		case 0:
			pattern = Pattern.compile("1[Ee]-[0-9]+|[0-9]+\\.[0-9]+|[0-9]+");
			b3 = pattern.matcher(cutoff).matches();
			break;
		case 1:
			pattern = Pattern.compile("0\\.[0-9]+|[0-1]");
			b3 = pattern.matcher(cutoff).matches();
			break;
		case 2:
			pattern = Pattern.compile("0\\.[0-9]+|[0-1]");
			b3 = pattern.matcher(cutoff).matches();
			break;
		default:
			break;
		}
		
		filtButton.setEnabled(b1 && b2 && b3);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		int genomNum = Integer.parseInt(genomeNumField.getText());
		double maxCut = Double.parseDouble(maxCutField.getText());
		int index = filtTypeBox.getSelectedIndex();
		double cutoff = Double.parseDouble(cutoffField.getText());
		
		command.setMaxCut(maxCut);
		command.setGenomNum(genomNum);
		
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
			}
		}
		
		
		Global.COMMAND_MANAGER.putCommand(command,
				CommandManager.NEW_THREAD);
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
			filtTypeTip
					.setText("Product of Query&Subject Coverage");
			cutoffTip.setText("0.25, range(0~1)");
		}
		checkFiltButtonStatus();
		this.pack();
	}
}
