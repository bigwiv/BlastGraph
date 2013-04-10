package org.bigwiv.bio.phylogeny;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.bigwiv.blastgraph.gui.TreeFrame;


/**
 * display the tree this class should be merged into TreeFrame, or provides a
 * group of controls
 * 
 * @author yeyanbo
 * 
 */
public class TreePanel extends JPanel {
	private Tree tree;
	private Map<String, BitArray> geneContent;
	private double treeLength; // max branch length sum
	private double treeHeight; // max branch level

	private int padding;// padding space
	private int labelWidth;// with for label text

	private int treeAreaWidth; // width for tree drawing
	private int treeAreaHeight; // height for tree drawing

	private int treeRadius;// radius for circular tree
	private int centerX;// panel center point X;
	private int centerY;// panel center point Y;

	private int curTerm;// for term counting

	private int unitWith; // width per branch Length or width per treeHeight for
							// cladogram

	private int termNum; // terminal node number
	private int termHeight; // terminal node height for normal tree
	private int termArc; // terminal node arc for circular tree

	// tree type
	private boolean isCladogram;
	private boolean isCircular;

	// branch data plot
	private boolean isBranchLength;
	private boolean isBranchSupport;

	// gain loss piechart and data plot
	private boolean isGainLoss;
	private boolean isGlPieChart;
	private boolean isGlData;

	// type for estimation of gene gain and loss
	public enum GainLossType {
		mapping, parsimony
	}

	private GainLossType gainLossType;

	// tree mode editing
	public enum Mode {
		view, reRoot, swap,
	}

	private Mode mode;

	// alpha parameter for pie chart transparency
	private float alpha;

	/**
	 * 
	 * @param tree
	 */
	public TreePanel(Tree tree) {
		super();
		this.setTree(tree);
		this.setGeneContent(null);
		this.setCladogram(false);
		this.setCircular(false);
		this.setBranchLength(false);
		this.setBranchSupport(false);
		this.setGainLoss(false);
		this.setGlPieChart(true);
		this.setGlData(false);
		this.setGainLossType(GainLossType.mapping);
		this.setMode(Mode.view);
		this.setAlpha(0.6f);
		refreshTreeInfo();
		this.setMinimumSize(new Dimension(labelWidth, termNum));
	}

	private void refreshTreeInfo() {
		this.termNum = tree.getTermNum();
		this.treeLength = TreeTools.treeLength(tree);
		this.treeHeight = TreeTools.treeHeight(tree);

		padding = 30;// default padding space

		Set<Node> nodes = tree.getNodes();
		int max = 0;
		for (Node node : nodes) {
			if (node.isTerminal()) {
				int length = node.getLabel().length();
				max = max > length ? max : length;
			}
		}

		labelWidth = max * 7;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);

		// draw background
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		Graphics2D g2d = (Graphics2D) g;

		int width = this.getWidth();
		int height = this.getHeight();

		treeAreaWidth = width - 2 * padding; // width for tree and tree label
		treeAreaHeight = height - 2 * padding; // height for tree
		centerX = width / 2;
		centerY = height / 2;

		Node rootNode = tree.getRootNode();
		termHeight = treeAreaHeight / termNum;
		termArc = 360 / termNum;

		// choose smaller value as tree diameter
		treeRadius = treeAreaHeight > treeAreaWidth ? treeAreaWidth / 2
				- labelWidth : treeAreaHeight / 2 - labelWidth;

		curTerm = 0;
		g2d.setColor(Color.black);

		// draw tree
		if (treeLength != 0 && !isCladogram && !isCircular) {
			unitWith = (int) ((treeAreaWidth - labelWidth) / treeLength);
			// draw ruler
			drawRuler(g2d);
			// draw tree
			int xStart = padding;
			rootNode.addNodeData("pointX",
					(int) (xStart + rootNode.getBranchLength() * unitWith));
			drawBranch(g2d, rootNode);
		} else if (treeLength != 0 && !isCladogram && isCircular) {
			unitWith = (int) (treeRadius / treeLength);
			drawRuler(g2d);
			int radius = (int) (unitWith * rootNode.getBranchLength());
			rootNode.addNodeData("radius", radius);
			drawCircularBranch(g2d, rootNode);
		} else if ((treeLength != 0 && isCladogram && isCircular)
				|| (treeLength == 0 && isCircular)) {
			unitWith = (int) (treeRadius / (treeHeight - 1));
			rootNode.addNodeData("radius", 0);
			drawCircularCladoBranch(g2d, rootNode);
		} else {
			unitWith = (int) ((treeAreaWidth - labelWidth) / (treeHeight - 1));

			int xStart = padding;
			rootNode.addNodeData("pointX", xStart);

			drawCladoBranch(g2d, rootNode);
		}

		// draw branch length
		if (isBranchLength && treeLength != 0 && !isCladogram) {
			drawData(g2d, "bl", 0.5, 0.2);
		}

		// draw branch support
		if (isBranchSupport) {
			drawData(g2d, "bs", 0.5, -1);
		}

		// draw pie chart
		if (isGainLoss && geneContent != null) {

			if (gainLossType == GainLossType.mapping) {
				PhyloAlgorithms.geneTreeMapping(tree, geneContent);
			} else {
				PhyloAlgorithms.geneTreeParsimony(tree, geneContent);
			}

			if (isGlPieChart) {
				drawPieChart(g2d);
			}
			if (isGlData) {
				drawData(g2d, "gl", 1, 0.2);
			}
		}

		// draw edit square
		if (mode != Mode.view) {
			drawEditSquare(g2d);
		}
	}

	/**
	 * draw ruler
	 * 
	 * @param g2d
	 */
	private void drawRuler(Graphics2D g2d) {
		double rulerLength = treeLength * 0.1;
		int rulerWith = (int) (unitWith * rulerLength);
		DecimalFormat formater = new DecimalFormat("0.00");
		g2d.drawString(formater.format(rulerLength), padding + 5, padding - 10);
		g2d.drawLine(padding, padding - 5, padding, padding);
		g2d.drawLine(padding + rulerWith, padding - 5, padding + rulerWith,
				padding);
		g2d.drawLine(padding, padding, padding + rulerWith, padding);
	}

	/**
	 * draw a normal branch with branch length and return the y position
	 * 
	 * @param g2d
	 * @param node
	 * @param xStart
	 * @param yStart
	 * @param unitWith
	 * @param height
	 */
	private int drawBranch(Graphics2D g2d, Node node) {
		int xStart, pointX, pointY;
		pointX = (Integer) node.getNodeData("pointX");
		xStart = pointX - (int) (node.getBranchLength() * unitWith);

		if (node.isTerminal()) {
			curTerm++;
			// System.out.println(curTerm);
			pointY = padding + termHeight * curTerm - termHeight / 2;
			g2d.drawString(node.getLabel(), pointX + 10, pointY + 5);
		} else {
			Node left = node.getLeftChild();
			Node right = node.getRightChild();
			left.addNodeData("pointX", (int) (pointX + left.getBranchLength()
					* unitWith));
			right.addNodeData("pointX", (int) (pointX + right.getBranchLength()
					* unitWith));

			int leftY = drawBranch(g2d, left);
			int rightY = drawBranch(g2d, right);
			pointY = (leftY + rightY) / 2;

			g2d.drawLine(pointX, pointY, pointX, leftY);
			g2d.drawLine(pointX, pointY, pointX, rightY);
		}

		node.addNodeData("pointY", pointY);
		g2d.drawLine(xStart, pointY, pointX, pointY);

		return pointY;
	}

	/**
	 * draw a cladogram branch and return the y position
	 * 
	 * @param g2d
	 * @param node
	 * @param xStart
	 * @param yStart
	 * @param unitWith
	 * @param height
	 */
	private int drawCladoBranch(Graphics2D g2d, Node node) {
		int xStart, pointX, pointY;
		pointX = (Integer) node.getNodeData("pointX");
		if (node.isRoot()) {
			xStart = pointX;
		} else {
			Node sister = node.getSister();
			int sisterPointX = (Integer) sister.getNodeData("pointX");
			xStart = pointX < sisterPointX ? pointX : sisterPointX;
			xStart = xStart - unitWith;
		}

		if (node.isTerminal()) {
			curTerm++;
			// System.out.println(curTerm);
			pointY = padding + termHeight * curTerm - termHeight / 2;
			g2d.drawString(node.getLabel(), pointX + 10, pointY + 5);
		} else {
			Node left = node.getLeftChild();
			int leftHeight = TreeTools.nodeHeight(left);
			Node right = node.getRightChild();
			int rightHeight = TreeTools.nodeHeight(right);

			left.addNodeData("pointX", padding
					+ (int) (unitWith * (treeHeight - leftHeight)));
			right.addNodeData("pointX", padding
					+ (int) (unitWith * (treeHeight - rightHeight)));

			int leftY = drawCladoBranch(g2d, left);
			int rightY = drawCladoBranch(g2d, right);
			pointY = (leftY + rightY) / 2;

			g2d.drawLine(pointX, pointY, pointX, leftY);
			g2d.drawLine(pointX, pointY, pointX, rightY);
		}

		node.addNodeData("pointY", pointY);
		g2d.drawLine(xStart, pointY, pointX, pointY);

		return pointY;
	}

	/**
	 * draw a normal circular branch and return the arc
	 * 
	 * @param g2d
	 * @param node
	 * @return
	 */
	private int drawCircularBranch(Graphics2D g2d, Node node) {
		int radius, preRadius, arc;
		radius = (Integer) node.getNodeData("radius");
		preRadius = radius - (int) (node.getBranchLength() * unitWith);
		if (node.isTerminal()) {
			curTerm++;
			arc = termArc * (curTerm - 1);
			// System.out.println("termArc: " + arc + "\tcurTerm: " + curTerm);
		} else {
			Node left = node.getLeftChild();
			Node right = node.getRightChild();
			left.addNodeData("radius", (int) (radius + left.getBranchLength()
					* unitWith));
			right.addNodeData("radius", (int) (radius + right.getBranchLength()
					* unitWith));

			int leftArc = drawCircularBranch(g2d, left);
			int rightArc = drawCircularBranch(g2d, right);
			arc = (leftArc + rightArc) / 2;

			// System.out.println(leftArc + "\t" + rightArc + "\t" + arc);
			g2d.drawArc(centerX - radius, centerY - radius, radius * 2,
					radius * 2, leftArc, rightArc - leftArc);

		}

		node.addNodeData("arc", arc);
		double angle = Math.PI * 2 * arc / 360;
		int pointX = (int) (centerX + radius * Math.cos(angle));
		int pointY = (int) (centerY - radius * Math.sin(angle));

		node.addNodeData("pointX", pointX);
		node.addNodeData("pointY", pointY);

		int prePointX = (int) (centerX + preRadius * Math.cos(angle));
		int prepointY = (int) (centerY - preRadius * Math.sin(angle));

		if (node.isTerminal()) {
			String label = node.getLabel();
			double rAngle;
			int anchorX, anchorY;
			if (arc > 90 && arc < 270) {
				rAngle = Math.PI - angle;
				anchorX = (int) (centerX + (radius + label.length() * 7)
						* Math.cos(angle));
				anchorY = (int) (centerY - (radius + label.length() * 7)
						* Math.sin(angle));
			} else {
				rAngle = -angle;
				anchorX = pointX;
				anchorY = pointY;
			}

			AffineTransform at = new AffineTransform();
			at.setToRotation(rAngle, anchorX, anchorY);
			g2d.setTransform(at);
			g2d.drawString(node.getLabel(), anchorX, anchorY);
			at.setToRotation(0);
			g2d.setTransform(at);
		}

		g2d.drawLine(pointX, pointY, prePointX, prepointY);

		return arc;
	}

	/**
	 * draw circular cladogram tree
	 * 
	 * @param g2d
	 * @param node
	 * @return
	 */
	private int drawCircularCladoBranch(Graphics2D g2d, Node node) {
		int radius, preRadius, arc;
		radius = (Integer) node.getNodeData("radius");

		if (node.isRoot()) {
			preRadius = radius;
		} else {
			Node sister = node.getSister();
			int sisterRadius = (Integer) sister.getNodeData("radius");
			preRadius = radius < sisterRadius ? radius : sisterRadius;
			preRadius = preRadius - unitWith;
		}

		if (node.isTerminal()) {
			curTerm++;
			arc = termArc * (curTerm - 1);
			// System.out.println("termArc: " + arc + "\tcurTerm: " + curTerm);
		} else {
			Node left = node.getLeftChild();
			int leftHeight = TreeTools.nodeHeight(left);
			Node right = node.getRightChild();
			int rightHeight = TreeTools.nodeHeight(right);

			left.addNodeData("radius",
					+(int) (unitWith * (treeHeight - leftHeight)));
			right.addNodeData("radius",
					+(int) (unitWith * (treeHeight - rightHeight)));

			int leftArc = drawCircularCladoBranch(g2d, left);
			int rightArc = drawCircularCladoBranch(g2d, right);
			arc = (leftArc + rightArc) / 2;

			// System.out.println(leftArc + "\t" + rightArc + "\t" + arc);

			g2d.drawArc(centerX - radius, centerY - radius, radius * 2,
					radius * 2, leftArc, rightArc - leftArc);

		}

		node.addNodeData("arc", arc);
		double angle = Math.PI * 2 * arc / 360;
		int pointX = (int) (centerX + radius * Math.cos(angle));
		int pointY = (int) (centerY - radius * Math.sin(angle));

		node.addNodeData("pointX", pointX);
		node.addNodeData("pointY", pointY);

		int prePointX = (int) (centerX + preRadius * Math.cos(angle));
		int prepointY = (int) (centerY - preRadius * Math.sin(angle));

		// System.out.println(radius + "\t" + preRadius);

		if (node.isTerminal()) {
			String label = node.getLabel();
			double rAngle;
			int anchorX, anchorY;
			if (arc > 90 && arc < 270) {
				rAngle = Math.PI - angle;
				anchorX = (int) (centerX + (radius + label.length() * 7)
						* Math.cos(angle));
				anchorY = (int) (centerY - (radius + label.length() * 7)
						* Math.sin(angle));
			} else {
				rAngle = -angle;
				anchorX = pointX;
				anchorY = pointY;
			}

			AffineTransform at = new AffineTransform();
			at.setToRotation(rAngle, anchorX, anchorY);
			g2d.setTransform(at);
			g2d.drawString(node.getLabel(), anchorX, anchorY);
			at.setToRotation(0);
			g2d.setTransform(at);
		}

		g2d.drawLine(pointX, pointY, prePointX, prepointY);

		return arc;
	}

	/**
	 * Draw branch length or branch support
	 * 
	 * @param g2d
	 * @param data
	 *            <table>
	 *            <tr>
	 *            <td>"bl"</td>
	 *            <td>branch length</td>
	 *            </tr>
	 *            <tr>
	 *            <td>"bs"</td>
	 *            <td>branch support(Default)</td>
	 *            </tr>
	 *            </table>
	 * @param offset
	 *            0 ~ 1
	 */
	private void drawData(Graphics2D g2d, String data, double offsetX,
			double offsetY) {
		// check offset
		if (offsetX > 1) {
			offsetX = 1;
		} else if (offsetX < 0) {
			offsetX = 0;
		}

		if (offsetY > 1) {
			offsetY = 1;
		} else if (offsetY < -1) {
			offsetY = -1;
		}

		Set<Node> nodes = tree.getNodes();

		for (Node node : nodes) {
			int pointX = (Integer) node.getNodeData("pointX");
			int pointY = (Integer) node.getNodeData("pointY");
			double length = node.getBranchLength();
			String dataString = "";

			// data type
			if (data.equals("bl")) {
				DecimalFormat formater = new DecimalFormat("0.####");
				dataString = formater.format(length);
			} else if (data.equals("gl")) {
				if (gainLossType == GainLossType.mapping) {
					double allNum = (Double) node.getNodeData("geneNumber");

					double preNum = 0;

					if (!node.isRoot()) {
						preNum = (Double) node.getParent().getNodeData(
								"geneNumber");
					}

					double newNum = allNum - preNum;

					dataString = ("+" + newNum + "/" + allNum)
							.replace(".0", "");
				} else {
					double allNum = (Double) node.getNodeData("geneNumber");
					double estimateVar = (Double) node
							.getNodeData("estimateVar");
					double gain = (Double) node.getNodeData("gain");
					double gainVar = (Double) node.getNodeData("gainVar");
					double loss = (Double) node.getNodeData("loss");
					double lossVar = (Double) node.getNodeData("lossVar");
					dataString = ("+" + gain + "(±" + gainVar + ")/-" + loss
							+ "(±" + lossVar + ")/" + allNum + "(±"
							+ estimateVar + ")").replace(".0", "").replace(
							"(±0)", "");
				}
			} else {
				// branch support as default
				if (node.isTerminal()) {
					dataString = "";
				} else {
					dataString = node.getBranchSupport() + "";
				}
			}

			// offset length
			int offsetXLength = 0, offsetYLength = 0;
			if (!isCladogram && treeLength != 0) {
				offsetXLength = (int) (length * unitWith * offsetX);
			} else {
				int h = 0;
				if (!node.isRoot()) {
					int h1 = TreeTools.nodeHeight(node);
					int h2 = TreeTools.nodeHeight(node.getSister());
					h = h1 > h2 ? 0 : h2 - h1;
					offsetXLength = (int) ((h + 1) * unitWith * offsetX);
				}
			}

			// tree type
			if (!isCircular) {
				offsetYLength = (int) (termHeight * offsetY / 2);
				g2d.drawString(dataString, (int) (pointX - offsetXLength),
						pointY - offsetYLength);
			} else {
				int arc = (Integer) node.getNodeData("arc");
				int radius = (int) ((Integer) node.getNodeData("radius") - offsetXLength);
				double angle = Math.PI * 2 * arc / 360;
				double rAngle;
				int anchorX, anchorY;
				if (arc > 90 && arc < 270) {
					rAngle = Math.PI - angle;
					anchorX = (int) (centerX + (radius + dataString.length() * 7)
							* Math.cos(angle));
					anchorY = (int) (centerY - (radius + dataString.length() * 7)
							* Math.sin(angle));
				} else {
					rAngle = -angle;
					anchorX = (int) (centerX + radius * Math.cos(angle));
					anchorY = (int) (centerY - radius * Math.sin(angle));
				}
				AffineTransform at = new AffineTransform();
				at.setToRotation(rAngle, anchorX, anchorY);
				g2d.setTransform(at);
				g2d.drawString(dataString, anchorX, anchorY);
				at.setToRotation(0);
				g2d.setTransform(at);
			}
		}
	}

	/**
	 * draw Pie Chart
	 * 
	 * @param g2d
	 */
	private void drawPieChart(Graphics2D g2d) {

		AlphaComposite ac = java.awt.AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha);

		g2d.setComposite(ac);

		Set<Node> nodes = tree.getNodes();

		double maxDiam = 0;

		for (Node node : nodes) {
			Double all = (Double) node.getNodeData("geneNumber");
			double diam = Math.sqrt(all);
			maxDiam = maxDiam > diam ? maxDiam : diam;
		}

		double unitDiam = termHeight / maxDiam;

		for (Node node : nodes) {
			int pointX = (Integer) node.getNodeData("pointX");
			int pointY = (Integer) node.getNodeData("pointY");

			Double allNum = (Double) node.getNodeData("geneNumber");

			int diam = (int) (Math.sqrt(allNum) * unitDiam);

			double inherit = 0;
			double gain = 0;
			if (gainLossType == GainLossType.mapping) {
				if (!node.isRoot()) {
					inherit = (Double) node.getParent().getNodeData(
							"geneNumber");
				}

				gain = allNum - inherit;
			} else {
				inherit = (Double) node.getNodeData("inherit");
				gain = (Double) node.getNodeData("gain");
			}
			// System.out.println(newNum + "\t" + preNum);
			g2d.setColor(Color.green);
			g2d.fillArc(pointX - diam / 2, pointY - diam / 2, diam, diam, 0,
					360);
			g2d.setColor(Color.red);
			g2d.fillArc(pointX - diam / 2, pointY - diam / 2, diam, diam, 0,
					(int) (gain * 360 / allNum));
			g2d.setColor(Color.black);
			g2d.drawArc(pointX - diam / 2, pointY - diam / 2, diam, diam, 0,
					360);
		}

		// reset composite
		g2d.setComposite(AlphaComposite.SrcOver);
	}

	/**
	 * 
	 * @param g2d
	 */
	private void drawEditSquare(Graphics2D g2d) {
		Set<Node> nodes;
		if (mode == Mode.swap) {
			nodes = tree.getInnerNodes();
		} else {
			nodes = tree.getNodes();
		}

		for (Node node : nodes) {
			int pointX = (Integer) node.getNodeData("pointX");
			int pointY = (Integer) node.getNodeData("pointY");
			if (mode == Mode.reRoot
					&& (node.isRoot() || node.getParent().isRoot()))
				continue;
			g2d.fillRect(pointX - 3, pointY - 3, 6, 6);
		}
	}

	/**
	 * save tree image
	 * 
	 * @throws IOException
	 */
	public void saveImage(String imageFile) throws IOException {
		BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		this.paintComponent(g2);
		g2.dispose();
		ImageIO.write(bi, "png", new File(imageFile));
	}

	/**
	 * @return the tree
	 */
	public Tree getTree() {
		return tree;
	}

	/**
	 * @param tree
	 *            the tree to set
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		refreshTreeInfo();
	}

	/**
	 * @return the isCladogram
	 */
	public boolean isCladogram() {
		return isCladogram;
	}

	/**
	 * @param isCladogram
	 *            the isCladogram to set
	 */
	public void setCladogram(boolean isCladogram) {
		this.isCladogram = isCladogram;
	}

	/**
	 * @return the isCircular
	 */
	public boolean isCircular() {
		return isCircular;
	}

	/**
	 * @param isCircular
	 *            the isCircular to set
	 */
	public void setCircular(boolean isCircular) {
		this.isCircular = isCircular;
	}

	/**
	 * @return the isBranchLength
	 */
	public boolean isBranchLength() {
		return isBranchLength;
	}

	/**
	 * @param isBranchLength
	 *            the isBranchLength to set
	 */
	public void setBranchLength(boolean isBranchLength) {
		this.isBranchLength = isBranchLength;
	}

	/**
	 * @return the isBranchSupport
	 */
	public boolean isBranchSupport() {
		return isBranchSupport;
	}

	/**
	 * @param isBranchSupport
	 *            the isBranchSupport to set
	 */
	public void setBranchSupport(boolean isBranchSupport) {
		this.isBranchSupport = isBranchSupport;
	}

	/**
	 * @return the isGlPieChart
	 */
	public boolean isGlPieChart() {
		return isGlPieChart;
	}

	/**
	 * @return the isGainLoss
	 */
	public boolean isGainLoss() {
		return isGainLoss;
	}

	/**
	 * @param isGainLoss
	 *            the isGainLoss to set
	 */
	public void setGainLoss(boolean isGainLoss) {
		this.isGainLoss = isGainLoss;
	}

	/**
	 * @param isGlPieChart
	 *            the isGlPieChart to set
	 */
	public void setGlPieChart(boolean isGlPieChart) {
		this.isGlPieChart = isGlPieChart;
	}

	/**
	 * @return the isGlData
	 */
	public boolean isGlData() {
		return isGlData;
	}

	/**
	 * @param isGlData
	 *            the isGlData to set
	 */
	public void setGlData(boolean isGlData) {
		this.isGlData = isGlData;
	}

	/**
	 * @return the alpha
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha
	 *            the alpha to set
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * @return the geneContent
	 */
	public Map<String, BitArray> getGeneContent() {
		return geneContent;
	}

	/**
	 * @param geneContent
	 *            the geneContent to set
	 */
	public void setGeneContent(Map<String, BitArray> geneContent) {
		this.geneContent = geneContent;
	}

	/**
	 * @return the gainLossType
	 */
	public GainLossType getGainLossType() {
		return gainLossType;
	}

	/**
	 * @param gainLossType
	 *            the gainLossType to set
	 */
	public void setGainLossType(GainLossType gainLossType) {
		this.gainLossType = gainLossType;
	}

	/**
	 * mimic mouse action
	 * 
	 * @param point
	 */
	public void mouseAction(Point point) {
		if (mode == Mode.view)
			return;

		double x = point.getX();
		double y = point.getY();
		System.out.println(x + ", " + y);
		Set<Node> nodes;
		if (mode == Mode.swap) {
			nodes = tree.getInnerNodes();
		} else {
			nodes = tree.getNodes();
		}

		Node targetNode = null;
		for (Node node : nodes) {
			int pointX = (Integer) node.getNodeData("pointX");
			int pointY = (Integer) node.getNodeData("pointY");

			if (Math.abs(pointX - x) <= 3 && Math.abs(pointY - y) <= 3) {
				targetNode = node;
				break;
			}
		}

		if (targetNode != null) {
			if (mode == Mode.swap) {
				tree.swap(targetNode);
			} else if (mode == Mode.reRoot) {
				tree.reRoot(targetNode);
			}
		}
		
		refreshTreeInfo();
	}

	public static void main(String[] args) throws IOException {
		Tree tree = TreeTools.readNewick("/home/yeyanbo/tree").get(0);

		TreeFrame frame = new TreeFrame(tree);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
