/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.Size2DSyntax;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.biojavax.bio.seq.PositionResolver.MaximalResolver;


import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Given a large graph, for each subGraph, remove weak(represent by evalue or
 * coverage etc.) edges until the vertex number is close(represent by a maxCut,
 * vertexNum/genomeNum less than 1.5 or 1.1, etc.) to a given number(genomeNum)
 * or until there is no edges weaker than the cutoff(an evalue or coverage).
 * 
 * @author yeyanbo
 * 
 */
public class GenomeNumFiltCommand extends Command {
	// filtBy type
	public static final int EVALUE = 0;
	public static final int COVERAGE = 1;
	public static final int COVERAGE2= 2;
	public static final int SCORE_DENSITY = 3;
	public static final int PERCENTAGE_IDENTITIES = 4;
	public static final int PERCENTAGE_POSITIVES = 5;

	private int filtBy;
	
	private Map<ValueEdge, Pair<HitVertex>> removed;
	private int genomNum; // Num of Genomes in this graph
	private double maxCut; // max multiple to genomeNum a subgraph can have
	private double cutoff; // weak cutoff, in this cutoff, the edge will not be
							// removed even the subgraph size > maxCut*genomeNum

	public GenomeNumFiltCommand() {
		this.commandName = "GenomeNumFilt";
		this.isUndoable = true;
		removed = new HashMap<ValueEdge, Pair<HitVertex>> ();
	}

	public GenomeNumFiltCommand(int genomNum, double maxCut, int filtBy, double cutoff) {
		this();
		this.genomNum = genomNum;
		this.maxCut = maxCut;
		this.filtBy = filtBy;
		this.cutoff = cutoff;
	}

	@Override
	public void concreteExecute() {
		
		if (filtBy == EVALUE) {
			filtByEvalue();
		} else if (filtBy == COVERAGE) {
			filtByCoverage();
		} else if (filtBy == COVERAGE2) {
			filtByCoverage2();
		}else if (filtBy == SCORE_DENSITY) {

		} else if (filtBy == PERCENTAGE_IDENTITIES) {

		} else {

		}
		
	}

	private void filtByEvalue() {
		boolean greaterExist = true;
		boolean noWeak = false;
		while (greaterExist) {
			greaterExist = false;
			noWeak = true;
			Global.graph.generateSubSet();
			ArrayList<Set<HitVertex>> hvSets = Global.graph.getSubSet();
			for (Set<HitVertex> set : hvSets) {
				int size = set.size();
				// System.out.println("size: " + size);
				if (size > genomNum * maxCut) {
					Global.WORK_STATUS.setMessage("Filtering Size: " + size);
					BlastGraph<HitVertex, ValueEdge> tmpGraph = FilterUtils
							.createInducedSubgraph(set, Global.graph);

					ArrayList<ValueEdge> edges = new ArrayList<ValueEdge>(
							tmpGraph.getEdges());
					Collections.sort(edges, new Comparator<ValueEdge>() {
						@Override
						public int compare(ValueEdge arg0, ValueEdge arg1) {
							// from biggest to smallest
							if (arg0.getExpectValue() < arg1.getExpectValue()) {
								return 1;
							} else if (arg0.getExpectValue() > arg1
									.getExpectValue()) {
								return -1;
							} else {
								return 0;
							}
						}
					});

					// if the weakest edge is in that cutoff, break
					if (edges.get(0).getExpectValue() < cutoff) {
						break;
					} else {
						noWeak = false;
					}

					// if the tmpGraph size changed, break;
					int maxsize = 0;
					for (int i = 0; i < edges.size(); i++) {
						ValueEdge ve = edges.get(i);
						removed.put(ve, Global.graph.getEndpoints(ve));
						tmpGraph.removeEdge(ve);
						Global.graph.removeEdge(ve);
						tmpGraph.generateSubSet();
						maxsize = tmpGraph.getSubSet().get(0).size();
						if (maxsize != size)
							break;
					}
					if (maxsize > genomNum * maxCut)
						greaterExist = true;
				}
			}
		}
	}

	private void filtByCoverage() {
		boolean greaterExist = true;
		boolean noWeak = false;
		while (greaterExist && !noWeak) {
			greaterExist = false;
			noWeak = true;
			Global.graph.generateSubSet();
			ArrayList<Set<HitVertex>> hvSets = Global.graph.getSubSet();
			for (Set<HitVertex> set : hvSets) {
				int size = set.size();
				// System.out.println("size: " + size);
				if (size > genomNum * maxCut) {
					Global.WORK_STATUS.setMessage("Filtering Size: " + size);
					BlastGraph<HitVertex, ValueEdge> tmpGraph = FilterUtils
							.createInducedSubgraph(set, Global.graph);

					ArrayList<ValueEdge> edges = new ArrayList<ValueEdge>(
							tmpGraph.getEdges());
					Collections.sort(edges, new Comparator<ValueEdge>() {
						@Override
						public int compare(ValueEdge arg0, ValueEdge arg1) {
							double coverage0 = Global.graph.getCoverage(arg0);
							double coverage1 = Global.graph.getCoverage(arg1);
							// from smallest to biggest
							if (coverage0 > coverage1) {
								return 1;
							} else if (coverage0 < coverage1) {
								return -1;
							} else {
								return 0;
							}
						}
					});

					// if the weakest edge is in that cutoff, break
					if (Global.graph.getCoverage(edges.get(0)) > cutoff) {
						Global.WORK_STATUS.setMessage("No weak edge, value: " + Global.graph.getCoverage(edges.get(0)));
						break;
					} else {
						noWeak = false;
					}
					
					int maxsize = 0;
					for (int i = 0; i < edges.size(); i++) {
						// System.out.println(graph.getCoverage(edges.get(i)));
						ValueEdge ve = edges.get(i);
						removed.put(ve, Global.graph.getEndpoints(ve));
						tmpGraph.removeEdge(ve);
						Global.graph.removeEdge(ve);
						tmpGraph.generateSubSet();
						maxsize = tmpGraph.getSubSet().get(0).size();
						if (maxsize != size)
							break;
					}
					
					if (maxsize > genomNum * maxCut)
						greaterExist = true;
				}
			}
		}
	}
	
	private void filtByCoverage2() {
		boolean greaterExist = true;
		boolean noWeak = false;
		while (greaterExist && !noWeak) {
			greaterExist = false;
			noWeak = true;
			Global.graph.generateSubSet();
			ArrayList<Set<HitVertex>> hvSets = Global.graph.getSubSet();
			for (Set<HitVertex> set : hvSets) {
				int size = set.size();
				// System.out.println("size: " + size);
				if (size > genomNum * maxCut) {
					Global.WORK_STATUS.setMessage("Filtering Size: " + size);
					BlastGraph<HitVertex, ValueEdge> tmpGraph = FilterUtils
							.createInducedSubgraph(set, Global.graph);

					ArrayList<ValueEdge> edges = new ArrayList<ValueEdge>(
							tmpGraph.getEdges());
					Collections.sort(edges, new Comparator<ValueEdge>() {
						@Override
						public int compare(ValueEdge arg0, ValueEdge arg1) {
							double coverage0 = Global.graph.getCoverage2(arg0);
							double coverage1 = Global.graph.getCoverage2(arg1);
							// from smallest to biggest
							if (coverage0 > coverage1) {
								return 1;
							} else if (coverage0 < coverage1) {
								return -1;
							} else {
								return 0;
							}
						}
					});

					// if the weakest edge is in that cutoff, break
					if (Global.graph.getCoverage2(edges.get(0)) > cutoff) {
						Global.WORK_STATUS.setMessage("No weak edge, value: " + Global.graph.getCoverage2(edges.get(0)));
						break;
					} else {
						noWeak = false;
					}
					
					int maxsize = 0;
					for (int i = 0; i < edges.size(); i++) {
						// System.out.println(Global.graph.getCoverage2(edges.get(i)));
						ValueEdge ve = edges.get(i);
						removed.put(ve, Global.graph.getEndpoints(ve));
						tmpGraph.removeEdge(ve);
						Global.graph.removeEdge(ve);
						tmpGraph.generateSubSet();
						maxsize = tmpGraph.getSubSet().get(0).size();
						if (maxsize != size)
							break;
					}
					
					if (maxsize > genomNum * maxCut)
						greaterExist = true;
				}
			}
		}
	}

	@Override
	public void concreteUnExecute() {
		
		for (ValueEdge ve : removed.keySet()) {
			Global.graph.addEdge(ve, removed.get(ve));
		}
		
	}

	/**
	 * @return the filtBy
	 */
	public int getFiltBy() {
		return filtBy;
	}

	/**
	 * @param filtBy the filtBy to set
	 */
	public void setFiltBy(int filtBy) {
		this.filtBy = filtBy;
	}

	/**
	 * @return the genomNum
	 */
	public int getGenomNum() {
		return genomNum;
	}

	/**
	 * @param genomNum the genomNum to set
	 */
	public void setGenomNum(int genomNum) {
		this.genomNum = genomNum;
	}

	/**
	 * @return the maxCut
	 */
	public double getMaxCut() {
		return maxCut;
	}

	/**
	 * @param maxCut the maxCut to set
	 */
	public void setMaxCut(double maxCut) {
		this.maxCut = maxCut;
	}

	/**
	 * @return the cutoff
	 */
	public double getCutoff() {
		return cutoff;
	}

	/**
	 * @param cutoff the cutoff to set
	 */
	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}
}
