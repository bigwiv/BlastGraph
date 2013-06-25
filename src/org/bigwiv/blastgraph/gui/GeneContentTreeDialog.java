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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.bigwiv.bio.phylogeny.BitArray;
import org.bigwiv.bio.phylogeny.DistanceType;
import org.bigwiv.bio.phylogeny.PhyloAlgorithms;
import org.bigwiv.bio.phylogeny.ReSamplingType;
import org.bigwiv.bio.phylogeny.Tree;
import org.bigwiv.bio.phylogeny.TreeTools;
import org.bigwiv.bio.phylogeny.TreeType;
import org.bigwiv.blastgraph.gui.table.GeneContentModel;


/**
 * @author yeyanbo
 * 
 */
public class GeneContentTreeDialog extends JDialog implements ActionListener {
	private JComboBox distanceBox, treeBox;
	private JLabel sampleTimeLabel;
	private JTextField sampleTimeField;
	private JButton runButton, cancelButton;

	private GeneContentModel model;

	public GeneContentTreeDialog(Frame frame, GeneContentModel model) {
		super(frame);
		this.model = model;
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
	}

	private void initComponents() {
		this.setMinimumSize(new Dimension(400, 300));

		JComponent mainPane = (JComponent) this.getContentPane();
		mainPane.setLayout(new GridBagLayout());

		JLabel distanceLabel = new JLabel("Distance Type: ");
		distanceBox = new JComboBox(new DistanceType[] { DistanceType.SMD,
				DistanceType.JACCARD, DistanceType.Snel });
		distanceBox.addActionListener(this);

		JLabel treeLabel = new JLabel("Tree Type: ");
		treeBox = new JComboBox(new TreeType[] { TreeType.UPGMA, TreeType.NJ });
		treeBox.addActionListener(this);

		sampleTimeLabel = new JLabel("Bootstrap: ");
		sampleTimeField = new JTextField();

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

		GridBagManager.add(mainPane, distanceLabel, 0, 0, 1, 1);
		GridBagManager.add(mainPane, treeLabel, 0, 1, 1, 1);
		GridBagManager.add(mainPane, sampleTimeLabel, 0, 2, 1, 1);

		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(mainPane, distanceBox, 1, 0, 1, 1);
		GridBagManager.add(mainPane, treeBox, 1, 1, 1, 1);
		GridBagManager.add(mainPane, sampleTimeField, 1, 2, 1, 1);

		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.NONE;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.SOUTH;
		GridBagManager.add(mainPane, runButton, 0, 3, 1, 1);

		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(mainPane, cancelButton, 1, 3, 1, 1);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source.equals(runButton)) {
			// species names for tree labels
			Vector<String> rowHeader = model.getRowHeader();
			// parameters
			DistanceType distanceType = (DistanceType) distanceBox
					.getSelectedItem();
			TreeType treeType = (TreeType) treeBox.getSelectedItem();
			int sampleTime = Integer.parseInt(sampleTimeField.getText());
			
			Map<String, BitArray> geneContent = new HashMap<String, BitArray>();
			
			BitArray[] bitArrays = new BitArray[rowHeader.size()];

			for (int i = 0; i < rowHeader.size(); i++) {
				bitArrays[i] = new BitArray(model.getColumnCount());
				for (int j = 0; j < model.getColumnCount(); j++) {
					if ((Integer) (model.getValueAt(i, j)) > 0) {
						bitArrays[i].set(j);
					}
				}
				geneContent.put(rowHeader.get(i), bitArrays[i]);
			}
			
			ReSamplingType rsType = ReSamplingType.Bootstrap;
			
			if (distanceType.equals(DistanceType.Snel)) {
				rsType = ReSamplingType.DeleteHalfJackknife;
			} 
			

			ArrayList<Tree> trees = PhyloAlgorithms.reSample(bitArrays, rowHeader, sampleTime, distanceType, treeType, rsType);
			
			Tree consTree = PhyloAlgorithms.consensusTree(trees);

			
			
			TreeFrame frame = new TreeFrame(consTree);
			frame.getTreePanel().setGeneContent(geneContent);
			frame.setSize(500, 400);
			frame.setVisible(true);
			this.setVisible(false);
			this.dispose();
		} else if (source.equals(cancelButton)) {
			this.setVisible(false);
			this.dispose();
		} else if (source.equals(distanceBox)) {
			if (distanceBox.getSelectedItem().equals(DistanceType.Snel)) {
				sampleTimeLabel.setText("Jackknife: ");
			} else {
				sampleTimeLabel.setText("Bootstrap: ");
			}
		}

	}
}
