/**
 * 
 */
package org.bigwiv.blastgraph.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.transform.Source;

import org.bigwiv.bio.phylogeny.BitArray;
import org.bigwiv.bio.phylogeny.PhyloAlgorithms;
import org.bigwiv.bio.phylogeny.Tree;
import org.bigwiv.bio.phylogeny.TreePanel;
import org.bigwiv.bio.phylogeny.TreeTools;
import org.bigwiv.bio.phylogeny.TreePanel.GainLossType;
import org.bigwiv.bio.phylogeny.TreePanel.Mode;
import org.bigwiv.blastgraph.global.Global;

import sun.awt.X11.XLayerProtocol;


/**
 * TreeFrame include TreePanel and controls.
 * @author yeyanbo
 * 
 */
public class TreeFrame extends JFrame implements MouseWheelListener,
		MouseListener, MouseMotionListener, ChangeListener, ActionListener {
	private Tree tree;

	private JButton saveButton;
	private JButton saveImageButton;

	// zoom tool
	private SpinnerNumberModel zoomX;
	private SpinnerNumberModel zoomY;
	private JSpinner zoomerX;
	private JSpinner zoomerY;

	// tree mode tool
	private JRadioButton viewRadio, swapRadio, reRootRadio;

	// tree view tool
	private JCheckBox cladoBox, circularBox;

	private JCheckBox branchLengthBox, branchSupportBox;

	private JCheckBox gainLossBox, glPieChartBox, glDataBox;
	//gene gain and loss estimation type
	private JRadioButton mappingRadio, parsimonyRadio;
	
	// for pie chart transparency
	private JSlider alphaSlider;

	private JScrollPane scrollPane;
	private TreePanel treePanel;

	private Point startPoint;
	
	// is tree editable
	private boolean editable;

	public TreeFrame(Tree tree) {
		this.tree = tree;
		this.editable = true;
		initComponents();
	}

	/**
	 * @return the treePanel
	 */
	public TreePanel getTreePanel() {
		return treePanel;
	}

	/**
	 * @param treePanel
	 *            the treePanel to set
	 */
	public void setTreePanel(TreePanel treePanel) {
		this.treePanel = treePanel;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	private void initComponents() {
		this.setTitle(tree.getTreeName());
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		URL icon;

		saveButton = new JButton();
		saveButton.setBorderPainted(false);
		saveButton.setMnemonic('s');
		saveButton.setToolTipText("Save Tree");
		saveButton.addActionListener(this);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/filesave.png");
		saveButton.setIcon(new ImageIcon(icon));

		saveImageButton = new JButton();
		saveImageButton.setBorderPainted(false);
		saveImageButton.setToolTipText("Save Image");
		saveImageButton.addActionListener(this);
		icon = getClass().getResource(
				"/org/bigwiv/blastgraph/icons/saveimage.png");
		saveImageButton.setIcon(new ImageIcon(icon));

		viewRadio = new JRadioButton();
		viewRadio.setBorderPainted(false);
		viewRadio.setSelected(true);
		swapRadio = new JRadioButton();
		swapRadio.setBorderPainted(false);
		swapRadio.setEnabled(editable);
		reRootRadio = new JRadioButton();
		reRootRadio.setBorderPainted(false);
		reRootRadio.setEnabled(editable);
		viewRadio.addActionListener(this);
		swapRadio.addActionListener(this);
		reRootRadio.addActionListener(this);

		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(viewRadio);
		modeGroup.add(swapRadio);
		modeGroup.add(reRootRadio);

		zoomX = new SpinnerNumberModel(1, 1, 4, 0.5);
		zoomY = new SpinnerNumberModel(1, 1, 4, 0.5);

		zoomerX = new JSpinner(zoomX);
		zoomerX.addChangeListener(this);

		zoomerY = new JSpinner(zoomY);
		zoomerY.addChangeListener(this);

		//tree type data
		cladoBox = new JCheckBox();
		cladoBox.addActionListener(this);
		circularBox = new JCheckBox();
		circularBox.addActionListener(this);

		//branch data
		branchLengthBox = new JCheckBox();
		branchLengthBox.addActionListener(this);
		branchSupportBox = new JCheckBox();
		branchSupportBox.addActionListener(this);
		
		//gene gain loss data
		gainLossBox = new JCheckBox();
		gainLossBox.addActionListener(this);
		glPieChartBox = new JCheckBox();
		glPieChartBox.setSelected(true);
		glPieChartBox.setEnabled(false);
		glPieChartBox.addActionListener(this);
		glDataBox = new JCheckBox();
		glDataBox.setEnabled(false);
		glDataBox.addActionListener(this);
		mappingRadio = new JRadioButton();
		mappingRadio.setSelected(true);
		mappingRadio.setEnabled(false);
		mappingRadio.addActionListener(this);
		parsimonyRadio = new JRadioButton();
		parsimonyRadio.setEnabled(false);
		parsimonyRadio.addActionListener(this);
		ButtonGroup gainLossGroup = new ButtonGroup();
		gainLossGroup.add(mappingRadio);
		gainLossGroup.add(parsimonyRadio);
		alphaSlider = new JSlider(0, 100, 60);
		alphaSlider.setEnabled(false);
		alphaSlider.addChangeListener(this);

		JToolBar fileBar = new JToolBar();
		fileBar.add(saveButton);
		fileBar.add(saveImageButton);

		JToolBar modeBar = new JToolBar();
		modeBar.add(viewRadio);
		modeBar.add(new JLabel("View"));
		modeBar.add(swapRadio);
		modeBar.add(new JLabel("Swap"));
		modeBar.add(reRootRadio);
		modeBar.add(new JLabel("Reroot"));

		JToolBar zoomBar = new JToolBar();
		zoomBar.add(new JLabel("X: "));
		zoomBar.add(zoomerX);
		zoomBar.add(new JLabel("Y: "));
		zoomBar.add(zoomerY);

		JToolBar typeBar = new JToolBar();
		typeBar.add(cladoBox);
		typeBar.add(new JLabel("Cladogram"));
		typeBar.add(circularBox);
		typeBar.add(new JLabel("Circular"));

		JToolBar branchDataBar = new JToolBar();
		branchDataBar.add(branchLengthBox);
		branchDataBar.add(new JLabel("BranchLength"));
		branchDataBar.add(branchSupportBox);
		branchDataBar.add(new JLabel("BranchSupport"));
		
		JToolBar gainLossDataBar = new JToolBar();
		gainLossDataBar.add(gainLossBox);
		gainLossDataBar.add(new JLabel("GainLoss"));
		gainLossDataBar.addSeparator();
		gainLossDataBar.add(glPieChartBox);
		gainLossDataBar.add(new JLabel("PieChart"));
		gainLossDataBar.add(glDataBox);
		gainLossDataBar.add(new JLabel("Data"));
		gainLossDataBar.addSeparator();
		gainLossDataBar.add(mappingRadio);
		gainLossDataBar.add(new JLabel("Mapping"));
		gainLossDataBar.add(parsimonyRadio);
		gainLossDataBar.add(new JLabel("Parsimony "));
		gainLossDataBar.addSeparator();
		gainLossDataBar.add(new JLabel(" Transparency"));
		gainLossDataBar.add(alphaSlider);

		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new ModifiedFlowLayout(ModifiedFlowLayout.LEFT, 0, 0));
		toolPanel.setBorder(new EtchedBorder());

		toolPanel.add(fileBar);
		toolPanel.add(modeBar);
		toolPanel.add(zoomBar);
		toolPanel.add(typeBar);
		toolPanel.add(branchDataBar);
		toolPanel.add(gainLossDataBar);
		contentPane.add(toolPanel, BorderLayout.NORTH);

		treePanel = new TreePanel(tree);
		scrollPane = new JScrollPane(treePanel);
		scrollPane.addMouseWheelListener(this);
		scrollPane.addMouseMotionListener(this);
		scrollPane.addMouseListener(this);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				viewUpdate();
			}
		});
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 3, screen.height / 10);
	}

	/**
	 * zoom by mouse wheel and maintain zoom point
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int direction = e.getWheelRotation();
		double x, y;
		x = (Double) zoomX.getValue();
		y = (Double) zoomY.getValue();

		Object newX, newY;

		if (direction == 1) {
			newX = zoomX.getNextValue();
			newY = zoomY.getNextValue();
		} else {
			newX = zoomX.getPreviousValue();
			newY = zoomY.getPreviousValue();
		}

		JViewport viewport = scrollPane.getViewport();

		// original scroll Pane point
		Point sl = e.getPoint();

		Point vp = SwingUtilities.convertPoint(scrollPane, sl, viewport);

		// original treePanel point

		Point zeroPoint = SwingUtilities
				.convertPoint(viewport, 0, 0, treePanel);
		Point tpPoint = SwingUtilities.convertPoint(viewport, vp, treePanel);

		int tpDistX = tpPoint.x - zeroPoint.x;
		int tpDistY = tpPoint.y - zeroPoint.y;

		if (newX != null) {
			zoomX.setValue(newX);
		}

		if (newY != null) {
			zoomY.setValue(newY);
		}

		double curX = (Double) zoomX.getValue();
		double curY = (Double) zoomY.getValue();

		Point viewPoint = new Point((int) (tpPoint.x * curX / x - tpDistX),
				(int) (tpPoint.y * curY / y - tpDistY));

		viewport.setViewPosition(viewPoint);
	}

	private void viewUpdate() {
		Dimension d = scrollPane.getViewport().getExtentSize();
		double x = (Double) (zoomX.getValue());
		double y = (Double) (zoomY.getValue());
		treePanel.setPreferredSize(new Dimension((int) (d.width * x),
				(int) (d.height * y)));
		treePanel.revalidate();
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		JViewport viewport = scrollPane.getViewport();

		Point sl = e.getLocationOnScreen();

		Point move = new Point();
		move.setLocation(sl.x - startPoint.x, sl.y - startPoint.y);

		startPoint.setLocation(sl);

		Rectangle viewrRectangle = viewport.getViewRect();
		int w = viewrRectangle.width;
		int h = viewrRectangle.height;

		Point zeroPoint = SwingUtilities
				.convertPoint(viewport, 0, 0, treePanel);
		Rectangle rect = new Rectangle();

		rect.setRect(zeroPoint.x - move.x, zeroPoint.y - move.y, w, h);

		treePanel.scrollRectToVisible(rect);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JViewport viewport = scrollPane.getViewport();
		Point point = SwingUtilities.convertPoint(viewport, e.getPoint(), treePanel);
		treePanel.mouseAction(point);
		treePanel.revalidate();
		this.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = e.getLocationOnScreen();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		startPoint = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (!source.equals(saveButton) && !source.equals(saveImageButton)) {
			if (source.equals(viewRadio)) {
				treePanel.setMode(Mode.view);
			} else if (source.equals(swapRadio)) {
				treePanel.setMode(Mode.swap);
			} else if (source.equals(reRootRadio)) {
				treePanel.setMode(Mode.reRoot);
			} else if (source.equals(cladoBox)) {
				boolean s = cladoBox.isSelected();
				treePanel.setCladogram(s);
			} else if (source.equals(circularBox)) {
				boolean s = circularBox.isSelected();
				treePanel.setCircular(s);
			} else if (source.equals(gainLossBox)) {
				boolean s = gainLossBox.isSelected();
				treePanel.setGainLoss(s);
				glPieChartBox.setEnabled(s);
				glDataBox.setEnabled(s);
				mappingRadio.setEnabled(s);
				parsimonyRadio.setEnabled(s);
				alphaSlider.setEnabled(s);
			} else if (source.equals(glPieChartBox)) {
				boolean s = glPieChartBox.isSelected();
				treePanel.setGlPieChart(s);
			} else if (source.equals(glDataBox)) {
				boolean s = glDataBox.isSelected();
				treePanel.setGlData(s);
			} else if (source.equals(mappingRadio)) {
				treePanel.setGainLossType(GainLossType.mapping);
			} else if (source.equals(parsimonyRadio)) {
				treePanel.setGainLossType(GainLossType.parsimony);
			} else if (source.equals(branchLengthBox)) {
				boolean s = branchLengthBox.isSelected();
				treePanel.setBranchLength(s);
			} else if (source.equals(branchSupportBox)) {
				boolean s = branchSupportBox.isSelected();
				treePanel.setBranchSupport(s);
			}
			
			viewUpdate();
		} else {
			ArrayList<FileFilter> filterList = new ArrayList<FileFilter>();
			if (source.equals(saveButton)) {
				filterList.clear();
				filterList.add(FileChooseTools.NWK_FILTER);

				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(), "Save Tree",
						filterList);

				if (selectedFile == null)
					return;
				String treeFile = selectedFile.getFile().getPath();

				try {
					TreeTools.writeNewick(tree, treeFile,
							treePanel.isBranchSupport());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else if (source.equals(saveImageButton)) {
				filterList.clear();
				filterList.add(FileChooseTools.PNG_FILTER);

				SelectedFile selectedFile = FileChooseTools.saveFile(
						Global.APP_FRAME_PROXY.getFrame(), "Save Tree Image",
						filterList);

				if (selectedFile == null)
					return;
				String imageFile = selectedFile.getFile().getPath();

				try {
					treePanel.saveImage(imageFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source.equals(alphaSlider)) {
			int value = alphaSlider.getValue();
			treePanel.setAlpha(value * 1.0f / 100);
		}
		viewUpdate();
	}
}
