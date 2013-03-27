package org.bigwiv.blastgraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.global.Global;


public class SettingDialog extends JDialog implements ActionListener {
	private JTabbedPane settingTabPane;
	private JPanel buttonPanel;

	private JButton blastButton, mclButton, okButton, cancelButton; // helpButton;
	private JTextField maxVertices, csvSeparator, blastPath, mclPath;
	private JComboBox csvQuotes;
	private String[] quotes = new String[] { "\"", "\'", "null" };

	public SettingDialog(JFrame frame) {
		super(frame);
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
		initSetting();
	}

	private void initComponents() {
		// GridBagManager.GRID_BAG init
		GridBagManager.reset();
		GridBagManager.GRID_BAG.fill = GridBagConstraints.HORIZONTAL;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTH;
		GridBagManager.GRID_BAG.insets = new Insets(1, 1, 1, 1);

		setTitle("Setting");
		setMinimumSize(new Dimension(400, 300));
		JComponent pane;
		pane = (JComponent) getContentPane();

		settingTabPane = new JTabbedPane();
		initSettingTabPane();
		pane.add(BorderLayout.CENTER, settingTabPane);
		initButtonPanel();
		pane.add(BorderLayout.SOUTH, buttonPanel);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
	}

	/**
	 * set saved setting from setting.cfg file
	 */
	private void initSetting() {
		maxVertices.setText(Global.SETTING.get("MAX_VERTICES"));
		// seqFile.setText(Global.SETTING.get("FASTA_SOURCE"));
		csvSeparator.setText(Global.SETTING.get("CSV_SEPARATOR"));

		blastPath.setText(Global.SETTING.get("BLAST_PATH"));
		mclPath.setText(Global.SETTING.get("MCL_PATH"));

		String q = Global.SETTING.get("CSV_QUOTES");
		// System.out.println("read: " + q);
		int index = 0;
		for (int i = 0; i < quotes.length; i++) {
			if (q.equals(quotes[i])) {
				index = i;
				break;
			}
		}
		csvQuotes.setSelectedIndex(index);
		// System.out.println("read: " + index);
	}

	private void initGraphPanel() {
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTHWEST;
		JPanel graphPanel = new JPanel(new GridBagLayout());

		JLabel maxVerticesLabel = new JLabel("Max Vertices");
		maxVertices = new JTextField();
		maxVertices.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent arg0) {
				Global.SETTING
						.put("MAX_VERTICES", maxVertices.getText().trim());
			}
		});

		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.add(graphPanel, maxVerticesLabel, 0, 0, 1, 1);
		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(graphPanel, maxVertices, 1, 0, 1, 1);

		settingTabPane.add("Graph", graphPanel);
	}

	private void initProgramPanel() {
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTHWEST;
		JPanel programPanel = new JPanel(new GridBagLayout());

		JLabel blastLabel = new JLabel("BLAST");
		blastPath = new JTextField();
		blastPath.setEditable(false);
		blastButton = new JButton("Browse...");
		blastButton.addActionListener(this);

		JLabel mclLabel = new JLabel("MCL");
		mclPath = new JTextField();
		mclPath.setEditable(false);
		mclButton = new JButton("Browse...");
		mclButton.addActionListener(this);

		JLabel maxVerticesTip = new JLabel("Alter if greater when display");
		maxVerticesTip.setForeground(Color.gray);

		GridBagManager.add(programPanel, blastLabel, 0, 0, 1, 1);
		GridBagManager.add(programPanel, blastButton, 2, 0, 1, 1);

		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.add(programPanel, mclLabel, 0, 1, 1, 1);
		GridBagManager.add(programPanel, mclButton, 2, 1, 1, 1);

		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(programPanel, blastPath, 1, 0, 1, 1);

		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.add(programPanel, mclPath, 1, 1, 1, 1);

		settingTabPane.add("Program", programPanel);
	}

	// private void initSequencePanel() {
	// GridBagManager.GRID_BAG.weightx = 0;
	// GridBagManager.GRID_BAG.weighty = 1;
	// JPanel fastaFilePanel = new JPanel(new GridBagLayout());
	//
	// JLabel seqLabel = new JLabel("Fasta Source: ");
	// seqFile = new JTextField();
	// browseButton = new JButton("Browse...");
	// browseButton.addActionListener(this);
	//
	// GridBagManager.add(fastaFilePanel, seqLabel, 0, 0, 1, 1);
	// GridBagManager.add(fastaFilePanel, browseButton, 2, 0, 1, 1);
	// GridBagManager.GRID_BAG.weightx = 1;
	// GridBagManager.add(fastaFilePanel, seqFile, 1, 0, 1, 1);
	//
	// settingTabPane.add("Sequence", fastaFilePanel);
	// }

	private void initCSVPanel() {
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTHWEST;

		JPanel csvPanel = new JPanel(new GridBagLayout());

		JLabel csvSepLabel = new JLabel("Separator: ");
		csvSeparator = new JTextField();
		csvSeparator.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent arg0) {
				Global.SETTING.put("CSV_SEPARATOR", csvSeparator.getText()
						.trim());
			}
		});

		JLabel csvQuotesLabel = new JLabel("Quotes: ");
		csvQuotes = new JComboBox(quotes);
		csvQuotes.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int index = csvQuotes.getSelectedIndex();
				Global.SETTING.put("CSV_QUOTES", quotes[index]);
				// System.out.println("saved: " + quotes[index]);
			}
		});

		GridBagManager.add(csvPanel, csvSepLabel, 0, 0, 1, 1);

		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.add(csvPanel, csvQuotesLabel, 0, 1, 1, 1);

		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.GRID_BAG.weighty = 0;

		GridBagManager.add(csvPanel, csvSeparator, 1, 0, 1, 1);
		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.add(csvPanel, csvQuotes, 1, 1, 1, 1);

		settingTabPane.add("CSV", csvPanel);
	}

	private void initSettingTabPane() {
		initGraphPanel();
		// initSequencePanel();
		initCSVPanel();
		initProgramPanel();
		// init other tabs
	}

	private void initButtonPanel() {
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		okButton.setMnemonic('O');
		buttonPanel.add(okButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic('C');
		buttonPanel.add(cancelButton);
		// helpButton = new JButton("Help");
		// helpButton.setMnemonic('H');
		// buttonPanel.add(helpButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton sourceButton = (JButton) e.getSource();
		if (sourceButton.equals(blastButton)) {
			String path = choosePath();
			blastPath.setText(path);
			Global.SETTING.put("BLAST_PATH", path);
		} else if (sourceButton.equals(mclButton)) {
			String path = choosePath();
			mclPath.setText(path);
			Global.SETTING.put("MCL_PATH", path);
		} else if (sourceButton.equals(okButton)) {
			savePreferences();
		} else {
			setVisible(false);
			dispose();
		}
	}

	private String choosePath() {
		JFileChooser fileChooser = new JFileChooser();

		String lastPath = Global.PREFERENCES.get("LAST_PATH", "");

		if (!lastPath.equals("")) {
			fileChooser.setCurrentDirectory(new File(lastPath));
		}

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int c = fileChooser.showDialog(this, null);

		if (c != JFileChooser.APPROVE_OPTION) {
			return "";
		} else {
			return fileChooser.getSelectedFile().getPath();
		}
	}

	private void savePreferences() {
		String curPath = Global.getAppPath(this.getClass());
		// System.out.println(curPath);
		File settingFile = new File(curPath + File.separator + "setting.cfg");
		// System.out.println(settingFile.getPath());
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(
					settingFile));
			for (String key : Global.SETTING.keySet()) {
				fileWriter.write(key + Global.getSeparator(':')
						+ Global.SETTING.get(key) + "\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setVisible(false);
		dispose();
	}
}
