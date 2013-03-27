package org.bigwiv.blastgraph.global;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FrameProxy {
	private JFrame proxiedFrame;
	
	public void setFrame(JFrame frame){
		this.proxiedFrame = frame;
	}
	
	public JFrame getFrame(){
		return proxiedFrame;
	}
	
	public void showError(String message) {
		Global.WORK_STATUS.setError(message);
		JOptionPane.showMessageDialog(proxiedFrame, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
