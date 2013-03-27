package org.bigwiv.blastgraph.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.bigwiv.blastgraph.workstatus.StatusChangeListener;
import org.bigwiv.blastgraph.workstatus.WorkStatus;


public abstract class AbstractFrame extends JFrame {
	/**
	 * use this to fire some status changes when
	 * doing some long running tasks
	 */
	protected WorkStatus workStatus;

	/**
	 * @param title
	 */
	public AbstractFrame(String title) {
		super(title);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.workStatus = new WorkStatus();
		
		//basic setting of this frame
	}

	public void setStatusChangeListener(StatusChangeListener listener) {
		workStatus.setStatusChangeListener(listener);
	}
	
	protected void showError(String message) {
		workStatus.setError(message);
		JOptionPane.showMessageDialog(this, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
