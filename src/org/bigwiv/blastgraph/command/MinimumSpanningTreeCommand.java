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

package org.bigwiv.blastgraph.command;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;


import edu.uci.ics.jung.graph.util.Pair;

/**
 * 
 * @author yeyanbo
 * 
 */
public class MinimumSpanningTreeCommand extends Command {
	
	private BlastGraph<HitVertex, ValueEdge> curGraph;
	private BlastGraph<HitVertex, ValueEdge> preGraph;

	enum State {
		White, Gray, Black
	}

	public MinimumSpanningTreeCommand(BlastGraph<HitVertex, ValueEdge> curGraph) {
		this.isUndoable = true;
		this.commandName = "MinimumSapanningTree";
		this.curGraph = curGraph;

	}

	@Override
	public void concreteExecute() {

		BlastGraph<HitVertex, ValueEdge> newGraph = new BlastGraph<HitVertex, ValueEdge>();
		
		Comparator comparator = new Comparator<ValueEdge>() {
			@Override
			public int compare(ValueEdge arg0, ValueEdge arg1) {
				// from strong to weak
				double evalue = arg0.getExpectValue() - arg1.getExpectValue();
				if (evalue > 0) {
					return 1;
				} else if (evalue < 0) {
					return -1;
				} else {
					double c1 = Global.graph.getCoverage(arg0)
							- Global.graph.getCoverage(arg1);
					if (c1 < 0) {
						return 1;
					} else if (c1 > 0) {
						return -1;
					} else {
						double c2 = Global.graph.getCoverage2(arg0)
								- Global.graph.getCoverage2(arg1);
						if (c2 < 0) {
							return 1;
						} else if (c2 > 0) {
							return -1;
						} else {
							return 0;
						}
					}
				}
			}
		};
		
		//Set<HitVertex> done = new HashSet<HitVertex>();
		
		Collection<HitVertex> vertices = curGraph.getVertices();

		Map<ValueEdge, HitVertex> edges = new HashMap<ValueEdge, HitVertex>();
		
		int size = vertices.size();
		HitVertex first = vertices.iterator().next();
		newGraph.addVertex(first);
		
		while (newGraph.getVertexCount() != size) {
			//System.out.println(newGraph.getVertexCount());
			for (HitVertex hv : newGraph.getVertices()) {
				Collection<ValueEdge> ves = curGraph.getIncidentEdges(hv);
				for (ValueEdge ve : ves) {
					HitVertex hv1 = curGraph.getOpposite(hv, ve);
					if(!newGraph.containsVertex(hv1)){
						//System.out.println(hv.getAccession() + "\t" + hv1.getAccession());
						edges.put(ve, hv1);
					}
				}
			}

			HitVertex hvMin = null; 
			ValueEdge veMin = null;
			for (ValueEdge ve : edges.keySet()) {
				
				if(hvMin == null || comparator.compare(ve, veMin) == -1){
					veMin = ve;
					hvMin = edges.get(ve);
				}
			}
			
			edges.clear();
			
			HitVertex hvMinOps = curGraph.getOpposite(hvMin, veMin);
			newGraph.addVertex(hvMin);
			//System.out.println(hvMin.getAccession());
			newGraph.addEdge(veMin, hvMin, hvMinOps);
		}
		
		//save current graph, for undo purpose
		preGraph = new BlastGraph<HitVertex, ValueEdge>();
		preGraph.copy(curGraph);
		
		curGraph.empty();
		curGraph.copy(newGraph);
		newGraph = null;
	}

	private boolean isInCycle(ValueEdge valueEdge,
			BlastGraph<HitVertex, ValueEdge> graph) {
		Map<HitVertex, State> vertexStates = new HashMap<HitVertex, State>();

		for (HitVertex hv : graph.getVertices()) {
			vertexStates.put(hv, State.White);
		}

		Pair<HitVertex> pair = graph.getEndpoints(valueEdge);

		HitVertex hv1 = pair.getFirst();
		HitVertex hv2 = pair.getSecond();

		Queue<HitVertex> toVisitQueue = new LinkedList<HitVertex>();

		// Queue for BFS
		toVisitQueue.add(hv2);

		vertexStates.put(hv1, State.Gray);// make hv1 black, so it cannot be put
											// into toVisiteQueue;
		vertexStates.put(hv2, State.Gray);

		Set<HitVertex> neighbors;
		HitVertex curV;
		while (toVisitQueue.size() != 0) {
			// System.out.println(toVisitQueue.size());
			curV = toVisitQueue.remove();
			neighbors = new HashSet<HitVertex>(graph.getNeighbors(curV));
			for (HitVertex nv : neighbors) {

				if (vertexStates.get(nv) == State.White) {
					vertexStates.put(nv, State.Gray);
					// System.out.println(vertexStates.size());
					toVisitQueue.add(nv);
					// System.out.println(nv.getAccession());
				} else if (vertexStates.get(nv) == State.Gray && nv.equals(hv1)) {
					vertexStates.put(nv, State.Black);
				} else if (vertexStates.get(nv) == State.Black
						&& nv.equals(hv1)) {
					return true;
				}
			}
			vertexStates.put(curV, State.Black);
		}

		return false;
	}

	@Override
	public void concreteUnExecute() {
		curGraph.empty();
		curGraph.copy(preGraph);
		preGraph = null;
	}

}
