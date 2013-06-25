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

package org.bigwiv.blastgraph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.bigwiv.blastgraph.global.Global;


import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * This class is to extract unrelated(or unconnected) subset of a large graph
 * 
 * @author yeyanbo
 * 
 * @param <V>
 * @param <E>
 */
public class SubSetCluster<V, E> implements
		Transformer<UndirectedGraph<V, E>, Set<Set<V>>> {

	protected Map<V, Number> isVisited;

	// @Override
	// public Set<Set<V>> transform(UndirectedGraph<V, E> theGraph) {
	// Set<Set<V>> subSets = new LinkedHashSet<Set<V>>();
	//
	// if (theGraph.getVertices().isEmpty())
	// return subSets;
	//
	// isVisited = new HashMap<V, Number>();
	// for (V v : theGraph.getVertices()) {
	// isVisited.put(v, 0);
	// }
	//
	// for (V v : theGraph.getVertices()) {
	// if (isVisited.get(v).intValue() == 0) {
	// Set<V> subSet = new HashSet<V>();
	// subSet.add(v);
	//
	// findAllConnectedVetices(theGraph, v, subSet);
	// subSets.add(subSet);
	// isVisited.put(v, 1);
	// }
	// }
	// return subSets;
	// }
	//
	// protected void findAllConnectedVetices(UndirectedGraph<V, E> g, V v,
	// Set<V> subSet) {
	// for (V w : g.getNeighbors(v)) {
	// if (isVisited.get(w).intValue() == 0) // w hasn't yet been visited
	// {
	// subSet.add(w);
	// isVisited.put(w, 1);
	// // System.out.println("Size:" + subSet.size());
	// findAllConnectedVetices(g, w, subSet);
	// }
	// }
	// }

	// @Override
	// public Set<Set<V>> transform(UndirectedGraph<V, E> theGraph) {
	// Set<Set<V>> subSets = new LinkedHashSet<Set<V>>();
	// if (theGraph.getVertices().isEmpty())
	// return subSets;
	//
	// for (V v : theGraph.getVertices()) {
	// Set<V> subSet = new HashSet<V>();
	// subSet.addAll(theGraph.getNeighbors(v));
	// subSets.add(subSet);
	//
	// Set<Set<V>> interSets = new LinkedHashSet<Set<V>>(); // store sets
	// intersect with subSet
	// for (Set<V> existSet : subSets) {
	// Set<V> tempSet = new HashSet<V>(existSet);
	// if (tempSet.retainAll(subSet)){
	// interSets.add(existSet);
	// }
	// }
	//
	// if(interSets.size() == 0){
	// subSets.add(subSet);
	// }else {
	// for(Set<V> interSet : interSets){
	// subSet.addAll(interSet);
	// subSets.remove(interSet);
	// }
	// subSets.add(subSet);
	// }
	// }
	// return subSets;
	// }

	@Override
	public Set<Set<V>> transform(UndirectedGraph<V, E> theGraph) {
		Set<Set<V>> subSets = new LinkedHashSet<Set<V>>();

		if (theGraph.getVertices().isEmpty())
			return subSets;

		isVisited = new HashMap<V, Number>();
		for (V v : theGraph.getVertices()) {
			isVisited.put(v, 0);
		}

		for (V v : theGraph.getVertices()) {
			if (isVisited.get(v).intValue() == 0) {
				Set<V> subSet = new HashSet<V>();
				subSet.add(v);

//				Stack<V> toVisitStack = new Stack<V>();//stack for DFS
//				toVisitStack.push(v);
//
//				while (toVisitStack.size() != 0) {
//					V curV = toVisitStack.pop();
//					isVisited.put(curV, 1);
//					for (V w : theGraph.getNeighbors(curV)) {
//						if (isVisited.get(w).intValue() == 0) // w hasn't yet
//																// been visited
//						{
//							subSet.add(w);
//							toVisitStack.push(w);
//						}
//					}
//
//				}
				
				Queue<V> toVisitQueue = new LinkedList<V>();//Queue for BFS
				toVisitQueue.add(v);
				
				while (toVisitQueue.size() != 0) {
					V curV = toVisitQueue.remove();
					isVisited.put(curV, 1);
					for (V w : theGraph.getNeighbors(curV)) {
						if (isVisited.get(w).intValue() == 0) // w hasn't yet
																// been visited
						{
							subSet.add(w);
							toVisitQueue.add(w);
						}
					}

				}

				subSets.add(subSet);
			}
		}
		return subSets;
	}

}
