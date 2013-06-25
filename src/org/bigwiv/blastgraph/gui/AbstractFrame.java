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
