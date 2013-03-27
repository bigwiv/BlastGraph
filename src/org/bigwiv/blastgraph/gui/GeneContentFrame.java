package org.bigwiv.blastgraph.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.bigwiv.bio.phylogeny.BitArray;
import org.bigwiv.bio.phylogeny.DistanceMatrix;
import org.bigwiv.bio.phylogeny.DistanceType;
import org.bigwiv.bio.phylogeny.PhyloAlgorithms;
import org.bigwiv.bio.phylogeny.Tree;
import org.bigwiv.bio.phylogeny.TreePanel;
import org.bigwiv.bio.phylogeny.TreeTools;
import org.bigwiv.bio.phylogeny.TreeType;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.table.GeneContentModel;
import org.bigwiv.blastgraph.gui.table.GeneContentTable;


public class GeneContentFrame extends JFrame implements ActionListener {
	private JMenuBar menuBar;
	private JMenu fileMenu, phyloMenu;
	private JMenuItem saveItem;
	private JMenuItem geneContentClusterItem, geneContentTreeItem,
			mappingExistingTreeItem;
	private JTable mainTable;
	private GeneContentModel model;

	public GeneContentFrame(GeneContentModel model) {
		super();
		this.setTitle("Gene Content");
		this.model = model;
		initComponents();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void initComponents() {
		saveItem = new JMenuItem("Save");
		saveItem.addActionListener(this);

		fileMenu = new JMenu("File");
		fileMenu.add(saveItem);

		geneContentClusterItem = new JMenuItem("Gene Content Clustering");
		geneContentClusterItem.addActionListener(this);

		geneContentTreeItem = new JMenuItem("Gene Content Tree");
		geneContentTreeItem.addActionListener(this);

		//mappingExistingTreeItem = new JMenuItem("Mapping to Existing Tree");
		//mappingExistingTreeItem.addActionListener(this);

		phyloMenu = new JMenu("Phylogeny");
		phyloMenu.add(geneContentClusterItem);
		phyloMenu.add(geneContentTreeItem);
		//phyloMenu.add(mappingExistingTreeItem);

		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(phyloMenu);

		this.setJMenuBar(menuBar);

		mainTable = new JTable(model);

		JScrollPane scrollPane = new JScrollPane(mainTable);
		JTable rowTable = new GeneContentTable(mainTable);
		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());

		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane);
		mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// mainTable.getTableHeader().setPreferredSize(new Dimension(20, 20));
		// mainTable.getTableHeader().setDefaultRenderer(
		// new RotatedTableCellRenderer(-90));

		TableColumnModel columnModel = mainTable.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setWidth(20);
			columnModel.getColumn(i).setPreferredWidth(20);
			// columnModel.getColumn(i).setMaxWidth(4);
		}

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((screen.width * 2) / 3, (screen.height * 9) / 10);
		setLocation(screen.width / 6, screen.height / 16);
	}

	public void setModel(AbstractTableModel model) {
		mainTable.setModel(model);
		TableColumnModel columnModel = mainTable.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setWidth(20);
			columnModel.getColumn(i).setPreferredWidth(20);
			// columnModel.getColumn(i).setMaxWidth(4);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem sourceItem = (JMenuItem) e.getSource();
		if (sourceItem.equals(saveItem)) {
			ArrayList<FileFilter> filterList = new ArrayList<FileFilter>();
			filterList.add(FileChooseTools.CSV_FILTER);
			SelectedFile selectedFile = FileChooseTools.saveFile(this,
					"Save geneContent table", filterList);

			if (selectedFile == null)
				return;

			File file = selectedFile.getFile();

			try {
				model.writeTo(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (sourceItem.equals(geneContentClusterItem)
				|| sourceItem.equals(mappingExistingTreeItem)) {
			Vector<String> rowHeader = model.getRowHeader();

			Map<String, BitArray> geneContent = new HashMap<String, BitArray>();

			for (int i = 0; i < rowHeader.size(); i++) {
				BitArray genes = new BitArray(model.getColumnCount());
				for (int j = 0; j < model.getColumnCount(); j++) {
					if ((Integer) (model.getValueAt(i, j)) > 0) {
						genes.set(j);
					}
				}
				geneContent.put(rowHeader.get(i), genes);
			}

			Tree tree = null;

			if (sourceItem.equals(geneContentClusterItem)) {
				tree = PhyloAlgorithms.getGeneContentCluster(geneContent);
			} else if (sourceItem.equals(mappingExistingTreeItem)) {
				ArrayList<FileFilter> filterList = new ArrayList<FileFilter>();
				filterList.clear();
				filterList.add(FileChooseTools.NWK_FILTER);

				SelectedFile selectedFile = FileChooseTools.openFile(
						Global.APP_FRAME_PROXY.getFrame(), "Choose Tree",
						filterList);

				if (selectedFile == null)
					return;
				String treeFile = selectedFile.getFile().getPath();
				try {
					tree = TreeTools.readNewick(treeFile).get(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			TreeFrame frame = new TreeFrame(tree);
			frame.getTreePanel().setGeneContent(geneContent);
			frame.setEditable(false);
			frame.setSize(500, 400);
			frame.setVisible(true);
		} else if (sourceItem.equals(geneContentTreeItem)) {
			GeneContentTreeDialog gcTreeDialog = new GeneContentTreeDialog(
					this, model);
			gcTreeDialog.pack();
			gcTreeDialog.setVisible(true);
		}
	}

}
