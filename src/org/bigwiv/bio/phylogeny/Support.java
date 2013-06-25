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
