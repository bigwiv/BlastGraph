/**
 * 
 */
package org.bigwiv.bio.phylogeny;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javax.print.attribute.standard.Sides;

import org.biojavax.bio.seq.PositionResolver.MaximalResolver;
import org.jgrapht.experimental.permutation.IntegerPermutationIter;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xpath.internal.axes.NodeSequence;
import com.sun.org.apache.xpath.internal.operations.And;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Union;
import com.sun.xml.internal.ws.message.RootElementSniffer;

/**
 * @author yeyanbo
 * 
 */
public class PhyloAlgorithms {

	public static Tree getGeneContentCluster(Map<String, BitArray> geneContent) {

		ArrayList<Node> nodeList = new ArrayList<Node>();
		Node node;

		// init terminal nodes
		for (String label : geneContent.keySet()) {
			node = new Node(label);
			BitArray genes = geneContent.get(label);
			node.addNodeData("genes", genes);
			node.addNodeData("geneNumber", (double) genes.cardinality());
			nodeList.add(node);
		}

		while (nodeList.size() != 1) {
			int size = nodeList.size();
			BitArray maxIntersection = null;
			double maxScore = 0;
			int m = 0, n = 0;

			for (int i = 0; i < size; i++) {
				BitArray iGenes = (BitArray) nodeList.get(i).getNodeData(
						"genes");
				for (int j = i + 1; j < size; j++) {
					BitArray jGenes = (BitArray) nodeList.get(j).getNodeData(
							"genes");
					BitArray inter = BitArray.Tools.intersect(iGenes, jGenes);

					int score = inter.cardinality();
					// store the maxScore
					if (score > maxScore) {
						maxIntersection = inter;
						maxScore = score;
						m = i;
						n = j;
					}
				}
			}

			Node node1 = nodeList.get(m);
			Node node2 = nodeList.get(n);

			Node newNode = new Node("");
			newNode.addNodeData("genes", maxIntersection);
			newNode.addNodeData("geneNumber", maxScore);
			newNode.setLeftChild(nodeList.get(m));
			newNode.setRightChild(nodeList.get(n));

			if (m > n) {
				nodeList.remove(m);
				nodeList.remove(n);
			} else {
				nodeList.remove(n);
				nodeList.remove(m);
			}

			nodeList.add(newNode);
		}

		return new Tree("Gene Content Cluster", nodeList.get(0));
	}

	/**
	 * mapping gene content to the given tree
	 * 
	 * @param tree
	 * @param geneContent
	 */
	public static void geneTreeMapping(Tree tree,
			Map<String, BitArray> geneContent) {
		Set<Node> nodes = tree.getTermNodes();

		// init terminal nodes
		for (Node node : nodes) {
			String label = node.getLabel();
			BitArray genes = geneContent.get(label);
			node.addNodeData("genes", genes);
			node.addNodeData("geneNumber", (double) genes.cardinality());
		}

		Node rootNode = tree.getRootNode();

		setSharedGenes(rootNode);
	}

	/**
	 * Given the root, set shared genes data to all nodes
	 * 
	 * @param node
	 * @return
	 */
	private static BitArray setSharedGenes(Node node) {
		if (node.isTerminal()) {
			return (BitArray) node.getNodeData("genes");
		} else {
			Node left = node.getLeftChild();
			Node right = node.getRightChild();

			BitArray inter = BitArray.Tools.intersect(setSharedGenes(left),
					setSharedGenes(right));

			node.addNodeData("genes", inter);
			node.addNodeData("geneNumber", (double) inter.cardinality());

			return inter;
		}
	}

	/**
	 * estimate gene content of internal node using maximum parsimony(Fitch
	 * algorithm)
	 * 
	 * @param tree
	 * @param geneContent
	 */
	public static void geneTreeParsimony(Tree tree,
			Map<String, BitArray> geneContent) {
		Node rootNode = tree.getRootNode();
		Set<Node> termNodes = tree.getTermNodes();

		Set<Node> innerNodes = tree.getInnerNodes();

		// gene family length
		int gf = 0;
		// init terminal nodes
		for (Node node : termNodes) {
			String label = node.getLabel();
			BitArray genes = geneContent.get(label);
			node.addNodeData("genes", genes);
			gf = genes.size();
			int[] state = new int[gf];
			for (int i = 0; i < state.length; i++) {
				state[i] = genes.get(i);
			}
			node.addNodeData("state", state);
			node.addNodeData("geneNumber", (double) genes.cardinality());
			node.addNodeData("estimate", 0.0);
			node.addNodeData("estimateVar", 0.0);
			node.addNodeData("gain", 0.0);
			node.addNodeData("gainVar", 0.0);
			node.addNodeData("loss", 0.0);
			node.addNodeData("lossVar", 0.0);
			node.addNodeData("inherit", 0.0);
			node.addNodeData("inheritVar", 0.0);
		}

		// add state attribute for innerNodes
		for (Node node : innerNodes) {
			node.addNodeData("state", new int[gf]);
			// there should be four types of data
			// gain + inherit = estimate
			// pre - loss = inherit
			node.addNodeData("geneNumber", 0.0);
			node.addNodeData("estimate", 0.0);
			node.addNodeData("estimateVar", 0.0);
			node.addNodeData("gain", 0.0);
			node.addNodeData("gainVar", 0.0);
			node.addNodeData("loss", 0.0);
			node.addNodeData("lossVar", 0.0);
			node.addNodeData("inherit", 0.0);
			node.addNodeData("inheritVar", 0.0);
		}

		// set state for each position
		for (int p = 0; p < gf; p++) {
			setGeneState(rootNode, p);
		}

		// find best state with minimum change and estimate gain loss event
		// summing up the gene number, gain and loss event
		for (int p = 0; p < gf; p++) {
			Stack<Node> nodeStack = new Stack<Node>();
			nodeStack.push(rootNode);
			Node curNode;
			while (!nodeStack.isEmpty()) {
				curNode = nodeStack.pop();
				if (!curNode.isTerminal()) {
					Node leftNode = curNode.getLeftChild();
					Node rightNode = curNode.getRightChild();
					nodeStack.push(leftNode);
					nodeStack.push(rightNode);

				}

				int[] curState = (int[]) curNode.getNodeData("state");
				double estimate = (Double) curNode.getNodeData("estimate");
				double inherit = (Double) curNode.getNodeData("inherit");
				double gain = (Double) curNode.getNodeData("gain");
				double loss = (Double) curNode.getNodeData("loss");
				double estimateVar = (Double) curNode
						.getNodeData("estimateVar");
				double inheritVar = (Double) curNode.getNodeData("inheritVar");
				double gainVar = (Double) curNode.getNodeData("gainVar");
				double lossVar = (Double) curNode.getNodeData("lossVar");

				if (curNode.isRoot()) {
					if (curState[p] == 1) {
						curNode.addNodeData("gain", gain + 1);
						curNode.addNodeData("estimate", estimate = estimate + 1);
					} else if (curState[p] == -1) {
						curNode.addNodeData("gain", gain + 0.5);
						curNode.addNodeData("gainVar", gainVar + 0.5);
						curNode.addNodeData("estimate",
								estimate = estimate + 0.5);
						curNode.addNodeData("estimateVar", estimateVar + 0.5);
					}
				} else {

					Node parentNode = curNode.getParent();
					int[] parentState = (int[]) parentNode.getNodeData("state");

					// confirm child based on the parent state to make sure
					// the number of changes is minimum
					if (curState[p] == -1 && parentState[p] != -1) {
						curState[p] = parentState[p];
					}

					if (parentState[p] == 1) {
						if (curState[p] == 0) {
							curNode.addNodeData("loss", loss + 1);
						} else if (curState[p] == 1) {
							curNode.addNodeData("inherit", inherit + 1);
							curNode.addNodeData("estimate",
									estimate = estimate + 1);
						}
					} else if (parentState[p] == 0) {
						if (curState[p] == 1) {
							curNode.addNodeData("gain", gain + 1);
							curNode.addNodeData("estimate",
									estimate = estimate + 1);
						}
					} else if (parentState[p] == -1) {
						if (curState[p] == 1) {
							curNode.addNodeData("gain", gain + 0.5);
							curNode.addNodeData("gainVar", gainVar + 0.5);
							curNode.addNodeData("inherit", inherit + 0.5);
							curNode.addNodeData("inheritVar", inheritVar + 0.5);
							curNode.addNodeData("estimate",
									estimate = estimate + 1);
						} else if (curState[p] == 0) {
							curNode.addNodeData("loss", loss + 0.5);
							curNode.addNodeData("lossVar", lossVar + 0.5);
						} else if (curState[p] == -1) {
							curNode.addNodeData("loss", loss + 0.25);
							curNode.addNodeData("lossVar", lossVar + 0.25);
							curNode.addNodeData("gain", gain + 0.25);
							curNode.addNodeData("gainVar", gainVar + 0.25);
							curNode.addNodeData("inherit", inherit + 0.25);
							curNode.addNodeData("inheritVar", inheritVar + 0.25);
							curNode.addNodeData("estimate",
									estimate = estimate + 0.25);
							curNode.addNodeData("estimateVar",
									estimateVar + 0.25);
						}
					}

				}

				if (!curNode.isTerminal()) {
					curNode.addNodeData("geneNumber", estimate);
				}
			}
		}
	}

	/**
	 * set the gene state: 1 for presence; 0 for absence; -1 for both(01).
	 * 
	 * @param node
	 * @param p
	 * @return
	 */
	private static int setGeneState(Node node, int p) {
		if (node.isTerminal()) {
			return ((int[]) node.getNodeData("state"))[p];
		} else {
			Node left = node.getLeftChild();
			Node right = node.getRightChild();
			int leftState = setGeneState(left, p);
			int rightState = setGeneState(right, p);
			int state = 0;

			if ((leftState == -1 && rightState == -1)
					|| (leftState == 1 && rightState == 0)
					|| (leftState == 0 && rightState == 1)) {

				state = -1;
			} else {

				if (Math.abs(leftState * rightState) == 1) {
					state = 1;
				} else {
					state = 0;

				}
			}

			((int[]) node.getNodeData("state"))[p] = state;

			return state;
		}
	}

	/**
	 * estimate gene content of internal node using Dollo Parsimony( gene loss
	 * is easier than parallel gene gain, event the number of loss events is
	 * greater than gain events)
	 * 
	 * @param tree
	 * @param geneContent
	 */

	public static void geneTreeDolloParsimony(Tree tree,
			Map<String, BitArray> geneContent) {
		Node rootNode = tree.getRootNode();
		Set<Node> termNodes = tree.getTermNodes();

		Set<Node> innerNodes = tree.getInnerNodes();

		// gene family length
		int gf = 0;
		// init terminal nodes
		for (Node node : termNodes) {
			String label = node.getLabel();
			BitArray genes = geneContent.get(label);
			node.addNodeData("genes", genes);
			gf = genes.size();
			int[] state = new int[gf];
			for (int i = 0; i < state.length; i++) {
				state[i] = genes.get(i);
			}
			node.addNodeData("state", state);
			node.addNodeData("geneNumber", (double) genes.cardinality());
			node.addNodeData("estimate", 0.0);
			node.addNodeData("estimateVar", 0.0);
			node.addNodeData("gain", 0.0);
			node.addNodeData("gainVar", 0.0);
			node.addNodeData("loss", 0.0);
			node.addNodeData("lossVar", 0.0);
			node.addNodeData("inherit", 0.0);
			node.addNodeData("inheritVar", 0.0);
		}

		// add state attribute for innerNodes
		for (Node node : innerNodes) {
			node.addNodeData("state", new int[gf]);
			// there should be four types of data
			// gain + inherit = estimate
			// pre - loss = inherit
			node.addNodeData("geneNumber", 0.0);
			node.addNodeData("estimate", 0.0);
			node.addNodeData("estimateVar", 0.0);
			node.addNodeData("gain", 0.0);
			node.addNodeData("gainVar", 0.0);
			node.addNodeData("loss", 0.0);
			node.addNodeData("lossVar", 0.0);
			node.addNodeData("inherit", 0.0);
			node.addNodeData("inheritVar", 0.0);
		}

		// set state for each position
		for (int p = 0; p < gf; p++) {
			setDolloGeneState(rootNode, p);
		}

		// find best state using Dollo Parsimony and estimate gain loss event
		// summing up the gene number, gain and loss event
		for (int p = 0; p < gf; p++) {
			Stack<Node> nodeStack = new Stack<Node>();
			nodeStack.push(rootNode);
			Node curNode;
			while (!nodeStack.isEmpty()) {
				curNode = nodeStack.pop();
				if (!curNode.isTerminal()) {
					Node leftNode = curNode.getLeftChild();
					Node rightNode = curNode.getRightChild();
					nodeStack.push(leftNode);
					nodeStack.push(rightNode);

				}

				int[] curState = (int[]) curNode.getNodeData("state");
				double estimate = (Double) curNode.getNodeData("estimate");
				double inherit = (Double) curNode.getNodeData("inherit");
				double gain = (Double) curNode.getNodeData("gain");
				double loss = (Double) curNode.getNodeData("loss");
				double estimateVar = (Double) curNode
						.getNodeData("estimateVar");
				double inheritVar = (Double) curNode.getNodeData("inheritVar");
				double gainVar = (Double) curNode.getNodeData("gainVar");
				double lossVar = (Double) curNode.getNodeData("lossVar");


				if (curNode.isRoot()) {
					int[] leftState = (int[])curNode.getLeftChild().getNodeData("state");
					int[] rightState = (int[])curNode.getRightChild().getNodeData("state");

					//solve previously unestimated states for -1 based on children
					//actually to distinguish (-1,0) and (1,0) children pairs
					if(curState[p] == -1 && (leftState[p] == -1 || rightState[p] == -1)){
						curState[p] = 0;
					}
					
					//state change
					if (curState[p] == 1) {
						curNode.addNodeData("gain", gain + 1);
						curNode.addNodeData("estimate", estimate = estimate + 1);
					} else if (curState[p] == -1) {
						curNode.addNodeData("gain", gain + 0.5);
						curNode.addNodeData("gainVar", gainVar + 0.5);
						curNode.addNodeData("estimate",
								estimate = estimate + 0.5);
						curNode.addNodeData("estimateVar", estimateVar + 0.5);
					}
				} else {

					Node parentNode = curNode.getParent();
					int[] parentState = (int[]) parentNode.getNodeData("state");

					// confirm child based on the parent state to make sure
					// the number of changes is minimum
					if (curState[p] == -1 && parentState[p] != -1) {
						curState[p] = parentState[p];
					}

					if (parentState[p] == 1) {
						if (curState[p] == 0) {
							curNode.addNodeData("loss", loss + 1);
						} else if (curState[p] == 1) {
							curNode.addNodeData("inherit", inherit + 1);
							curNode.addNodeData("estimate",
									estimate = estimate + 1);
						}
					} else if (parentState[p] == 0) {
						if (curState[p] == 1) {
							curNode.addNodeData("gain", gain + 1);
							curNode.addNodeData("estimate",
									estimate = estimate + 1);
						}
					} else if (parentState[p] == -1) {
						if (curState[p] == 1) {
							curNode.addNodeData("gain", gain + 0.5);
							curNode.addNodeData("gainVar", gainVar + 0.5);
							curNode.addNodeData("inherit", inherit + 0.5);
							curNode.addNodeData("inheritVar", inheritVar + 0.5);
							curNode.addNodeData("estimate",
									estimate = estimate + 1);
						} else if (curState[p] == 0) {
							curNode.addNodeData("loss", loss + 0.5);
							curNode.addNodeData("lossVar", lossVar + 0.5);
						} else if (curState[p] == -1) {
							curNode.addNodeData("loss", loss + 0.25);
							curNode.addNodeData("lossVar", lossVar + 0.25);
							curNode.addNodeData("gain", gain + 0.25);
							curNode.addNodeData("gainVar", gainVar + 0.25);
							curNode.addNodeData("inherit", inherit + 0.25);
							curNode.addNodeData("inheritVar", inheritVar + 0.25);
							curNode.addNodeData("estimate",
									estimate = estimate + 0.25);
							curNode.addNodeData("estimateVar",
									estimateVar + 0.25);
						}
					}

				}

				if (!curNode.isTerminal()) {
					curNode.addNodeData("geneNumber", estimate);
				}
			}
		}
	}
	
	/**
	 * set the gene state: 1 for presence; 0 for absence; -1 for both(01).
	 * 
	 * @param node
	 * @param p
	 * @return
	 */
	private static int setDolloGeneState(Node node, int p) {
		if (node.isTerminal()) {
			return ((int[]) node.getNodeData("state"))[p];
		} else {
			Node left = node.getLeftChild();
			Node right = node.getRightChild();
			int leftState = setDolloGeneState(left, p);
			int rightState = setDolloGeneState(right, p);
			int state = 0;

			if (Math.abs(leftState * rightState) == 1) {
				state = 1;
			} else if ((Math.abs(leftState) == 1 && rightState == 0)
					|| (leftState == 0 && Math.abs(rightState) == 1)) {

				state = -1;
			} else {
				state = 0;
			}

			((int[]) node.getNodeData("state"))[p] = state;

			return state;
		}
	}

	/**
	 * 
	 * @param matrix
	 * @param number
	 * @return
	 */
	public static ArrayList<Tree> reSample(BitArray[] matrix,
			List<String> labels, int time, DistanceType distanceType,
			TreeType treeType, ReSamplingType rsType) {
		ArrayList<Tree> trees = new ArrayList<Tree>();
		for (int i = 0; i < time; i++) {
			BitArray[] sample = BitArray.Tools.reSample(matrix, rsType);
			DistanceMatrix dm = getDistanceMatrix(sample, labels, distanceType);
			if (treeType == TreeType.NJ) {
				trees.add(nj(dm));
			} else if (treeType == TreeType.UPGMA) {
				trees.add(upgma(dm));
			}
		}

		return trees;
	}

	public static DistanceMatrix getDistanceMatrix(BitArray[] matrix,
			List<String> labels, DistanceType distanceType) {
		DistanceMatrix distanceMatrix = new DistanceMatrix(labels);
		for (int i = 0; i < matrix.length; i++) {
			// System.out.println(labels.get(i) + "\t" + matrix[i].length);
			for (int j = i + 1; j < matrix.length; j++) {
				distanceMatrix.set(i, j, BitArray.Tools.distance(matrix[i],
						matrix[j], distanceType));
			}
		}

		return distanceMatrix;
	}

	public static Tree upgma(DistanceMatrix dm) {

		int size = dm.size();
		double[][] matrix = dm.getMatrix().clone();
		ArrayList<String> labels = new ArrayList<String>(dm.getLabels());

		// init terminal nodes
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < size; i++) {
			nodes.add(new Node(labels.get(i)));
		}

		Node node1, node2, inner = null;

		int minI = 0, minJ = 0;
		int innerCount = 0;
		while (size > 1) {
			// find minimum distance
			double minDist = matrix[0][1];
			minI = 0;
			minJ = 1;
			for (int i = 0; i < size; i++) {
				for (int j = i + 1; j < size; j++) {
					if (minDist >= matrix[i][j]) {
						minDist = matrix[i][j];
						minI = i;
						minJ = j;
					}
				}
			}

			// create subtree
			node1 = nodes.get(minI);
			node2 = nodes.get(minJ);
			innerCount++;
			inner = new Node("inner" + innerCount);
			inner.setLeftChild(node1);
			inner.setRightChild(node2);

			// update node list
			nodes.set(minI, inner);
			nodes.remove(minJ);

			// set branch length
			if (node1.isTerminal()) {
				node1.setBranchLength(minDist / 2);
			} else {
				node1.setBranchLength(minDist / 2 - TreeTools.nodeLength(node1));
			}

			if (node2.isTerminal()) {
				node2.setBranchLength(minDist / 2);
			} else {
				node2.setBranchLength(minDist / 2 - TreeTools.nodeLength(node2));
			}

			// rebuild distance matrix
			// set the distances of new node at the index of minI
			for (int k = 0; k < size; k++) {
				if (k != minI && k != minJ) {
					matrix[k][minI] = (matrix[minI][k] + matrix[minJ][k]) / 2;
					matrix[minI][k] = matrix[k][minI];
				}
			}

			// remove column minJ
			// move row greater than minJ up
			for (int k = minJ; k < size - 1; k++) {
				for (int l = 0; l < size; l++) {
					matrix[k][l] = matrix[k + 1][l];
				}
			}

			// move column greater than minJ left
			for (int k = minJ; k < size - 1; k++) {
				for (int l = 0; l < size; l++) {
					matrix[l][k] = matrix[l][k + 1];
				}
			}

			size--;
		}

		return new Tree("upgma", inner);
	}

	public static Tree nj(DistanceMatrix dm) {

		int size = dm.size();
		double[][] matrix = dm.getMatrix().clone();
		double[] nodeDist = new double[size];
		ArrayList<String> labels = new ArrayList<String>(dm.getLabels());

		// init terminal nodes
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < size; i++) {
			nodes.add(new Node(labels.get(i)));
		}

		Node node1, node2, inner = null;

		int minI = 0, minJ = 0;
		int innerCount = 0;

		while (size > 2) {
			// calculate nodeDist
			for (int i = 0; i < size; i++) {
				nodeDist[i] = 0;
				for (int j = 0; j < size; j++) {
					nodeDist[i] += matrix[i][j];
				}
				nodeDist[i] = nodeDist[i] / (size - 2);
			}

			// System.out.println();
			// find minimum distance pair
			double minDist = matrix[0][1] - nodeDist[0] - nodeDist[1];
			minI = 0;
			minJ = 1;
			double temp;
			for (int i = 0; i < size; i++) {
				for (int j = i + 1; j < size; j++) {
					temp = matrix[i][j] - nodeDist[i] - nodeDist[j];
					if (minDist > temp) {
						minDist = temp;
						minI = i;
						minJ = j;
					}
				}
			}

			// create subtree
			node1 = nodes.get(minI);
			node2 = nodes.get(minJ);
			innerCount++;
			inner = new Node("inner" + innerCount);
			inner.setLeftChild(node1);
			inner.setRightChild(node2);

			// update node list
			nodes.set(minI, inner);
			nodes.remove(minJ);

			// set branch length
			double br1 = (matrix[minI][minJ] + nodeDist[minI] - nodeDist[minJ]) / 2;

			node1.setBranchLength(br1);
			node2.setBranchLength(matrix[minI][minJ] - br1);
			// rebuild distance matrix
			// set the distances of new node at the index of minI
			for (int k = 0; k < size; k++) {
				if (k != minI && k != minJ) {
					matrix[k][minI] = (matrix[minI][k] + matrix[minJ][k] - matrix[minI][minJ]) / 2;
					matrix[minI][k] = matrix[k][minI];
				}
			}

			// remove column minJ
			// move row greater than minJ up
			for (int k = minJ; k < size - 1; k++) {
				for (int l = 0; l < size; l++) {
					matrix[k][l] = matrix[k + 1][l];
				}
			}
			// move column greater than minJ left
			for (int k = minJ; k < size - 1; k++) {
				for (int l = 0; l < size; l++) {
					matrix[l][k] = matrix[l][k + 1];
				}
			}

			size--;
		}

		innerCount++;
		Node rootNode = new Node("inner" + innerCount);
		nodes.get(0).setBranchLength(matrix[0][1] / 2);
		nodes.get(1).setBranchLength(matrix[0][1] / 2);
		rootNode.setLeftChild(nodes.get(0));
		rootNode.setRightChild(nodes.get(1));

		return new Tree("nj", rootNode);
	}

	/**
	 * Greedy consensus tree like PHYLIP. Majority Rule(extension).
	 * 
	 * @param trees
	 * @return
	 */
	public static Tree consensusTree(ArrayList<Tree> trees) {

		Map<BitArray, Support> bitSupport = new LinkedHashMap<BitArray, Support>();

		int termNum = trees.get(0).getTermNum();
		List<String> termLabels = trees.get(0).getTermLabels();

		double[] sumOfTermBranchLength = new double[termNum];

		// search clade support
		for (Tree tree : trees) {
			Node rootNode = tree.getRootNode();
			scanBit(rootNode, bitSupport, termLabels, sumOfTermBranchLength);
		}

		// sort clade support from large to small
		final Comparator<Map.Entry<BitArray, Support>> comparator = new Comparator<Map.Entry<BitArray, Support>>() {
			public int compare(Map.Entry<BitArray, Support> o1,
					Map.Entry<BitArray, Support> o2) {
				return o2.getValue().getNumTrees()
						- o1.getValue().getNumTrees();
			}
		};

		PriorityQueue<Map.Entry<BitArray, Support>> queue = new PriorityQueue<Map.Entry<BitArray, Support>>(
				bitSupport.size(), comparator);

		for (Map.Entry<BitArray, Support> s : bitSupport.entrySet()) {
			queue.add(s);
		}

		// create consense tree
		Node rootNode = null;
		Node[] nodes = new Node[termNum];
		for (int i = 0; i < termLabels.size(); i++) {
			nodes[i] = new Node(termLabels.get(i));
			nodes[i].setBranchLength(sumOfTermBranchLength[i] / trees.size());
		}

		Map<BitArray, Node> innerNodes = new LinkedHashMap<BitArray, Node>();

		while (queue.peek() != null) {

			Map.Entry<BitArray, Support> e = queue.poll();

			final Support s = e.getValue();
			final BitArray splitBit = e.getKey();

			final int branchesupport = (100 * s.getNumTrees()) / trees.size();
			final double branchLength = s.getSumBranches() / s.getNumTrees();

			Node node = new Node("");
			node.setBranchSupport(branchesupport);
			node.setBranchLength(branchLength);

			if (innerNodes.isEmpty()) {
				innerNodes.put(splitBit, node);
			} else {
				// check compatibility
				boolean compatible = true;
				for (BitArray bit : innerNodes.keySet()) {
					if (!BitArray.Tools.compatible(splitBit, bit)) {
						compatible = false;
						break;
					}
				}

				if (compatible) {
					innerNodes.put(splitBit, node);
				}
			}

			if (innerNodes.size() == (termNum - 1))
				break;
		}

		// sort clade support from small to large
		final Comparator<Map.Entry<BitArray, Node>> comparator2 = new Comparator<Map.Entry<BitArray, Node>>() {
			public int compare(Map.Entry<BitArray, Node> o1,
					Map.Entry<BitArray, Node> o2) {
				return o1.getKey().cardinality() - o2.getKey().cardinality();
			}
		};

		ArrayList<Map.Entry<BitArray, Node>> mapList = new ArrayList<Map.Entry<BitArray, Node>>();

		for (Map.Entry<BitArray, Node> s : innerNodes.entrySet()) {
			mapList.add(s);
		}

		Collections.sort(mapList, comparator2);

		for (int i = 0; i < mapList.size(); i++) {
			Map.Entry<BitArray, Node> e = mapList.get(i);
			Node node = e.getValue();
			BitArray splitBit = e.getKey();

			// System.out.println(node.getBranchSupport() + ", " + splitBit);

			if (splitBit.cardinality() == 2) {
				int pos[] = splitBit.positions();
				node.setLeftChild(nodes[pos[0]]);
				node.setRightChild(nodes[pos[1]]);
			}

			for (int j = i + 1; j < mapList.size(); j++) {
				Map.Entry<BitArray, Node> entry = mapList.get(j);
				Node nodeParent = entry.getValue();
				BitArray splitBitParent = entry.getKey();

				BitArray intersect = BitArray.Tools.intersect(splitBit,
						splitBitParent);

				if (intersect.equals(splitBit)) {
					// System.out.println(splitBit.toString());
					if (nodeParent.getLeftChild() == null) {
						nodeParent.setLeftChild(node);

						if (splitBitParent.cardinality()
								- splitBit.cardinality() == 1) {
							int[] p = BitArray.Tools.diff(splitBit,
									splitBitParent).positions();
							nodeParent.setRightChild(nodes[p[0]]);
						}

					} else {
						nodeParent.setRightChild(node);
					}

					break;
				}
			}

			if (splitBit.cardinality() == termNum) {
				rootNode = node;
			}
		}

		return new Tree("Consensus Tree", rootNode);
	}

	/**
	 * return bit for the given node and scan bits and supports for all nodes
	 * 
	 * @param node
	 * @param bitSupport
	 * @param termLabels
	 * @param sumOfTermBranchLength
	 * @return
	 */
	private static BitArray scanBit(Node node,
			Map<BitArray, Support> bitSupport, List<String> termLabels,
			double[] sumOfTermBranchLength) {
		if (node.isTerminal()) {
			BitArray bit = new BitArray(termLabels.size());
			int position = termLabels.indexOf(node.getLabel());
			bit.set(position);
			sumOfTermBranchLength[position] += node.getBranchLength();
			return bit;
		} else {
			Node leftNode = node.getLeftChild();
			Node rightNode = node.getRightChild();
			BitArray unionBit = BitArray.Tools.union(
					scanBit(leftNode, bitSupport, termLabels,
							sumOfTermBranchLength),
					scanBit(rightNode, bitSupport, termLabels,
							sumOfTermBranchLength));

			double branchLength = node.getBranchLength();

			Support s = bitSupport.get(unionBit);

			if (s == null) {
				s = new Support();
				bitSupport.put(unionBit, s);
			}

			s.add(branchLength);

			return unionBit;
		}
	}
}
