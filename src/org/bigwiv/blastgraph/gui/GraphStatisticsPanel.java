/**
 * 
 */
package org.bigwiv.blastgraph.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.math.plot.Plot2DPanel;


/**
 * @author yeyanbo
 * 
 */
public class GraphStatisticsPanel extends JPanel implements ActionListener {
	BlastGraph<HitVertex, ValueEdge> graph;

	JLabel valueLabel, methodLabel;
	JComboBox valueComboBox, methodComboBox;
	// StatisticsPanel statisticsPanel;
	Plot2DPanel plot2dPanel;
	String[] items = { "Vertex Length", "Edge LogEvalue", "Edge Coverage",
			"Edge Coverage2", "Edge ScoreDensity", "Edge PercentageIdentity",
			"Edge PercentagePositive" };

	String[] additional = { "VertexCount To LogEvalue",
			"VertexCount To Coverage", "VertexCount To Coverage2" };

	String[] methods = { "LinePlot", "Histogram" };
	double vertexCount[] = null;
	double values[] = null;

	// BasicStatisticsTool bsTool;

	public GraphStatisticsPanel(BlastGraph<HitVertex, ValueEdge> graph) {
		super();
		this.graph = graph;
		this.plot2dPanel = new Plot2DPanel();
		// this.bsTool = new BasicStatisticsTool();
		initComponents();
	}

	private void initComponents() {
		this.setLayout(new GridBagLayout());

		GridBagManager.reset();
		GridBagManager.GRID_BAG.fill = GridBagConstraints.HORIZONTAL;
		GridBagManager.GRID_BAG.anchor = GridBagConstraints.FIRST_LINE_START;
		GridBagManager.GRID_BAG.weightx = 0;
		GridBagManager.GRID_BAG.weighty = 0;
		GridBagManager.GRID_BAG.insets = new Insets(2, 2, 1, 1);

		valueLabel = new JLabel("Value: ");
		methodLabel = new JLabel("Plot: ");

		valueComboBox = new JComboBox(items);
		// this.addAdditional();
		valueComboBox.addActionListener(this);
		methodComboBox = new JComboBox(methods);
		methodComboBox.addActionListener(this);

		if (graph != null) {
			Vector vector = new Vector();
			Collection<HitVertex> hvs = graph.getVertices();
			for (HitVertex hitVertex : hvs) {
				vector.add((double) hitVertex.getLength());
			}

			double[] array = new double[vector.size()];
			for (int i = 0; i < vector.size(); i++) {
				array[i] = (Double) vector.get(i);
			}

			// Image image = null;
			// try {
			// image = bsTool.getHistogram(array);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// statisticsPanel.setImage(image);

			Arrays.sort(array);
			plot2dPanel.addLinePlot("Array", array);
		}

		// JButton testButton = new JButton("Test");
		GridBagManager.add(this, valueLabel, 0, 0, 1, 1);
		GridBagManager.add(this, methodLabel, 0, 1, 1, 1);
		GridBagManager.GRID_BAG.weightx = 1;
		GridBagManager.add(this, valueComboBox, 1, 0, 1, 1);
		GridBagManager.add(this, methodComboBox, 1, 1, 1, 1);
		GridBagManager.GRID_BAG.weighty = 1;
		GridBagManager.GRID_BAG.fill = GridBagConstraints.BOTH;
		// GridBagManager.add(pane, statisticsPanel, 0, 1, 4, 4);
		GridBagManager.add(this, plot2dPanel, 0, 2, 2, 2);
		// GridBagManager.add(pane, testButton, 0, 1, 4, 4);
	}

	// private void addAdditional() {
	// for (int i = 0; i < additional.length; i++) {
	// valueComboBox.addItem(additional[i]);
	// }
	// }

	// private void removeAdditional() {
	// for (int i = 0; i < additional.length; i++) {
	// valueComboBox.removeItem(additional[i]);
	// }
	// }

	// @Override
	// protected void processWindowEvent(WindowEvent e) {
	// bsTool.close();
	// super.processWindowEvent(e);
	// }

	@Override
	public void actionPerformed(ActionEvent e) {
		int index = valueComboBox.getSelectedIndex();
		int index2 = methodComboBox.getSelectedIndex();

		if (!plot2dPanel.getPlots().isEmpty()) {
			plot2dPanel.getPlots().clear();
		}
		
		// System.out.println("changed: " + index + " " + index2);
		Vector vector = new Vector();
		if (index == 0) {
			Collection<HitVertex> hvs = graph.getVertices();
			for (HitVertex hitVertex : hvs) {
				vector.add((double) hitVertex.getLength());
			}
		} else if (index == 1) {
			Collection<ValueEdge> ves = graph.getEdges();
			for (ValueEdge ve : ves) {
				if (ve.getExpectValue() == 0) {
					vector.add(-200.0);
				} else {
					vector.add(Math.log10(ve.getExpectValue()));
				}
			}
		} else if (index == 2) {
			Collection<ValueEdge> ves = graph.getEdges();
			for (ValueEdge ve : ves) {
				vector.add(graph.getCoverage(ve));
			}
		} else if (index == 3) {
			Collection<ValueEdge> ves = graph.getEdges();
			for (ValueEdge ve : ves) {
				vector.add(graph.getCoverage2(ve));
			}
		} else if (index == 4) {
			Collection<ValueEdge> ves = graph.getEdges();
			for (ValueEdge ve : ves) {
				vector.add(graph.getScoreDensity(ve));
			}
		} else if (index == 5) {
			Collection<ValueEdge> ves = graph.getEdges();
			for (ValueEdge ve : ves) {
				vector.add(graph.getPercentageIdentity(ve));
			}
		} else if (index == 6) {
			Collection<ValueEdge> ves = graph.getEdges();
			for (ValueEdge ve : ves) {
				vector.add(graph.getPercentagePositive(ve));
			}
		} else if (index == 7) {
			final BlastGraph<HitVertex, ValueEdge> tmpGraph = new BlastGraph<HitVertex, ValueEdge>();
			tmpGraph.copy(graph);
			ArrayList<ValueEdge> ves = new ArrayList<ValueEdge>(
					tmpGraph.getEdges());
			vertexCount = new double[ves.size()];
			values = new double[ves.size()];
			Collections.sort(ves, new Comparator<ValueEdge>() {
				@Override
				public int compare(ValueEdge arg0, ValueEdge arg1) {
					// from biggest to smallest
					if (arg0.getExpectValue() < arg1.getExpectValue()) {
						return 1;
					} else if (arg0.getExpectValue() > arg1.getExpectValue()) {
						return -1;
					} else {
						return 0;
					}
				}
			});

			for (int i = 0; i < ves.size(); i++) {
				ValueEdge ve = ves.get(i);
				if (ve.getExpectValue() == 0) {
					values[i] = -200.0;
				} else {
					values[i] = Math.log10(ve.getExpectValue());
				}
				// values[i] = ve.getExpectValue();
				tmpGraph.removeEdge(ve);
				tmpGraph.generateSubSet();
				vertexCount[i] = tmpGraph.getSubSet().get(0).size();
				// System.out.println(values[i] + ": " + vertexCount[i]);
			}

		} else if (index == 8) {
			final BlastGraph<HitVertex, ValueEdge> tmpGraph = new BlastGraph<HitVertex, ValueEdge>();
			tmpGraph.copy(graph);
			ArrayList<ValueEdge> ves = new ArrayList<ValueEdge>(
					tmpGraph.getEdges());
			vertexCount = new double[ves.size()];
			values = new double[ves.size()];
			Collections.sort(ves, new Comparator<ValueEdge>() {
				@Override
				public int compare(ValueEdge arg0, ValueEdge arg1) {
					// from smallest to biggest
					if (tmpGraph.getCoverage(arg0) > tmpGraph.getCoverage(arg1)) {
						return 1;
					} else if (tmpGraph.getCoverage(arg0) < tmpGraph
							.getCoverage(arg1)) {
						return -1;
					} else {
						return 0;
					}
				}
			});

			for (int i = 0; i < ves.size(); i++) {
				ValueEdge ve = ves.get(i);
				values[i] = tmpGraph.getCoverage(ve);
				tmpGraph.removeEdge(ve);
				tmpGraph.generateSubSet();
				vertexCount[i] = tmpGraph.getSubSet().get(0).size();
				// System.out.println(values[i] + ": " + vertexCount[i]);
			}
		} else {
			final BlastGraph<HitVertex, ValueEdge> tmpGraph = new BlastGraph<HitVertex, ValueEdge>();
			tmpGraph.copy(graph);
			ArrayList<ValueEdge> ves = new ArrayList<ValueEdge>(
					tmpGraph.getEdges());
			vertexCount = new double[ves.size()];
			values = new double[ves.size()];
			Collections.sort(ves, new Comparator<ValueEdge>() {
				@Override
				public int compare(ValueEdge arg0, ValueEdge arg1) {
					// from smallest to biggest
					if (tmpGraph.getCoverage2(arg0) > tmpGraph
							.getCoverage2(arg1)) {
						return 1;
					} else if (tmpGraph.getCoverage2(arg0) < tmpGraph
							.getCoverage2(arg1)) {
						return -1;
					} else {
						return 0;
					}
				}
			});

			for (int i = 0; i < ves.size(); i++) {
				ValueEdge ve = ves.get(i);
				values[i] = tmpGraph.getCoverage2(ve);
				tmpGraph.removeEdge(ve);
				tmpGraph.generateSubSet();
				vertexCount[i] = tmpGraph.getSubSet().get(0).size();
				// System.out.println(values[i] + ": " + vertexCount[i]);
			}
		}

		double[] array = new double[vector.size()];
		for (int i = 0; i < vector.size(); i++) {
			array[i] = (Double) vector.get(i);
		}

		Arrays.sort(array);
		double[] arrayIndex = new double[array.length];
		for (int i = 0; i < arrayIndex.length; i++) {
			arrayIndex[i] = i + 1;
		}

		// System.out.println(array[1]);
		if (index2 == 0) {
			// if (valueComboBox.getItemCount() == items.length) {
			// addAdditional();
			// }
			if (index < items.length) {
				plot2dPanel.addLinePlot("Array", array, arrayIndex);
			} else {
				// System.out.println(values.length + " " + vertexCount.length);
				plot2dPanel.addLinePlot("vertexCount", values, vertexCount);
			}
			plot2dPanel.repaint();
		} else if (index2 == 1) {
			// try {
			// Image image = bsTool.getHistogram(array);
			// statisticsPanel.setImage(image);
			// statisticsPanel.repaint();
			// this.pack();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// if (valueComboBox.getItemCount() > items.length) {
			// removeAdditional();
			// }
			plot2dPanel.addHistogramPlot("Array", array, 20);
			plot2dPanel.repaint();
		}
	}

	/**
	 * @return the graph
	 */
	public BlastGraph<HitVertex, ValueEdge> getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 *            the graph to set
	 */
	public void setGraph(BlastGraph<HitVertex, ValueEdge> graph) {
		this.graph = graph;
		
		if (graph != null) {
			this.setEnabled(true);
			int index = valueComboBox.getSelectedIndex();
			valueComboBox.setSelectedIndex(index);
		}else {
			this.setEnabled(false);
		}
		
		//this.revalidate();
		//this.repaint();
	}
	
	@Override
	public void setEnabled(boolean enabled){
		valueComboBox.setEnabled(enabled);
		methodComboBox.setEnabled(enabled);
		plot2dPanel.setEnabled(enabled);
	}
}
