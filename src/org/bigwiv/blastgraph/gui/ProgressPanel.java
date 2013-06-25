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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.workstatus.StatusChangeListener;
import org.bigwiv.blastgraph.workstatus.WorkStatus;


public class ProgressPanel extends JPanel implements StatusChangeListener , ActionListener{
	private JTextPane progressArea;
	private int maxProgressLine;
	private int progressLineCounter;
	private JButton cancelButton;
	private JButton clearButton;
	private CommandManager commandManager;

	public ProgressPanel(CommandManager commandManager) {
		this.progressLineCounter = 0;
		this.setMaxProgressLine(50);
		this.commandManager = commandManager;
		initComponents();
	}

	private void initComponents() {
		progressArea = new JTextPane(){
			public boolean getScrollableTracksViewportWidth(){
				  return false;
				  }
				 
				 public void setSize(Dimension d){
				  if(d.width<getParent().getSize().width){
				   d.width=getParent().getSize().width;
				  }
				  d.width+=100;
				  super.setSize(d);
				 }
		};
		progressArea.setEditable(false);
		progressArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(progressArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(clearButton, BorderLayout.WEST);
		buttonPanel.add(cancelButton, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * @return the maxProgressLine
	 */
	public int getMaxProgressLine() {
		return maxProgressLine;
	}

	/**
	 * @param maxProgressLine
	 *            the maxProgressLine to set
	 */
	public void setMaxProgressLine(int maxProgressLine) {
		this.maxProgressLine = maxProgressLine;
	}

	public void appendProgress(final String progress, final Color color) {
		Runnable progressUpdate = new Runnable() {
			@Override
			public void run() {
				if (progressLineCounter < maxProgressLine) {
					insertDocument(progress + "\n", color);
					progressLineCounter++;
				} else {
					int index = progressArea.getText().indexOf("\n");
					progressArea.setText(progressArea.getText().substring(
							index + 1));
					insertDocument(progress + "\n", color);
				}
			}
		};

		SwingUtilities.invokeLater(progressUpdate);
	}

	private void insertDocument(String text, Color textColor) {
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, textColor);
		StyleConstants.setFontSize(set, 12);
		Document doc = progressArea.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), text, set);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void clearProgress() {
		progressArea.setText("");
		progressLineCounter = 0;
	}

	private void cancle() {
		if(commandManager.isRunning()){
			int option = JOptionPane.showConfirmDialog(this,
					"Are you sure to cancle this task?", "Warning",
					JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.NO_OPTION){
				return;
			}else {
				if(commandManager.isCancelable()){
					commandManager.stop();
				}else {
					JOptionPane.showMessageDialog(this, "Cannot cancle this task!");
				}
			}
		}else {
			JOptionPane.showMessageDialog(this, "No running task!");
		}
	}
	
	@Override
	public void onStatusChange(WorkStatus ws) {
		if(ws.getStatusType() == ws.MESSAGE){
			appendProgress(ws.toString(), Color.BLACK);
		}else if(ws.getStatusType() == ws.WARNING){
			appendProgress(ws.toString(), Color.YELLOW);
		}else {
			appendProgress(ws.toString(), Color.RED);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		if (button.equals(clearButton)) {
			clearProgress();
		} else {
			cancle();
		}
	}

}
