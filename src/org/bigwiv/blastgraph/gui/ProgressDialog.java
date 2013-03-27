/**
 * 
 */
package org.bigwiv.blastgraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.workstatus.StatusChangeListener;
import org.bigwiv.blastgraph.workstatus.WorkStatus;


/**
 * @author yeyanbo
 * 
 */
public class ProgressDialog extends JDialog implements StatusChangeListener,
		ActionListener {

	private JTextPane progressArea;
	private int maxProgressLine;
	private int progressLineCounter;
	private JButton cancelButton;
	private JButton clearButton;
	private CommandManager commandManager;

	/**
	 * @param frame
	 */
	public ProgressDialog(JFrame frame, CommandManager commandManager) {
		super(frame);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing( WindowEvent e){
				JDialog dialog = (JDialog)e.getSource();
				dialog.setVisible(false);
			}
		});
		this.progressLineCounter = 0;
		this.setMaxProgressLine(50);
		this.commandManager = commandManager;
		
		initComponents();
	}

	private void initComponents() {
		setTitle("Progress");
		setMinimumSize(new Dimension(400, 300));
		progressArea = new JTextPane();
		progressArea.setEditable(false);
		progressArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(progressArea), BorderLayout.CENTER);

		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(clearButton, BorderLayout.WEST);
		buttonPanel.add(cancelButton, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
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
		if (ws.getStatusType() == ws.MESSAGE) {
			appendProgress(ws.toString(), Color.BLACK);
		} else if (ws.getStatusType() == ws.WARNING) {
			appendProgress(ws.toString(), Color.YELLOW);
		} else {
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
