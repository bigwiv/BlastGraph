package org.bigwiv.bio.phylogeny;

import java.util.HashMap;

/**
 * tree Node
 * 
 * @author yeyanbo
 * 
 */
public class Node {
	private String label;
	private Node parent;
	private Node leftChild;
	private Node rightChild;
	private double branchLength;
	private int branchSupport;

	// Other information
	private HashMap<String, Object> nodeData;

	/**
	 * 
	 * @param label
	 */
	public Node(String label) {
		this.label = label;
		this.setParent(null);
		this.setLeftChild(null);
		this.setRightChild(null);
		this.setBranchLength(0);
		this.setBranchSupport(0);
	}

	/**
	 * remove parent node
	 */
	public void removeParent() {
		this.setParent(null);
	}

	/**
	 * remove child node
	 * 
	 */
	public void removeChild() {
		this.getLeftChild().setParent(null);
		this.getRightChild().setParent(null);
		this.setLeftChild(null);
		this.setRightChild(null);
	}

	/**
	 * is terminal node
	 * 
	 * @return
	 */
	public boolean isTerminal() {
		if (leftChild != null && rightChild != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * is root node
	 * @return
	 */
	public boolean isRoot() {
		if (parent != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the branchLength
	 */
	public double getBranchLength() {
		return branchLength;
	}

	/**
	 * @param branchLength
	 *            the branchLength to set
	 */
	public void setBranchLength(double branchLength) {
		this.branchLength = branchLength;
	}

	/**
	 * @return the branchSupport
	 */
	public int getBranchSupport() {
		return branchSupport;
	}

	/**
	 * @param branchSupport
	 *            the branchSupport to set
	 */
	public void setBranchSupport(int branchSupport) {
		this.branchSupport = branchSupport;
	}
	
	/**
	 * get sister node of current node
	 * @return
	 */
	public Node getSister(){
		Node sister;
		if(this.isRoot()){
			sister = null;
		}else {
			Node left = this.getParent().getLeftChild();
			Node right = this.getParent().getRightChild();
			
			sister = this.equals(left) ? right : left;
		}
		
		return sister;
	}

	/**
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * private for safety
	 * @param parent
	 *            the parent to set
	 */
	private void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * @return the leftChild or null if terminal
	 */
	public Node getLeftChild() {
		return leftChild;
	}

	/**
	 * @param leftChild
	 *            the leftChild to set
	 */
	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
		if(leftChild != null){
			leftChild.setParent(this);
		}
	}

	/**
	 * @return the rightChild or null if terminal
	 */
	public Node getRightChild() {
		return rightChild;
	}

	/**
	 * @param rightChild
	 *            the rightChild to set
	 */
	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;

		if(rightChild != null){
			rightChild.setParent(this);
		}
	}

	/**
	 * get all the data in HashMap
	 * 
	 * @return the nodeData
	 */
	public HashMap<String, Object> getAllNodeData() {
		return nodeData;
	}

	/**
	 * get the node data by the given key
	 * 
	 * @param key
	 * @return
	 */
	public Object getNodeData(String key) {
		
		if(this.nodeData == null){
			return null;
		}
		
		return nodeData.get(key);
	}

	/**
	 * add node data or set the value if key exist
	 * 
	 * @param key
	 * @param value
	 */
	public void addNodeData(String key, Object value) {

		if (this.nodeData == null) {
			this.nodeData = new HashMap<String, Object>();
		}

		this.nodeData.put(key, value);
	}
	
	/**
	 * remove node data by key
	 * @param key
	 */
	public void removeNodeData(String key){
		
		if(this.nodeData == null){
			return;
		}
		
		this.nodeData.remove(key);
	}
	
	/**
	 * check if node data contains key
	 * @param key
	 * @return
	 */
	public boolean containsNodeData(String key){
		return this.nodeData.containsKey(key);
	}
}
