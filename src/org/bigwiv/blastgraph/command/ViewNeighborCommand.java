/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;


import edu.uci.ics.jung.algorithms.filters.FilterUtils;

/**
 * Give a Vertex and a depth, return a subgraph consist all vertices in the
 * depth of that vertex in the large graph
 * 
 * @author yeyanbo
 * 
 */
public class ViewNeighborCommand extends Command {
	private HitVertex hitVertex;
	private int depth;
	private BlastGraph<HitVertex, ValueEdge> neighborGraph;

	/**
	 * @param hitVertex
	 * @param depth
	 */
	public ViewNeighborCommand(BlastGraph<HitVertex, ValueEdge> curGraph, HitVertex hitVertex, int depth) {
		this.neighborGraph = curGraph;
		this.hitVertex = hitVertex;
		this.depth = depth;
	}

	/**
	 * indicate a vertex by gi
	 * 
	 * @param gi
	 * @param depth
	 */
	public ViewNeighborCommand(BlastGraph<HitVertex, ValueEdge> curGraph, String gi, int depth) {
		this.neighborGraph = curGraph;
		this.hitVertex = Global.graph.getVertex(gi);
		this.depth = depth;
	}

	/**
	 * 
	 */
	public ViewNeighborCommand(BlastGraph<HitVertex, ValueEdge> curGraph) {
		this.neighborGraph = curGraph;
		hitVertex = null;
		depth = 0;
	}

	/**
	 * @return the hitVertex
	 */
	public HitVertex getHitVertex() {
		return hitVertex;
	}

	/**
	 * @param hitVertex the hitVertex to set
	 */
	public void setHitVertex(HitVertex hitVertex) {
		this.hitVertex = hitVertex;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	protected void concreteExecute() {
		Set<HitVertex> resultSet = new HashSet<HitVertex>();
		Set<HitVertex> visited = new HashSet<HitVertex>();
		Queue<HitVertex> toVisitQueue = new LinkedList<HitVertex>();

		int preCount = 0; // vertices count of previous depth
		int curCount = 0; // vertices count of current depth

		resultSet.add(hitVertex);
		toVisitQueue.add(hitVertex);

		preCount = 1;

		while (toVisitQueue.size() != 0 && depth != 0) {
			HitVertex chv = toVisitQueue.remove();
			visited.add(chv);

			for (HitVertex hv : Global.graph.getNeighbors(chv)) {
				if (!visited.contains(hv)) {
					resultSet.add(hv);
					toVisitQueue.add(hv);

					curCount++;
				}
			}

			preCount--;

			// previous level finished
			if (preCount == 0) {
				depth--;
				preCount = curCount;
				//System.out.println(preCount + " " + curCount);
				curCount = 0;
			}
		}

		neighborGraph.empty();
		neighborGraph.copy(FilterUtils.createInducedSubgraph(resultSet,
				Global.graph));
		//System.out.println(resultSet.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	protected void concreteUnExecute() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the neighborGraph
	 */
	public BlastGraph<HitVertex, ValueEdge> getNeighborGraph() {
		return neighborGraph;
	}

}
