package org.bigwiv.blastgraph.gui;

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
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.command.FiltCommand;
import org.bigwiv.blastgraph.command.ViewNeighborCommand;
import org.bigwiv.blastgraph.global.Global;


public class NeighborViewDialog extends JDialog implements ActionListener{
	private ViewNeighborCommand command;
	private JLabel giLabel, levelLabel;
	private JTextField giField, levelField;
	private JButton viewButton;

	public NeighborViewDialog(JFrame frame, ViewNeighborCommand command) {
		super(frame);
		this.command = command;
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
	}
	
	private void initComponents(){
		setTitle("View Neighbor");
		setMinimumSize(new Dimension(200, 50));
		
		JComponent pane;
		pane = (JComponent) getContentPane();
		pane.setLayout(new GridBagLayout());

		GridBagManager.reset();
		GridBagManager.GRID_BAG.fill = GridBagConstraints.HORIZONTAL;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.FIRST_LINE_START;
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.insets = new Insets(2, 2, 1, 1);
		
		giLabel = new JLabel("Vertex GI");
		giField = new JTextField();
		
		levelLabel = new JLabel("Level");
		levelField = new JTextField();
		
		viewButton = new JButton("View");
		viewButton.addActionListener(this);

		GridBagManager.add(pane, giLabel, 0, 0, 1, 1);
		GridBagManager.add(pane, levelLabel, 0, 1, 1, 1);
		
		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(pane, giField, 1, 0, 1, 1);
		GridBagManager.add(pane, levelField, 1, 1, 1, 1);
		
		GridBagManager.GRID_BAG.fill = GridBagConstraints.CENTER;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.CENTER;
		GridBagManager.add(pane, viewButton, 0, 2, 2, 1);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		command.setDepth(Integer.parseInt(levelField.getText()));
		command.setHitVertex(Global.graph.getVertex(giField.getText()));
		command.execute();
		
		this.setVisible(false);
		this.dispose();
	}
}
