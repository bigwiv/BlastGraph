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

/**
 * 
 */
package org.bigwiv.bio.phylogeny;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import edu.uci.ics.jung.algorithms.layout3d.SpringLayout.LengthFunction;

/**
 * IOTool methods for Tree reading and writing
 * 
 * @author yeyanbo
 * 
 */
public final class TreeTools {
	/**
	 * read newick tree file
	 * 
	 * @param treeFile
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Tree> readNewick(String treeFile)
			throws IOException {
		String newickString = "";
		BufferedReader br = new BufferedReader(new FileReader(treeFile));
		String line;

		while ((line = br.readLine()) != null) {
			newickString += line;
		}
		br.close();

		// replace blank characters
		newickString = newickString.replaceAll("\\s|\\n|\\r", "");

		// multiple trees
		String[] newickStrings = newickString.split(";");
		ArrayList<Tree> trees = new ArrayList<Tree>();

		for (int i = 0; i < newickStrings.length; i++) {
			Tree tree = new Tree(treeFile + i, createNode(newickStrings[i]));
			trees.add(tree);
		}

		return trees;
	}

	/**
	 * 
	 * @param newickString
	 * @return
	 */
	public static Node createNode(String newickString) {
		Node node = new Node("");
		if (newickString.contains(",")) {
			int index = newickString.lastIndexOf(")");
			// name or branch support, and branch length
			String value = newickString.substring(index + 1,
					newickString.length());
			// System.out.println(value);
			String[] values = value.split(":");

			if (values[0].equals("")) {
				node.setLabel("");
			} else {
				if (values[0].matches("[0-9]+")) {
					node.setBranchSupport(Integer.parseInt(values[0]));
				} else {
					node.setLabel(values[0]);
				}
			}

			if (values.length == 2) {
				node.setBranchLength(Double.parseDouble(values[1]));
			}

			String childString = newickString.substring(1, index);
			// System.out.println(childString);
			index = getSepIndex(childString);
			String leftChild = childString.substring(0, index);
			String rightChild = childString.substring(index + 1,
					childString.length());
			node.setLeftChild(createNode(leftChild));
			node.setRightChild(createNode(rightChild));
			// System.out.println(leftChild);
			// System.out.println(rightChild);
		} else {
			String[] values = newickString.split(":");
			if (values[0].equals("")) {
				node.setLabel("");
			} else {
				if (values[0].matches("[0-9]+")) {
					node.setBranchSupport(Integer.parseInt(values[0]));
				} else {
					node.setLabel(values[0]);
				}
			}

			if (values.length == 2) {
				node.setBranchLength(Double.parseDouble(values[1]));
			}
		}

		return node;
	}

	/**
	 * 
	 * @param childString
	 * @return
	 */
	private static int getSepIndex(String childString) {
		char[] child = childString.toCharArray();

		int depth = 0;
		for (int i = 0; i < child.length; i++) {
			if (child[i] == '(') {
				depth++;
			} else if (child[i] == ')') {
				depth--;
			}

			if (depth == 0 && child[i] == ',') {
				return i;
			}
		}

		return 0;
	}

	/**
	 * 
	 * @param tree
	 * @param treeFile
	 * @param branchSupport
	 * @throws IOException
	 */
	public static void writeNewick(Tree tree, String treeFile,
			boolean branchSupport) throws IOException {
		Node node = tree.getRootNode();
		BufferedWriter bw = new BufferedWriter(new FileWriter(treeFile));
		String treeString = createString(node, branchSupport) + ";";
		bw.write(treeString);
		bw.close();
	}
	
	
	/**
	 * 
	 * @param trees
	 * @param treeFile
	 * @param branchSupport
	 * @throws IOException
	 */
	public static void writeNewick(ArrayList<Tree> trees, String treeFile, boolean branchSupport) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(treeFile));
		for (Tree tree : trees) {
			Node node = tree.getRootNode();
			String treeString = createString(node, branchSupport) + ";\n";
			bw.write(treeString);
		}
		bw.close();
	}

	/**
	 * 
	 * @param node
	 * @param branchSupport
	 * @return
	 */
	private static String createString(Node node, boolean branchSupport) {
		String result;
		if (node.isTerminal()) {
			result = node.getLabel() + ":" + node.getBranchLength();
		} else {
			String ann;
			if (branchSupport) {
				ann = node.getBranchSupport() + "";
			} else {
				ann = node.getLabel();
			}

			result = "(" + createString(node.getLeftChild(), branchSupport)
					+ "," + createString(node.getRightChild(), branchSupport)
					+ ")" + ann + ":" + node.getBranchLength();
		}

		return result;
	}

	/**
	 * get the max sum of branch Length
	 * @param tree
	 * @return
	 */
	public static double treeLength(Tree tree){
		Node rootNode = tree.getRootNode();
		
		return nodeLength(rootNode);
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public static double nodeLength(Node node){
		double length = 0;
		if(node.isTerminal()){
			length = node.getBranchLength();
		}else {
			length = node.getBranchLength();
			double left = nodeLength(node.getLeftChild());
			double right = nodeLength(node.getRightChild());
			double longer = left > right ? left : right;
			length += longer;
		}
		
		return length;
	}
	
	/**
	 * tree height
	 * @param tree
	 * @return
	 */
	public static int treeHeight(Tree tree){
		return nodeHeight(tree.getRootNode());
	}
	
	/**
	 * node height
	 * @param node
	 * @return
	 */
	public static int nodeHeight(Node node) {
		int max = 0;
		Stack<Node> nodeStack = new Stack<Node>();
		nodeStack.push(node);
		node.addNodeData("nodeHeight", 1);
		Node curNode;
		while(!nodeStack.isEmpty()){
			curNode = nodeStack.pop();
			int height = (Integer) curNode.getNodeData("nodeHeight");
			
			if(!curNode.isTerminal()){
				curNode.getLeftChild().addNodeData("nodeHeight", height + 1);
				curNode.getRightChild().addNodeData("nodeHeight", height + 1);
				nodeStack.push(curNode.getLeftChild());
				nodeStack.push(curNode.getRightChild());
			}else {
				max = max > height ? max : height;
			}
		}
		
		return max;
	}
	
	public static void main(String[] args) {
		try {
			ArrayList<Tree> trees = readNewick("/home/yeyanbo/baculovirus_new/outtree");
			for (Tree tree : trees) {
				Set<Node> nodes = tree.getNodes();

				for (Node node : nodes) {
					System.out.println(node.getLabel() + "\t"
							+ node.getBranchLength() + "\t"
							+ node.getBranchSupport());
				}
				
				writeNewick(tree, "/home/yeyanbo/baculovirus_new/testresult.tree", false);
				
				System.out.println(treeLength(tree));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
