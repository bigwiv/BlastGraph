/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;


import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author yeyanbo
 *
 */
public class RemoveSingleLinkageCommand extends Command {
	private Map<ValueEdge, Pair<HitVertex>> removed;

	/**
	 * 
	 */
	public RemoveSingleLinkageCommand() {
		this.isUndoable = true;
		this.commandName = "RemoveSingleLinkage";
		removed = new HashMap<ValueEdge, Pair<HitVertex>> ();
	}

	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		ArrayList<ValueEdge> edges = new ArrayList<ValueEdge>(
				Global.graph.getEdges());

		Collections.sort(edges, new Comparator<ValueEdge>() {
			@Override
			public int compare(ValueEdge arg0, ValueEdge arg1) {
				// from weak to strong
				double evalue = arg0.getExpectValue() - arg1.getExpectValue();
				if (evalue < 0) {
					return 1;
				} else if (evalue > 0) {
					return -1;
				} else {
					double c1 = Global.graph.getCoverage(arg0)
							- Global.graph.getCoverage(arg1);
					if (c1 > 0) {
						return 1;
					} else if (c1 < 0) {
						return -1;
					} else {
						double c2 = Global.graph.getCoverage2(arg0)
								- Global.graph.getCoverage2(arg1);
						if (c2 > 0) {
							return 1;
						} else if (c2 < 0) {
							return -1;
						} else {
							return 0;
						}
					}
				}
			}
		});
		
		for (ValueEdge valueEdge : edges) {

			//System.out.println(valueEdge.getExpectValue());
			if (isSingle(valueEdge, Global.graph)) {
				removed.put(valueEdge, Global.graph.getEndpoints(valueEdge));
				Global.graph.removeEdge(valueEdge);
			}
		}
		
		
	}

	private boolean isSingle(ValueEdge valueEdge,
			BlastGraph<HitVertex, ValueEdge> graph) {
		Pair<HitVertex> pair =	graph.getEndpoints(valueEdge);
		
		Collection<HitVertex> vertices1 = graph.getNeighbors(pair.getFirst());
		Collection<HitVertex> vertices2 = graph.getNeighbors(pair.getSecond());
		
		Collection<HitVertex> union = CollectionUtils.intersection(vertices1, vertices2);
		//System.out.println(vertices1.size() + " " + vertices2.size() + " " + union.size());
		if(union.isEmpty()){
			return true;
		}else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		
		for (ValueEdge ve : removed.keySet()) {
			Global.graph.addEdge(ve, removed.get(ve));
		}
		

	}

}
