package org.bigwiv.bio.phylogeny;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Tree with Node and tree information
 * 
 * @author yeyanbo
 * 
 */
public class Tree {
	private String treeName;
	private Node rootNode;
	private List<String> termLabels;
	private Set<Node> innerNodes;// for efficient node access
	private Set<Node> termNodes;// for efficient node access

	public Tree(String treeName, Node rootNode) {
		this.setTreeName(treeName);
		this.setRootNode(rootNode);

		this.termLabels = new ArrayList<String>();
		this.innerNodes = new LinkedHashSet<Node>();
		this.termNodes = new LinkedHashSet<Node>();

		Stack<Node> nodeStack = new Stack<Node>();
		nodeStack.push(rootNode);

		Node curNode;
		int innerNum = 0;
		while (!nodeStack.isEmpty()) {
			curNode = nodeStack.pop();

			if (!curNode.isTerminal()) {
				innerNum++;
				this.innerNodes.add(curNode);
				if (curNode.getLabel().equals("")) {
					curNode.setLabel("Inner" + innerNum);
				}
				nodeStack.push(curNode.getLeftChild());
				nodeStack.push(curNode.getRightChild());
			} else {
				termNodes.add(curNode);
				termLabels.add(curNode.getLabel());
			}
		}
	}

	public Node getNode(String label) {

		for (Node node : termNodes) {
			if (node.getLabel().equals(label)) {
				return node;
			}
		}

		for (Node node : innerNodes) {
			if (node.getLabel().equals(label)) {
				return node;
			}
		}

		return null;
	}

	/**
	 * @return the termNum
	 */
	public int getTermNum() {
		return termNodes.size();
	}

	/**
	 * @return the innerNum
	 */
	public int getInnerNum() {
		return innerNodes.size();
	}

	/**
	 * 
	 * @return termNodes
	 */
	public Set<Node> getTermNodes() {
		return termNodes;
	}

	/**
	 * 
	 * @return innerNodes
	 */
	public Set<Node> getInnerNodes() {
		return innerNodes;
	}

	/**
	 * @return the treeName
	 */
	public String getTreeName() {
		return treeName;
	}

	/**
	 * @param treeName
	 *            the treeName to set
	 */
	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	/**
	 * @return the rootNode
	 */
	public Node getRootNode() {
		return rootNode;
	}

	/**
	 * @param rootNode
	 *            the rootNode to set
	 */
	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * 
	 * @param innerNode
	 */
	public void swap(Node innerNode) {
		if (!innerNode.isTerminal()) {
			Node left = innerNode.getLeftChild();
			Node right = innerNode.getRightChild();

			innerNode.setLeftChild(right);
			innerNode.setRightChild(left);
		}
	}

	/**
	 * 
	 * @param outGroup
	 */
	public void reRoot(Node outGroup) {
		if (outGroup.isRoot() || outGroup.getParent().isRoot())
			return;

		Node topLeaf1 = outGroup;
		Node topLeaf2 = outGroup.getParent();

		double pbl = topLeaf2.getBranchLength();
		double obl = topLeaf1.getBranchLength();

		// set new root
		topLeaf1.setBranchLength(obl / 2);
		topLeaf2.setBranchLength(obl / 2);
		if (topLeaf2.getLeftChild().equals(topLeaf1)) {
			topLeaf2.setLeftChild(null);
		} else {
			topLeaf2.setRightChild(null);
		}

		Node parent = topLeaf2;
		Node pp = parent.getParent();
		while (!parent.isRoot()) {
			// parent of parent
			Node ppp = pp.getParent();

			if (!pp.isRoot()) {
				double tempbl = pp.getBranchLength();
				pp.setBranchLength(pbl);
				pbl = tempbl;
				if (parent.getLeftChild() == null) {
					parent.setLeftChild(pp);
				} else {
					parent.setRightChild(pp);
				}

				if (pp.getLeftChild().equals(parent)) {
					pp.setLeftChild(null);
				} else {
					pp.setRightChild(null);
				}
			} else {
				// recalculate the branch length of previous outgroup
				Node childNode;
				if (pp.getLeftChild().equals(parent)) {
					childNode = pp.getRightChild();
				} else {
					childNode = pp.getLeftChild();
				}

				if (parent.getLeftChild() == null) {
					parent.setLeftChild(childNode);
				} else {
					parent.setRightChild(childNode);
				}

				double cbl = childNode.getBranchLength()
						+ parent.getBranchLength();

				childNode.setBranchLength(cbl);

				pp.setLeftChild(topLeaf1);
				pp.setRightChild(topLeaf2);
			}

			parent = pp;
			pp = ppp;
		}

		// assign tempRoot childs to root
	}

	/**
	 * @return the nodeList
	 */
	public Set<Node> getNodes() {
		Set<Node> nodes = new LinkedHashSet<Node>();
		nodes.addAll(innerNodes);
		nodes.addAll(termNodes);
		return nodes;
	}

	public List<String> getTermLabels() {
		return termLabels;
	}
}
