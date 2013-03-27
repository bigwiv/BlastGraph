package org.bigwiv.bio.phylogeny;


final class Support {
	/**
	 * number of trees with this clade.
	 */
	private int numTrees;

	/**
	 * Sum of branch length of this clade
	 */
	private double sumBranches;

	Support() {
		sumBranches = 0.0;
		numTrees = 0;
	}

	public void add(double branch) {
		sumBranches += branch;
		++numTrees;
	}

	/**
	 * @return the numTrees
	 */
	public int getNumTrees() {
		return numTrees;
	}

	/**
	 * @return the sumBranches
	 */
	public double getSumBranches() {
		return sumBranches;
	}
}
