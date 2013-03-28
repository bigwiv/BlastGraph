package org.bigwiv.blastgraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.command.AppendCommand;
import org.bigwiv.blastgraph.command.BatchSaveCommand;
import org.bigwiv.blastgraph.command.CloseCommand;
import org.bigwiv.blastgraph.command.Command;
import org.bigwiv.blastgraph.command.CommandAdapter;
import org.bigwiv.blastgraph.command.CommandListener;
import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.command.ExportCommand;
import org.bigwiv.blastgraph.command.ExportVertexAttrCommand;
import org.bigwiv.blastgraph.command.FiltCommand;
import org.bigwiv.blastgraph.command.GenomeNumFiltCommand;
import org.bigwiv.blastgraph.command.ImportVertexAttrCommand;
import org.bigwiv.blastgraph.command.MclCommand;
import org.bigwiv.blastgraph.command.MinimumSpanningTreeCommand;
import org.bigwiv.blastgraph.command.NewCommand;
import org.bigwiv.blastgraph.command.OpenCommand;
import org.bigwiv.blastgraph.command.RemoveSingleCommand;
import org.bigwiv.blastgraph.command.RemoveSingleLinkageCommand;
import org.bigwiv.blastgraph.command.SaveAsCommand;
import org.bigwiv.blastgraph.command.SaveCommand;
import org.bigwiv.blastgraph.command.SaveImageCommand;
import org.bigwiv.blastgraph.command.SettingCommand;
import org.bigwiv.blastgraph.command.ViewNeighborCommand;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.graphvisualization.AttrVertexPainter;
import org.bigwiv.blastgraph.gui.graphvisualization.CollectionChangeListener;
import org.bigwiv.blastgraph.gui.graphvisualization.EdgeToolTip;
import org.bigwiv.blastgraph.gui.graphvisualization.MyAnnotatingModalGraphMouse;
import org.bigwiv.blastgraph.gui.graphvisualization.MyShapePickSupport;
import org.bigwiv.blastgraph.gui.graphvisualization.VertexPainter;
import org.bigwiv.blastgraph.gui.graphvisualization.VertexShape;
import org.bigwiv.blastgraph.gui.graphvisualization.VertexToolTip;
import org.bigwiv.blastgraph.gui.table.EdgesTable;
import org.bigwiv.blastgraph.gui.table.GeneContentModel;
import org.bigwiv.blastgraph.gui.table.SearchResultModel;
import org.bigwiv.blastgraph.gui.table.VerticesTable;
import org.bigwiv.blastgraph.io.PrintUtilities;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.annotations.AnnotationControls;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class BlastGraphFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2065966556434164545L;
	private JMenu fileMenu, saveAsMenu, saveCurGraphMenu, editMenu, toolsMenu,
			graphMenu, modeMenu, layoutMenu, helpMenu;
	private JMenuBar menuBar;
	private JMenuItem newItem, openItem, appendGraphItem, importVertexAttrItem,
			saveItem, saveAsGraphItem, exportGraphItem, saveCurGraphItem,
			saveCurImgItem, printItem, saveAllVertexAttrItem,
			saveCurVertexAttrItem, batchSaveItem, closeItem, exitItem,
			helpContentItem, aboutItem;
	private JMenuItem undoItem, redoItem, removeSingleItem, filterItem,
			markovClusterItem, mstItem, genomeNumFiltItem,
			removeSingleLinkageItem, settingItem;
	private JMenuItem previousItem, nextItem, resortItem, viewNeighborItem;
	private JMenuItem geneContentItem;
	// private JMenuItem tempWorkItem;

	private JToolBar fileToolBar, graphToolBar, viewToolBar, annotationToolBar;
	private JPanel toolBarPanel;
	private JButton newButton, openButton, saveButton, saveCurImgButton,
			printButton, previousButton, nextButton, searchButton,
			filterButton, resortButton;
	private JTextField indexField, searchField;

	// vertex color control
	JComboBox colorComboBox;
	ItemListener colorItemListener;

	private JSplitPane hsplitPane, vsplitPane;
	private JPanel mainPanel, infoPanel;
	// private ProgressPanel progressPanel;
	// private final ProgressDialog progressDialog;
	private final ProgressPanel progressPanel;
	private final GraphStatisticsPanel graphStatisticsPanel;
	private JTabbedPane tabbedPane;

	private JPanel verticesPanel, edgesPanel;
	private VerticesTable verticesTable;
	private EdgesTable edgesTable;

	// private GeneContentFrame geneContentFrame;
	private GeneContentModel geneContentModel;

	// graph visualization component
	private GraphZoomScrollPane vvPanel;
	private MyAnnotatingModalGraphMouse<HitVertex, ValueEdge> graphMouse;
	private AnnotatingGraphMousePlugin<HitVertex, ValueEdge> annotatingPlugin;
	// private MyPickingGraphMousePlugin<HitVertex, ValueEdge> pickingPlugin;
	private VisualizationViewer<HitVertex, ValueEdge> vv;
	private RenderContext<HitVertex, ValueEdge> rc;
	private Layout layout;
	private LayoutType layoutType;

	// graph data to manage
	private BlastGraph<HitVertex, ValueEdge> emptyGraph;
	private ArrayList<Set<HitVertex>> subSetList;
	private BlastGraph<HitVertex, ValueEdge> curGraph;
	private int curIndex;
	private int graphCount;

	// control component
	private JLabel vertexCountLabel, edgeCountLabel, subGraphCountLabel;

	private JLabel currentVertexCountLabel, currentEdgeCountLabel,
			currentDegreePercentageLabel;

	private JLabel selectedVertexCountLabel, selectedEdgeCountLabel;
	private JComboBox modeBox;

	private GeneContentFrame geneContentFrame;

	enum LayoutType {
		FRLayout, EWLayout
	};

	protected JComboBox layoutBox;

	public BlastGraphFrame(String title) {
		super(title);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		readSetting();
		this.emptyGraph = new BlastGraph<HitVertex, ValueEdge>();
		// this.progressDialog = new ProgressDialog(this,
		// Global.COMMAND_MANAGER);
		this.progressPanel = new ProgressPanel(Global.COMMAND_MANAGER);
		this.graphStatisticsPanel = new GraphStatisticsPanel(curGraph);
		this.layoutType = LayoutType.FRLayout;
		vvInit();
		initComponents();
		subGraphInit();
		Global.WORK_STATUS.setStatusChangeListener(progressPanel);
		// Global.WORK_STATUS.setStatusChangeListener(progressDialog);
		Global.APP_FRAME_PROXY.setFrame(this);
		setVisible(true);
	}

	/**
	 * read saved setting from setting.cfg file
	 */
	private void readSetting() {
		String curPath = Global.getAppPath(this.getClass());

		// System.out.println(curPath);
		File settingFile = new File(curPath + File.separator + "setting.cfg");
		if (settingFile.exists()) {
			try {
				BufferedReader fileReader = new BufferedReader(new FileReader(
						settingFile));
				String line = fileReader.readLine();
				while (line != null && !line.equals("")) {
					String pair[] = line.trim().split(Global.getSeparator(':'));
					if (pair.length != 2) {
						Global.WORK_STATUS.setError("Bad setting file");
						return;
					}
					// System.out.println(pair[0].trim() + " " +
					// pair[1].trim());
					Global.SETTING.put(pair[0].trim(), pair[1].trim());
					line = fileReader.readLine();
				}
				fileReader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// put default value
		if (!Global.SETTING.containsKey("MAX_VERTICES"))
			Global.SETTING.put("MAX_VERTICES", "1000");
		if (!Global.SETTING.containsKey("CSV_SEPARATOR"))
			Global.SETTING.put("CSV_SEPARATOR", ",");
		if (!Global.SETTING.containsKey("CSV_QUOTES"))
			Global.SETTING.put("CSV_QUOTES", "\"");
	}

	private void subGraphInit() {
		Global.graph.generateSubSet();

		subSetList = Global.graph.getSubSet();

		if (subSetList.size() > 0) {
			curGraph = FilterUtils.createInducedSubgraph(subSetList.get(0),
					Global.graph);
			navToIndex(1);
		} else {
			curIndex = 0;
			curGraph = emptyGraph;
			refreshSubGraphView();
		}
		graphCount = subSetList.size();

		// subInfo
		subGraphCountLabel.setText("" + graphCount);
		// spinnerNumberModel.setMaximum(graphCount);
	}

	private void vvInit() {

		// vv init
		try {
			layout = getLayoutFor(layoutType, emptyGraph);
		} catch (Exception e) {
			e.printStackTrace();
		}
		vv = new VisualizationViewer<HitVertex, ValueEdge>(layout);
		vv.setBackground(Color.white);

		rc = vv.getRenderContext();

		annotatingPlugin = new AnnotatingGraphMousePlugin<HitVertex, ValueEdge>(
				rc);

		graphMouse = new MyAnnotatingModalGraphMouse<HitVertex, ValueEdge>(rc,
				annotatingPlugin);

		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());

		rc.setPickSupport(new MyShapePickSupport<HitVertex, ValueEdge>(vv));
		rc.setVertexShapeTransformer(new VertexShape(Global.graph));
		rc.setVertexFillPaintTransformer(new VertexPainter());

		// rc.setEdgeShapeTransformer(new EdgeShape.Line<HitVertex,
		// ValueEdge>());
		// rc.setEdgeStrokeTransformer(new ConstantTransformer(new BasicStroke(
		// 2.5f)));

		rc.setVertexDrawPaintTransformer(new Transformer<HitVertex, Paint>() {
			@Override
			public Paint transform(HitVertex hv) {
				if (vv.getPickedVertexState().isPicked(hv)) {
					return Color.cyan;
				} else {
					return Color.BLACK;
				}
			}
		});

		vv.setVertexToolTipTransformer(new VertexToolTip<HitVertex>());
		vv.setEdgeToolTipTransformer(new EdgeToolTip<ValueEdge>());
	}

	private void initComponents() {

		URL icon;
		icon = getClass().getResource("/org/bigwiv/blastgraph/icons/icon.png");
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(icon));
		JComponent pane;
		pane = (JComponent) getContentPane();

		// ====================Menu Setting=======================
		newItem = new JMenuItem("New From Blast");
		newItem.setMnemonic('n');
		newItem.addActionListener(commandActionListener);

		openItem = new JMenuItem("Open");
		openItem.setMnemonic('o');
		openItem.addActionListener(commandActionListener);

		appendGraphItem = new JMenuItem("Append Graph");
		appendGraphItem.setMnemonic('a');
		appendGraphItem.addActionListener(commandActionListener);

		saveItem = new JMenuItem("Save");
		saveItem.setMnemonic('s');
		saveItem.addActionListener(commandActionListener);

		saveAsGraphItem = new JMenuItem("Save As");
		saveAsGraphItem.addActionListener(commandActionListener);

		exportGraphItem = new JMenuItem("Export");
		exportGraphItem.addActionListener(commandActionListener);

		saveCurGraphItem = new JMenuItem("Save Current Graph");
		saveCurGraphItem.addActionListener(commandActionListener);

		saveCurImgItem = new JMenuItem("Save Image");
		saveCurImgItem.addActionListener(commandActionListener);

		saveAllVertexAttrItem = new JMenuItem("Save Vertex Attribute");
		saveAllVertexAttrItem.addActionListener(commandActionListener);

		saveCurVertexAttrItem = new JMenuItem("Save Vertex Attribute");
		saveCurVertexAttrItem.addActionListener(commandActionListener);

		saveAsMenu = new JMenu("Save As");
		saveAsMenu.add(saveAsGraphItem);
		saveAsMenu.add(saveAllVertexAttrItem);

		saveCurGraphMenu = new JMenu("Save Current Graph");
		saveCurGraphMenu.add(saveCurGraphItem);
		saveCurGraphMenu.add(saveCurImgItem);
		saveCurGraphMenu.add(saveCurVertexAttrItem);

		printItem = new JMenuItem("Print...");
		printItem.setMnemonic('p');
		printItem.addActionListener(commandActionListener);

		importVertexAttrItem = new JMenuItem("Import Vertex Attribute");
		importVertexAttrItem.addActionListener(commandActionListener);

		closeItem = new JMenuItem("Close");
		closeItem.setMnemonic('c');
		closeItem.addActionListener(commandActionListener);

		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		exitItem.addActionListener(commandActionListener);

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(appendGraphItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(closeItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(saveItem);
		fileMenu.add(saveAsMenu);
		fileMenu.add(saveCurGraphMenu);
		fileMenu.add(new JSeparator());
		fileMenu.add(printItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(importVertexAttrItem);
		fileMenu.add(exportGraphItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitItem);

		// edit menu
		undoItem = new JMenuItem("Undo");
		redoItem = new JMenuItem("Redo");
		Global.COMMAND_MANAGER.registerUndoRedoItem(undoItem, redoItem);
		removeSingleItem = new JMenuItem("Remove Single Vertices");
		removeSingleItem.addActionListener(commandActionListener);
		filterItem = new JMenuItem("Filt Graph");
		filterItem.addActionListener(commandActionListener);
		settingItem = new JMenuItem("Setting");
		settingItem.addActionListener(commandActionListener);

		markovClusterItem = new JMenuItem("Markov Cluster");
		markovClusterItem.addActionListener(commandActionListener);

		// genomeNumFiltItem = new JMenuItem("GenomeNum Filt");
		// genomeNumFiltItem.addActionListener(commandActionListener);

		// removeSingleLinkageItem = new JMenuItem("Remove Single Linkage");
		// removeSingleLinkageItem.addActionListener(commandActionListener);

		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('e');
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		editMenu.add(filterItem);
		editMenu.add(markovClusterItem);
		editMenu.add(removeSingleItem);
		// editMenu.add(genomeNumFiltItem);
		// editMenu.add(removeSingleLinkageItem);
		editMenu.add(settingItem);

		// Tools Item
		geneContentItem = new JMenuItem("Gene Content Table");
		geneContentItem.addActionListener(commandActionListener);

		batchSaveItem = new JMenuItem("Batch Save");
		batchSaveItem.addActionListener(commandActionListener);

		mstItem = new JMenuItem("Minimum Spanning Tree");
		mstItem.addActionListener(commandActionListener);

		viewNeighborItem = new JMenuItem("View Neighbor of...");
		viewNeighborItem.addActionListener(commandActionListener);

		//
		// tempWorkItem = new JMenuItem("Temp Work");
		// tempWorkItem.addActionListener(commandActionListener);

		toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');
		toolsMenu.add(geneContentItem);
		toolsMenu.add(viewNeighborItem);
		toolsMenu.add(mstItem);
		toolsMenu.add(batchSaveItem);
		// toolsMenu.add(tempWorkItem);

		// graph Menu
		graphMenu = new JMenu("Graph");
		previousItem = new JMenuItem("Previous");
		previousItem.addActionListener(commandActionListener);
		nextItem = new JMenuItem("Next");
		nextItem.addActionListener(commandActionListener);
		resortItem = new JMenuItem("Resort graphs");
		resortItem.addActionListener(commandActionListener);

		graphMenu.add(nextItem);
		graphMenu.add(previousItem);
		graphMenu.add(resortItem);

		// help Menu
		helpContentItem = new JMenuItem("Online Manual");
		helpContentItem.addActionListener(commandActionListener);
		aboutItem = new JMenuItem("About BlastGraph");
		aboutItem.addActionListener(commandActionListener);

		helpMenu = new JMenu("Help");
		helpMenu.add(helpContentItem);
		helpMenu.add(aboutItem);

		// menu bar
		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(graphMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		// ===================mainPanel Setting===================
		GridBagManager.reset();
		GridBagManager.GRID_BAG.fill = GridBagConstraints.BOTH;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.FIRST_LINE_START;
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.insets = new Insets(2, 2, 1, 1);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		pane.add(mainPanel);

		hsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hsplitPane.setContinuousLayout(true);
		hsplitPane.setOneTouchExpandable(true);
		hsplitPane.setResizeWeight(0.9);

		// ===================toolBarPanel Setting===================

		// graph file toolbar
		fileToolBar = new JToolBar();
		fileToolBar.setRollover(true);
		newButton = new JButton();
		newButton.setBorderPainted(false);
		newButton.setMnemonic('n');
		newButton.setToolTipText("New From Blast");
		newButton.addActionListener(commandActionListener);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/filenew.png");
		// System.out.println(icon);
		newButton.setIcon(new ImageIcon(icon));

		openButton = new JButton();
		openButton.setBorderPainted(false);
		openButton.setMnemonic('o');
		openButton.setToolTipText("Open");
		openButton.addActionListener(commandActionListener);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/fileopen.png");
		openButton.setIcon(new ImageIcon(icon));

		saveButton = new JButton();
		saveButton.setBorderPainted(false);
		saveButton.setMnemonic('s');
		saveButton.setToolTipText("Save");
		saveButton.addActionListener(commandActionListener);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/filesave.png");
		saveButton.setIcon(new ImageIcon(icon));

		saveCurImgButton = new JButton();
		saveCurImgButton.setBorderPainted(false);
		saveCurImgButton.setToolTipText("Save current image");
		saveCurImgButton.addActionListener(commandActionListener);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/saveimage.png");
		saveCurImgButton.setIcon(new ImageIcon(icon));

		printButton = new JButton();
		printButton.setBorderPainted(false);
		printButton.setToolTipText("Print...");
		printButton.addActionListener(commandActionListener);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/fileprint.png");
		printButton.setIcon(new ImageIcon(icon));

		fileToolBar.add(newButton);
		fileToolBar.add(openButton);
		fileToolBar.add(saveButton);
		fileToolBar.add(saveCurImgButton);
		fileToolBar.add(printButton);

		// graph control toolbar
		graphToolBar = new JToolBar();
		graphToolBar.setRollover(true);

		previousButton = new JButton();
		previousButton.setBorderPainted(false);
		previousButton.setToolTipText("Previous graph");
		previousButton.addActionListener(commandActionListener);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/previous.png");
		previousButton.setIcon(new ImageIcon(icon));

		indexField = new JTextField();
		indexField.setSize(new Dimension(25, 16));
		indexField.setMinimumSize(new Dimension(25, 16));
		indexField.setPreferredSize(new Dimension(25, 16));
		indexField.addKeyListener(keyListener);

		nextButton = new JButton();
		nextButton.setBorderPainted(false);
		nextButton.setToolTipText("Next graph");
		nextButton.addActionListener(commandActionListener);
		icon = getClass().getResource("/org/bigwiv/blastgraph/icons/next.png");
		nextButton.setIcon(new ImageIcon(icon));

		filterButton = new JButton();
		filterButton.setBorderPainted(false);
		filterButton.setToolTipText("Filt graph");
		filterButton.addActionListener(commandActionListener);
		icon = getClass()
				.getResource("/org/bigwiv/blastgraph/icons/filter.png");
		filterButton.setIcon(new ImageIcon(icon));

		resortButton = new JButton();
		resortButton.setBorderPainted(false);
		resortButton.setToolTipText("Resort graph");
		resortButton.addActionListener(commandActionListener);
		icon = getClass()
				.getResource("/org/bigwiv/blastgraph/icons/resort.png");
		resortButton.setIcon(new ImageIcon(icon));

		searchField = new JTextField();
		searchField.setSize(new Dimension(60, 16));
		searchField.setMinimumSize(new Dimension(60, 16));
		searchField.setPreferredSize(new Dimension(60, 16));
		searchField.addKeyListener(keyListener);

		searchButton = new JButton();
		searchButton.setBorderPainted(false);
		searchButton.setToolTipText("Search");
		searchButton.addActionListener(commandActionListener);
		icon = getClass()
				.getResource("/org/bigwiv/blastgraph/icons/search.png");
		searchButton.setIcon(new ImageIcon(icon));

		graphToolBar.add(filterButton);
		graphToolBar.add(resortButton);
		// graphToolBar.add(new JToolBar.Separator());
		graphToolBar.add(previousButton);
		graphToolBar.add(indexField);
		graphToolBar.add(nextButton);
		graphToolBar.add(searchField);
		graphToolBar.add(searchButton);

		// view toolbar (modebox & layoutbox)
		viewToolBar = new JToolBar();
		viewToolBar.setRollover(true);
		modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				int index = modeBox.getSelectedIndex();
				if (index == 2) {
					int annotCount = annotationToolBar.getComponentCount();
					for (int i = 0; i < annotCount; i++) {
						annotationToolBar.getComponent(i).setEnabled(true);
					}
				} else {
					int annotCount = annotationToolBar.getComponentCount();
					for (int i = 0; i < annotCount; i++) {
						annotationToolBar.getComponent(i).setEnabled(false);
					}
				}

			}
		});
		layoutBox = this.getLayoutComboBox();
		viewToolBar.add(modeBox);
		viewToolBar.add(layoutBox);

		colorComboBox = new JComboBox(new String[] { "vertex color", "index",
				"strand", "genomeAcc", "organism" });
		viewToolBar.add(colorComboBox);

		// add additional menu to graphMenu
		modeMenu = graphMouse.getModeMenu();
		modeMenu.setText("Mode");
		layoutMenu = getLayoutMenu();
		layoutMenu.setText("Layout");
		graphMenu.add(modeMenu);
		graphMenu.add(layoutMenu);

		// annotation toolbar
		AnnotationControls<HitVertex, ValueEdge> annotationControls = new AnnotationControls<HitVertex, ValueEdge>(
				annotatingPlugin);
		annotationToolBar = annotationControls.getAnnotationsToolBar();
		((JButton) annotationToolBar.getComponent(1)).setBorderPainted(false);
		((JToggleButton) annotationToolBar.getComponent(2))
				.setBorderPainted(false);

		// add toolbars to toolBarPanel
		toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new ModifiedFlowLayout(ModifiedFlowLayout.LEFT,
				0, 0));
		toolBarPanel.setBorder(new EtchedBorder());
		toolBarPanel.add(fileToolBar);
		toolBarPanel.add(graphToolBar);
		toolBarPanel.add(viewToolBar);
		toolBarPanel.add(annotationToolBar);

		mainPanel.add(toolBarPanel, BorderLayout.NORTH);
		mainPanel.add(hsplitPane, BorderLayout.CENTER);

		// GridBagManager.add(mainPanel, toolBarPanel, 0, 0, 1, 1);
		// GridBagManager.add(mainPanel, hsplitPane, 0, 1, 1, 1);

		// mainPanel.add(toolBarPanel, BorderLayout.NORTH);

		// ===================vsplitPane Setting=================
		vsplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vsplitPane.setContinuousLayout(true);
		vsplitPane.setOneTouchExpandable(true);
		vsplitPane.setResizeWeight(0.9);

		hsplitPane.setLeftComponent(vsplitPane);
		// GridBagManager.GRID_BAG.weightx = 0.9;
		// GridBagManager.GRID_BAG.weighty = 1;
		// GridBagManager.add(mainPanel, vsplitPane, 0, 1, 1, 2);
		// mainPanel.add(vsplitPane, BorderLayout.CENTER);

		// ===================vvPanel Setting===================

		vvPanel = new GraphZoomScrollPane(vv);
		vsplitPane.setTopComponent(vvPanel);

		// ===================Tab Panel Setting=======================
		// progressPanel = new ProgressPanel();

		verticesTable = new VerticesTable();
		verticesTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int row = verticesTable.rowAtPoint(e.getPoint());
				int col = verticesTable.columnAtPoint(e.getPoint());
				// System.out.println(row + " " + col);
				String value = verticesTable.getValueAt(row, col).toString();
				// System.out.println(value);
				if (Global.graph.containsVertex(new HitVertex(value))) {
					for (int i = 0; i < subSetList.size(); i++) {
						if (subSetList.get(i).contains(new HitVertex(value))) {
							curGraph = FilterUtils.createInducedSubgraph(
									subSetList.get(i), Global.graph);
							PickedState<HitVertex> pickedVertexState = vv
									.getPickedVertexState();

							// picked is a reference to picked vertices
							// use a temp set to avoid concurrent modification
							Set<HitVertex> picked = pickedVertexState
									.getPicked();
							Set<HitVertex> temp = new HashSet<HitVertex>();
							for (HitVertex hitVertex : picked) {
								temp.add(hitVertex);
							}
							for (HitVertex hv : temp) {
								pickedVertexState.pick(hv, false);
							}

							pickedVertexState.pick(new HitVertex(value), true);
							// refreshSubGraphView();
							return;
						}
					}
				}
			}
		});

		verticesTable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int row = verticesTable.rowAtPoint(e.getPoint());
				int col = verticesTable.columnAtPoint(e.getPoint());
				String value = verticesTable.getValueAt(row, col).toString();
				// System.out.println(value);
				if (Global.graph.containsVertex(value)) {
					Cursor normalCursor = new Cursor(Cursor.HAND_CURSOR);
					setCursor(normalCursor);
				} else {
					Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(normalCursor);
				}
			}
		});
		verticesPanel = new JPanel();
		verticesPanel.setLayout(new GridLayout());
		verticesPanel.add(new JScrollPane(verticesTable));

		edgesTable = new EdgesTable();
		edgesPanel = new JPanel();
		edgesPanel.setLayout(new GridLayout());
		edgesPanel.add(new JScrollPane(edgesTable));

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Vertices", verticesPanel);
		tabbedPane.addTab("Edges", edgesPanel);
		vsplitPane.setBottomComponent(tabbedPane);

		graphMouse.addPichedChangeListener(verticesTable, edgesTable);

		// ===================Info Panel Setting===================
		infoPanel = new JPanel(new GridBagLayout());
		GridBagManager.GRID_BAG.weightx = 0.1;
		GridBagManager.GRID_BAG.weighty = 1;
		hsplitPane.setRightComponent(infoPanel);

		JPanel mainInfoPanel = new JPanel(new GridBagLayout());
		mainInfoPanel.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED), "Main Info"));

		JPanel currentInfoPanel = new JPanel(new GridBagLayout());
		currentInfoPanel.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED), "Current Graph"));
		JPanel selectedInfoPanel = new JPanel(new GridBagLayout());
		selectedInfoPanel.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED), "Selected"));

		JPanel statisticsInfoPanel = new JPanel(new GridBagLayout());
		statisticsInfoPanel.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED), "Statistics"));

		JPanel progressInfoPanel = new JPanel(new GridBagLayout());
		progressInfoPanel.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED), "Progress"));

		GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTH;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.HORIZONTAL;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.add(infoPanel, mainInfoPanel, 0, 1, 1, 1);
		GridBagManager.add(infoPanel, currentInfoPanel, 0, 2, 1, 1);
		GridBagManager.add(infoPanel, selectedInfoPanel, 0, 3, 1, 1);
		GridBagManager.GRID_BAG.weighty = 0.5;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.BOTH;
		GridBagManager.add(infoPanel, statisticsInfoPanel, 0, 4, 1, 1);
		GridBagManager.add(infoPanel, progressInfoPanel, 0, 5, 1, 1);

		// mainInfoPanel
		{
			JLabel vertices = new JLabel("Vertices:");
			vertexCountLabel = new JLabel("0");
			JLabel edges = new JLabel("Edges:");
			edgeCountLabel = new JLabel("0");
			JLabel subGraphs = new JLabel("SubGraphs:");
			subGraphCountLabel = new JLabel("0");
			GridBagManager.GRID_BAG.anchor = GridBagConstraints.WEST;
			GridBagManager.GRID_BAG.weightx = 0.5;
			GridBagManager.add(mainInfoPanel, vertices, 0, 0, 1, 1);
			GridBagManager.add(mainInfoPanel, edges, 0, 1, 1, 1);
			GridBagManager.add(mainInfoPanel, subGraphs, 0, 2, 1, 1);
			GridBagManager.GRID_BAG.anchor = GridBagConstraints.EAST;
			GridBagManager.GRID_BAG.weightx = 0.5;
			GridBagManager.add(mainInfoPanel, vertexCountLabel, 1, 0, 1, 1);
			GridBagManager.add(mainInfoPanel, edgeCountLabel, 1, 1, 1, 1);
			GridBagManager.add(mainInfoPanel, subGraphCountLabel, 1, 2, 1, 1);

		}// end of mainInfoPanel

		// currentInfoPanel
		{
			JLabel vertices = new JLabel("Vertices:");
			currentVertexCountLabel = new JLabel("0");

			JLabel edges = new JLabel("Edges:");
			currentEdgeCountLabel = new JLabel("0");

			JLabel degreePercentage = new JLabel("Degree Percentage:");
			currentDegreePercentageLabel = new JLabel("0");

			GridBagManager.GRID_BAG.anchor = GridBagConstraints.WEST;
			GridBagManager.GRID_BAG.weightx = 0.5;
			GridBagManager.add(currentInfoPanel, vertices, 0, 0, 1, 1);
			GridBagManager.add(currentInfoPanel, edges, 0, 1, 1, 1);
			GridBagManager.add(currentInfoPanel, degreePercentage, 0, 2, 1, 1);
			GridBagManager.GRID_BAG.anchor = GridBagConstraints.EAST;
			GridBagManager.GRID_BAG.weightx = 0.5;
			GridBagManager.add(currentInfoPanel, currentVertexCountLabel, 1, 0,
					1, 1);
			GridBagManager.add(currentInfoPanel, currentEdgeCountLabel, 1, 1,
					1, 1);
			GridBagManager.add(currentInfoPanel, currentDegreePercentageLabel,
					1, 2, 1, 1);

		}// end of currentInfoPanel

		// selectedInfoPanel
		{
			JLabel vertices = new JLabel("Vertices:");
			selectedVertexCountLabel = new JLabel("0");
			JLabel edges = new JLabel("Edges:");
			selectedEdgeCountLabel = new JLabel("0");
			GridBagManager.GRID_BAG.anchor = GridBagConstraints.WEST;
			GridBagManager.GRID_BAG.weightx = 0.5;
			GridBagManager.add(selectedInfoPanel, vertices, 0, 0, 1, 1);
			GridBagManager.add(selectedInfoPanel, edges, 0, 1, 1, 1);
			GridBagManager.GRID_BAG.anchor = GridBagConstraints.EAST;
			GridBagManager.GRID_BAG.weightx = 0.5;
			GridBagManager.add(selectedInfoPanel, selectedVertexCountLabel, 1,
					0, 1, 1);
			GridBagManager.add(selectedInfoPanel, selectedEdgeCountLabel, 1, 1,
					1, 1);
			CollectionChangeListener<HitVertex> svListener = new CollectionChangeListener<HitVertex>() {

				@Override
				public void onCollectionChange(Set<HitVertex> set) {
					selectedVertexCountLabel.setText("" + set.size());
				}
			};

			CollectionChangeListener<ValueEdge> veListener = new CollectionChangeListener<ValueEdge>() {

				@Override
				public void onCollectionChange(Set<ValueEdge> set) {
					selectedEdgeCountLabel.setText("" + set.size());
				}
			};

			graphMouse.addPichedChangeListener(svListener, veListener);

		}// end of selectedInfoPanel

		{// statistics Panel
			GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTH;
			GridBagManager.GRID_BAG.fill = GridBagConstraints.BOTH;
			GridBagManager.add(statisticsInfoPanel, graphStatisticsPanel, 0, 0,
					1, 1);
		}// statistics Panel

		{// progressInfoPanel
			GridBagManager.GRID_BAG.anchor = GridBagConstraints.NORTH;
			GridBagManager.GRID_BAG.fill = GridBagConstraints.BOTH;
			GridBagManager.add(progressInfoPanel, progressPanel, 0, 0, 1, 1);
		}// progressInfoPanel
			// set frame size
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((screen.width * 3) / 4, (screen.height * 9) / 10);
		setLocation(screen.width / 6, screen.height / 16);

		refreshUI(false);
	}

	/**
	 * set Layout
	 */
	private void setLayout(LayoutType layoutType) {
		if (!this.layoutType.equals(layoutType)) {
			this.layoutType = layoutType;
			try {
				this.layout = getLayoutFor(this.layoutType, curGraph);
			} catch (Exception e) {
				e.printStackTrace();
			}
			vv.setGraphLayout(layout);
			vv.repaint();
			// System.out.println("called");
		}
	}

	/**
	 * initialize LayoutCombox
	 * 
	 * @return
	 */
	private JComboBox getLayoutComboBox() {
		if (layoutBox == null) {
			layoutBox = new JComboBox(new LayoutType[] { LayoutType.FRLayout,
					LayoutType.EWLayout });
			layoutBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						LayoutType layoutType = (LayoutType) e.getItem();
						setLayout(layoutType);
					}
				}
			});
		}
		layoutBox.setSelectedItem(LayoutType.FRLayout);
		return layoutBox;
	}

	/**
	 * initialize LayoutMenu
	 * 
	 * @return
	 */
	private JMenu getLayoutMenu() {
		if (layoutMenu == null) {

			layoutMenu = new JMenu();

			final JRadioButtonMenuItem frButton = new JRadioButtonMenuItem(
					LayoutType.FRLayout.toString());
			final JRadioButtonMenuItem ewButton = new JRadioButtonMenuItem(
					LayoutType.EWLayout.toString());

			ItemListener layoutMenuItemListener = new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (e.getItem().equals(frButton)) {
							setLayout(LayoutType.FRLayout);
							layoutBox.setSelectedItem(LayoutType.FRLayout);
						} else if (e.getItem().equals(ewButton)) {
							setLayout(LayoutType.EWLayout);
							layoutBox.setSelectedItem(LayoutType.EWLayout);
						}
					}
				}
			};

			frButton.addItemListener(layoutMenuItemListener);
			ewButton.addItemListener(layoutMenuItemListener);

			ButtonGroup radio = new ButtonGroup();
			radio.add(frButton);
			radio.add(ewButton);
			frButton.setSelected(true);
			layoutMenu.add(frButton);
			layoutMenu.add(ewButton);
			layoutMenu.setToolTipText("Menu for setting graph layout");

			// layoutMenu.addItemListener(layoutMenuItemListener);

			layoutBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (e.getItem().equals(LayoutType.FRLayout)) {
							frButton.setSelected(true);
						} else if (e.getItem().equals(LayoutType.EWLayout)) {
							ewButton.setSelected(true);
						}
					}
				}
			});
		}
		return layoutMenu;
	}

	private CommandListener refreshNVListener = new CommandAdapter() {
		@Override
		public void onExecuteEnd() {
			if (curGraph.getVertexCount() != 0) {
				refreshUI(true);
				refreshSubGraphView();
			} else {
				refreshUI(false);
				refreshSubGraphView();
			}

		}

		@Override
		public void onUnExecuteEnd() {
			if (curGraph.getVertexCount() != 0) {
				refreshUI(true);
				refreshSubGraphView();
			} else {
				refreshUI(false);
				refreshSubGraphView();
			}
		}
	};

	private CommandListener refreshCurListener = new CommandAdapter() {
		@Override
		public void onExecuteEnd() {
			curGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(curIndex - 1), Global.graph);
			if (curGraph.getVertexCount() != 0) {
				refreshUI(true);
				refreshSubGraphView();
			} else {
				refreshUI(false);
				refreshSubGraphView();
			}

		}

		@Override
		public void onUnExecuteEnd() {
			curGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(curIndex - 1), Global.graph);
			if (curGraph.getVertexCount() != 0) {
				refreshUI(true);
				refreshSubGraphView();
			} else {
				refreshUI(false);
				refreshSubGraphView();
			}
		}
	};

	private KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();
			Object object = e.getSource();
			if (keycode == KeyEvent.VK_ENTER) {
				if (object.equals(indexField)) {
					Pattern pattern = Pattern.compile("[0-9]+");
					String index = indexField.getText().trim();
					if (pattern.matcher(index).matches()) {
						int idx = Integer.parseInt(index);
						navToIndex(idx);
					} else {
						Global.APP_FRAME_PROXY.showError("Bad number format");
					}
				} else if (object.equals(searchField)) {
					String query = searchField.getText();
					SearchResultModel model = new SearchResultModel(subSetList,
							query);
					SearchResultFrame resultFrame = new SearchResultFrame(model);
					resultFrame.pack();
					resultFrame.setVisible(true);
				}
			}
		}
	};

	private CommandListener refreshUiListener = new CommandAdapter() {

		@Override
		public void onExecuteEnd() {
			if (Global.graph.getVertexCount() != 0) {
				refreshUI(true);
				subGraphInit();
			} else {
				refreshUI(false);
				subGraphInit();
			}
		}

		@Override
		public void onUnExecuteEnd() {
			if (Global.graph.getVertexCount() != 0) {
				refreshUI(true);
				subGraphInit();
			} else {
				refreshUI(false);
				subGraphInit();
			}
		}
	};

	// private CommandListener proListener = new CommandAdapter() {
	//
	// @Override
	// public void onExecuteStart() {
	// progressDialog.setVisible(true);
	// }
	//
	// @Override
	// public void onExecuteEnd() {
	// progressDialog.setVisible(false);
	// }
	// };

	/**
	 * actionListener for all commands and works
	 */
	private ActionListener commandActionListener = new ActionListener() {

		Command command;
		ArrayList<FileFilter> filterList = new ArrayList<FileFilter>();

		@Override
		public void actionPerformed(ActionEvent e) {
			Object eventSource = e.getSource();
			if (eventSource.equals(openItem) || eventSource.equals(openButton)) {
				filterList.clear();
				filterList.add(FileChooseTools.GML_FILTER);
				SelectedFile selectedFile = FileChooseTools.openFile(
						Global.APP_FRAME_PROXY.getFrame(), "Open Graph",
						filterList);

				if (selectedFile == null)
					return;
				command = new OpenCommand(selectedFile);
				command.addCommandListener(refreshUiListener);
				// command.addCommandListener(proListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(appendGraphItem)) {
				filterList.clear();
				filterList.add(FileChooseTools.GML_FILTER);

				SelectedFile selectedFile = FileChooseTools.openFile(
						Global.APP_FRAME_PROXY.getFrame(), "Append Graph",
						filterList);

				if (selectedFile == null)
					return;
				command = new AppendCommand(selectedFile);
				command.addCommandListener(refreshUiListener);
				// command.addCommandListener(proListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(newItem)
					|| eventSource.equals(newButton)) {
				filterList.clear();
				filterList.add(FileChooseTools.BLAST_XML_FILTER);

				SelectedFile selectedFile = FileChooseTools.openFile(
						Global.APP_FRAME_PROXY.getFrame(),
						"Create Graph From Blast", filterList);

				if (selectedFile == null)
					return;

				command = new NewCommand(selectedFile);
				command.addCommandListener(refreshUiListener);
				// command.addCommandListener(proListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(saveItem)
					|| eventSource.equals(saveButton)) {
				command = new SaveCommand(Global.graph);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(saveAsGraphItem)) {
				filterList.clear();
				filterList.add(FileChooseTools.GML_FILTER);
				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(), "Save Graph",
						filterList);

				if (selectedFile == null)
					return;
				command = new SaveAsCommand(Global.graph, selectedFile);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(saveAllVertexAttrItem)
					|| eventSource.equals(saveButton)) {
				filterList.clear();
				filterList.add(FileChooseTools.CSV_FILTER);

				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(),
						"Save All Vertex Info", filterList);

				if (selectedFile == null)
					return;

				Collection<HitVertex> vertexs = Global.graph.getVertices();

				command = new ExportVertexAttrCommand(vertexs, selectedFile);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(saveCurGraphItem)) {
				filterList.clear();
				filterList.add(FileChooseTools.GML_FILTER);
				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(), "Save Graph",
						filterList);

				if (selectedFile == null)
					return;
				command = new SaveAsCommand(curGraph, selectedFile);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);

			} else if (eventSource.equals(saveCurImgItem)
					|| eventSource.equals(saveCurImgButton)) {

				filterList.clear();
				filterList.add(FileChooseTools.PNG_FILTER);
				filterList.add(FileChooseTools.GIF_FILTER);
				filterList.add(FileChooseTools.JPG_FILTER);

				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(), "Save Image",
						filterList);

				if (selectedFile == null)
					return;

				command = new SaveImageCommand(vv, selectedFile);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(saveCurVertexAttrItem)) {
				filterList.clear();
				filterList.add(FileChooseTools.CSV_FILTER);

				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(),
						"Save Current Vertex Info", filterList);

				if (selectedFile == null)
					return;

				Collection<HitVertex> vertexs = curGraph.getVertices();

				command = new ExportVertexAttrCommand(vertexs, selectedFile);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(batchSaveItem)) {
				filterList.clear();
				filterList.add(FileChooseTools.GML_FILTER);
				filterList.add(FileChooseTools.CSV_FILTER);
				filterList.add(FileChooseTools.PNG_FILTER);

				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(), "Saving SubGraphs",
						filterList);

				if (selectedFile == null)
					return;

				command = new BatchSaveCommand(subSetList, vv, selectedFile);
				// command.addCommandListener(proListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(importVertexAttrItem)) {
				filterList.clear();
				filterList.add(FileChooseTools.CSV_FILTER);

				SelectedFile selectedFile = FileChooseTools.openFile(
						Global.APP_FRAME_PROXY.getFrame(),
						"Import Vertex Info", filterList);

				if (selectedFile == null)
					return;

				command = new ImportVertexAttrCommand(selectedFile);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(exportGraphItem)) {

				filterList.clear();
				filterList.add(FileChooseTools.NT_FILTER);
				filterList.add(FileChooseTools.SP_FILTER);
				filterList.add(FileChooseTools.CF_FILTER);

				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(), "Export As",
						filterList);

				if (selectedFile == null)
					return;

				command = new ExportCommand(selectedFile);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(closeItem)) {
				command = new CloseCommand();
				command.addCommandListener(refreshUiListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(exitItem)) {
				exitFrame();
			} else if (eventSource.equals(removeSingleItem)) {
				command = new RemoveSingleCommand();
				command.addCommandListener(refreshUiListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(filterItem)
					|| eventSource.equals(filterButton)) {
				command = new FiltCommand(curIndex - 1);
				command.addCommandListener(refreshCurListener);
				FiltDialog filtDialog = new FiltDialog(
						Global.APP_FRAME_PROXY.getFrame(),
						(FiltCommand) command);
				filtDialog.pack();
				filtDialog.setVisible(true);
			} else if (eventSource.equals(genomeNumFiltItem)) {
				command = new GenomeNumFiltCommand();
				command.addCommandListener(refreshCurListener);
				// command.addCommandListener(proListener);
				GenomeNumFiltDialog genomeNumFiltDialog = new GenomeNumFiltDialog(
						Global.APP_FRAME_PROXY.getFrame(),
						(GenomeNumFiltCommand) command);
				genomeNumFiltDialog.pack();
				genomeNumFiltDialog.setVisible(true);
			} else if (eventSource.equals(markovClusterItem)) {
				command = new MclCommand();
				command.addCommandListener(refreshUiListener);

				MclDialog dialog = new MclDialog(
						Global.APP_FRAME_PROXY.getFrame(), (MclCommand) command);
				dialog.pack();
				dialog.setVisible(true);
			} else if (eventSource.equals(mstItem)) {
				command = new MinimumSpanningTreeCommand(curGraph);
				command.addCommandListener(new CommandAdapter() {
					@Override
					public void onUnExecuteEnd() {
						refreshSubGraphView();

					}

					@Override
					public void onExecuteEnd() {
						refreshSubGraphView();

					}
				});
				// command.addCommandListener(proListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(removeSingleLinkageItem)) {
				command = new RemoveSingleLinkageCommand();
				command.addCommandListener(refreshCurListener);
				// command.addCommandListener(proListener);
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.NEW_THREAD);
			} else if (eventSource.equals(settingItem)) {
				command = new SettingCommand();
				Global.COMMAND_MANAGER.putCommand(command,
						CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(printItem)
					|| eventSource.equals(printButton)) {
				PrintUtilities.printComponent(vv);
			} else if (eventSource.equals(previousItem)
					|| eventSource.equals(previousButton)) {
				int index = curIndex - 1;
				navToIndex(index);
			} else if (eventSource.equals(nextItem)
					|| eventSource.equals(nextButton)) {
				int index = curIndex + 1;
				navToIndex(index);
			} else if (eventSource.equals(resortItem)
					|| eventSource.equals(resortButton)) {
				subGraphInit();
			} else if (eventSource.equals(viewNeighborItem)) {
				command = new ViewNeighborCommand(curGraph);
				command.addCommandListener(refreshNVListener);
				NeighborViewDialog nvDialog = new NeighborViewDialog(
						Global.APP_FRAME_PROXY.getFrame(),
						(ViewNeighborCommand) command);
				nvDialog.pack();
				nvDialog.setVisible(true);

			} else if (eventSource.equals(searchButton)) {
				String query = searchField.getText();
				SearchResultModel model = new SearchResultModel(subSetList,
						query);
				SearchResultFrame resultFrame = new SearchResultFrame(model);
				resultFrame.pack();
				resultFrame.setVisible(true);
			} else if (eventSource.equals(geneContentItem)) {
				if (geneContentFrame == null) {
					geneContentModel = new GeneContentModel(subSetList);
					geneContentFrame = new GeneContentFrame(geneContentModel);
					geneContentFrame.setVisible(true);
				} else if (geneContentFrame.isVisible()) {
					geneContentFrame.setVisible(false);
				} else {
					geneContentModel = new GeneContentModel(subSetList);
					geneContentFrame.setModel(geneContentModel);
					geneContentFrame.setVisible(true);
				}
				// } else if (eventSource.equals(tempWorkItem)) {
				// command = new RemovePercenteEdgeCommand(0.4, 0.01);
				// Global.COMMAND_MANAGER.putCommand(command,
				// CommandManager.CURRENT_THREAD);
			} else if (eventSource.equals(helpContentItem)) {
				showHelpContent();
			} else if (eventSource.equals(aboutItem)) {
				showAbout();
			}
		}
	};

	private void exitFrame() {
		// TODO save or confirm exit
		try {
			Global.PREFERENCES.clear();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * 
	 * @param isOpen
	 *            is graph open
	 */
	private void refreshUI(boolean isOpen) {
		saveItem.setEnabled(isOpen);
		saveAsMenu.setEnabled(isOpen);
		exportGraphItem.setEnabled(isOpen);
		batchSaveItem.setEnabled(isOpen);
		closeItem.setEnabled(isOpen);
		appendGraphItem.setEnabled(isOpen);
		importVertexAttrItem.setEnabled(isOpen);
		// tempWorkItem.setEnabled(isOpen);

		filterItem.setEnabled(isOpen);
		removeSingleItem.setEnabled(isOpen);
		// genomeNumFiltItem.setEnabled(isOpen);
		markovClusterItem.setEnabled(isOpen);
		mstItem.setEnabled(isOpen);
		// removeSingleLinkageItem.setEnabled(isOpen);
		viewNeighborItem.setEnabled(isOpen);

		saveButton.setEnabled(isOpen);
		saveCurImgButton.setEnabled(isOpen);
		printItem.setEnabled(isOpen);
		printButton.setEnabled(isOpen);
		filterButton.setEnabled(isOpen);
		resortButton.setEnabled(isOpen);
		resortItem.setEnabled(isOpen);
		previousItem.setEnabled(isOpen);
		previousButton.setEnabled(isOpen);
		indexField.setEnabled(isOpen);
		nextItem.setEnabled(isOpen);
		nextButton.setEnabled(isOpen);
		searchField.setEnabled(isOpen);
		searchButton.setEnabled(isOpen);

		colorComboBox.setEnabled(isOpen);
		refreshColorControl();

		geneContentItem.setEnabled(isOpen);

		if (isOpen) {
			// evalueField.setEnabled(true);
			// evalueField.setText("0.01");
			// coverageField.setEnabled(false);
			// coverageField.setText("0.25");
			// filterComboBox.setEnabled(true);
			// filterComboBox.setSelectedIndex(0);
			// filtButton.setEnabled(true);

			vertexCountLabel.setText("" + Global.graph.getVertexCount());
			edgeCountLabel.setText("" + Global.graph.getEdgeCount());

		} else {
			// evalueField.setEnabled(false);
			// coverageField.setEnabled(false);
			// filterComboBox.setEnabled(false);
			// filtButton.setEnabled(false);

			vertexCountLabel.setText("0");
			edgeCountLabel.setText("0");
		}
	}

	private void refreshColorControl() {
		// find the minimal taxon length and attribute key collection
		Collection<HitVertex> hvs = Global.graph.getVertices();
		int minTaxon = 0;
		Collection<String> minKeySet = new HashSet<String>();
		for (HitVertex hitVertex : hvs) {
			int value = hitVertex.getTaxon().getLevels();
			Set<String> keySet = hitVertex.getAllAttributes().keySet();
			if (minTaxon == 0) {
				minTaxon = value;
				minKeySet = keySet;
			} else {
				minTaxon = value < minTaxon ? value : minTaxon;
				minKeySet = CollectionUtils.intersection(minKeySet, keySet);
			}
		}

		// clear taxons and attrs
		// final int seleceted = colorComboBox.getSelectedIndex();
		for (int i = colorComboBox.getItemCount() - 1; i >= 5; i--) {
			colorComboBox.removeItemAt(i);
		}

		// add taxons
		for (int i = 0; i < minTaxon; i++) {
			colorComboBox.addItem("taxon" + i);
		}

		// add attrs
		for (String string : minKeySet) {
			colorComboBox.addItem(string);
		}

		colorComboBox.setSelectedIndex(0);

		final int min = minTaxon;

		if (colorItemListener != null) {
			colorComboBox.removeItemListener(colorItemListener);
		}

		colorItemListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				int index = colorComboBox.getSelectedIndex();
				String attr = (String) colorComboBox.getSelectedItem();
				ArrayList<String> attrValues = new ArrayList<String>();
				Map<String, Color> colorMap = new HashMap<String, Color>();
				Collection<HitVertex> hvs = Global.graph.getVertices();

				String value = "";
				if (index != 0) {
					for (HitVertex hitVertex : hvs) {
						if (index < 5) {
							switch (index) {
							case 1:
								value = hitVertex.getIndex() + "";
								break;
							case 2:
								value = hitVertex.getStrand() + "";
								break;
							case 3:
								value = hitVertex.getGenomeAcc();
								break;
							case 4:
								value = hitVertex.getOrganism();
								break;
							default:
								break;
							}
						} else if (index < (5 + min)) {
							value = hitVertex.getTaxon().getTaxon(index - 5);
						} else {
							value = hitVertex
									.getAttribute((String) colorComboBox
											.getItemAt(index));

							System.out.println(index + " " + "attr");
						}

						if (!attrValues.contains(value)) {
							attrValues.add(value);
						}
					}
				}

				float hue = 0;
				int size = attrValues.size();
				for (int i = 0; i < size; i++) {
					Color c = Color.getHSBColor(hue, 0.85f, 1.0f);
					colorMap.put(attrValues.get(i), c);
					hue += 1.0f / size;
				}

				if (index < (5 + min)) {
					rc.setVertexFillPaintTransformer(new VertexPainter(index,
							colorMap));
				} else {
					rc.setVertexFillPaintTransformer(new AttrVertexPainter(
							attr, colorMap));
				}
				vv.repaint();
			}
		};

		colorComboBox.addItemListener(colorItemListener);
	}

	private void refreshSubGraphView() {
		int count = curGraph.getVertexCount();
		int maxCount = Integer.parseInt(Global.SETTING.get("MAX_VERTICES"));
		if (count != 0) {
			graphStatisticsPanel.setGraph(curGraph);
			int edgeCount = curGraph.getEdgeCount();
			int vertexCount = curGraph.getVertexCount();
			double degreePct = 0;

			if (vertexCount != 1) {
				degreePct = edgeCount * 1.0
						/ (((vertexCount - 1) * vertexCount) / 2);
			}
			currentVertexCountLabel.setText("" + vertexCount);
			currentEdgeCountLabel.setText("" + edgeCount);
			currentDegreePercentageLabel.setText(String.format("%1$.2f",
					degreePct));
			int option;
			if (count > maxCount) {
				option = JOptionPane.showConfirmDialog(this, "Too large("
						+ count + " vertices>" + maxCount
						+ ") to be displayed.Are you sure to display it?",
						"Warning", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			} else {
				option = JOptionPane.YES_OPTION;
			}

			if (option == JOptionPane.NO_OPTION) {
				layout.setGraph(emptyGraph);
				vv.setGraphLayout(layout);
				vv.repaint();
				return;
			}

			vvPanel.setEnabled(true);
			tabbedPane.setEnabled(true);
			saveCurGraphMenu.setEnabled(true);
			modeMenu.setEnabled(true);
			layoutMenu.setEnabled(true);
			modeBox.setEnabled(true);
			layoutBox.setEnabled(true);
			graphStatisticsPanel.setEnabled(true);
		} else {
			vvPanel.setEnabled(false);
			tabbedPane.setEnabled(false);
			saveCurGraphMenu.setEnabled(false);
			currentVertexCountLabel.setText("0");
			currentEdgeCountLabel.setText("0");
			currentDegreePercentageLabel.setText("0");
			selectedVertexCountLabel.setText("0");
			selectedEdgeCountLabel.setText("0");
			modeMenu.setEnabled(false);
			layoutMenu.setEnabled(false);
			modeBox.setEnabled(false);
			layoutBox.setEnabled(false);
			graphStatisticsPanel.setEnabled(false);

			int annotCount = annotationToolBar.getComponentCount();
			for (int i = 0; i < annotCount; i++) {
				annotationToolBar.getComponent(i).setEnabled(false);
			}
		}

		layout.setGraph(curGraph);
		vv.setGraphLayout(layout);
		vv.repaint();
	}

	private void navToIndex(int index) {
		int max = subSetList.size();
		if (index < 1 || index > max) {
			Global.APP_FRAME_PROXY.showError("Bad index number");
		} else {
			curIndex = index;
			if (curIndex == 1) {
				previousButton.setEnabled(false);
				previousItem.setEnabled(false);
			}

			if (curIndex == max) {
				nextButton.setEnabled(false);
				nextItem.setEnabled(false);
			}

			if (index > 1 && index < max) {
				previousButton.setEnabled(true);
				previousItem.setEnabled(true);
				nextButton.setEnabled(true);
				nextItem.setEnabled(true);
			}

			indexField.setText("" + curIndex);
			curGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(curIndex - 1), Global.graph);
			refreshSubGraphView();
		}
	}

	@SuppressWarnings("unchecked")
	private Layout<HitVertex, ValueEdge> getLayoutFor(LayoutType layout,
			Graph graph) throws Exception {
		Object[] args = new Object[] { graph };
		Class layoutClass;

		if (layout == LayoutType.EWLayout) {
			layoutClass = Class
					.forName("com.yeyanbo.bio.blastgraph.gui.graphvisualization.EWLayout");
		} else {
			layoutClass = Class.forName("edu.uci.ics.jung.algorithms.layout."
					+ layout);
		}

		Constructor constructor = layoutClass
				.getConstructor(new Class[] { Graph.class });
		return (Layout<HitVertex, ValueEdge>) constructor.newInstance(args);
	}

	private void showHelpContent() {

		try {
			URI helpLink = new URI(
					"https://github.com/bigwiv/BlastGraph/blob/master/doc/BlastGraph_Manual.md");
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop
					.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(helpLink);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showAbout() {
		String text = "BlastGraph is a user interactive Java program for comparative \n"
					+ "genomics analysis based on BLAST, graph clustering algorithms \n"
					+ "and data visualization libraries.\n"
					+ "\n"
					+ "Better to use SUN JDK&JRE for performance.\n"
					+ "\n"
					+ "Author: Yanbo Ye\n" + "Mail: yeyanbo289@gmail.com";

		JOptionPane.showMessageDialog(this, text,
				"About " + Global.APP_VERSION, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String args[]) {
		new BlastGraphFrame(Global.APP_VERSION);
	}
}
