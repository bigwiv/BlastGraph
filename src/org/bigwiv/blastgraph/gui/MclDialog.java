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

package org.bigwiv.blastgraph.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.bigwiv.bio.phylogeny.DistanceType;
import org.bigwiv.bio.phylogeny.TreeType;
import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.command.MclCommand;
import org.bigwiv.blastgraph.global.Global;


public class MclDialog extends JDialog implements ActionListener{
	private MclCommand command;
	
	private JComboBox weightTypeBox;
	private JTextField lowCutoff, highCutoff, inflation;
	private JButton runButton, cancelButton;
	
	public MclDialog(JFrame frame, MclCommand command) {
		super(frame);
		this.command = command;
		initComponents();
	}
	
	
	private void initComponents(){
		this.setMinimumSize(new Dimension(400, 300));

		JComponent mainPane = (JComponent) this.getContentPane();
		mainPane.setLayout(new GridBagLayout());

		JLabel weightTypeLabel = new JLabel("Weight Type: ");
		weightTypeBox = new JComboBox(new String[] {"Evalue", "Score", "Score Density" });
		weightTypeBox.addActionListener(this);

		JLabel lowCutoffLabel = new JLabel("Low Cutoff: ");
		lowCutoff = new JTextField("1");
		
		JLabel highCutoffLabel = new JLabel("High Cutoff: ");
		highCutoff = new JTextField("1E-10");
		
		JLabel inflationLabel = new JLabel("MCL Inflation: ");
		inflation = new JTextField("1.5");

		runButton = new JButton("Run");
		runButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		GridBagManager.reset();
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTHWEST;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.BOTH;
		GridBagManager.GRID_BAG.insets = new Insets(1, 1, 1, 1);
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;

		GridBagManager.add(mainPane, weightTypeLabel, 0, 0, 1, 1);
		GridBagManager.add(mainPane, lowCutoffLabel, 0, 1, 1, 1);
		GridBagManager.add(mainPane, highCutoffLabel, 0, 2, 1, 1);
		GridBagManager.add(mainPane, inflationLabel, 0, 3, 1, 1);

		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(mainPane, weightTypeBox, 1, 0, 1, 1);
		GridBagManager.add(mainPane, lowCutoff, 1, 1, 1, 1);
		GridBagManager.add(mainPane, highCutoff, 1, 2, 1, 1);
		GridBagManager.add(mainPane, inflation, 1, 3, 1, 1);

		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.NONE;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.SOUTH;
		GridBagManager.add(mainPane, runButton, 0, 4, 1, 1);

		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(mainPane, cancelButton, 1, 4, 1, 1);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source  = e.getSource();
		
		if(source.equals(runButton)){
			int weightType = weightTypeBox.getSelectedIndex();
			double low = Double.parseDouble(lowCutoff.getText());
			double high = Double.parseDouble(highCutoff.getText());
			double i = Double.parseDouble(inflation.getText());
			
			this.command.setWeightType(weightType);
			this.command.setLowCutoff(low);
			this.command.setHighCutoff(high);
			this.command.setInflation(i);
			
			Global.COMMAND_MANAGER.putCommand(command, CommandManager.NEW_THREAD);
			
			this.setVisible(false);
			this.dispose();
			
		}else if (source.equals(cancelButton)) {
			this.setVisible(false);
			this.dispose();
		} 
	}
	
}
