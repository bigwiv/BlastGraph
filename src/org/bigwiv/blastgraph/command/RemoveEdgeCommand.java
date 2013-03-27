/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.util.Collection;
import java.util.Map;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;


import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * @author yeyanbo
 *
 */
public class RemoveEdgeCommand extends Command {

	/**
	 * 
	 */
	private BlastGraph<HitVertex, ValueEdge> curSubGraph;
	private Map<ValueEdge, Pair<HitVertex>> removed;
	private VisualizationViewer<HitVertex, ValueEdge> vv;
	
	public RemoveEdgeCommand(BlastGraph<HitVertex, ValueEdge> curSubGraph, Map<ValueEdge, Pair<HitVertex>> removed, VisualizationViewer<HitVertex, ValueEdge> vv) {
		this.isUndoable = true;
		this.commandName = "RemoveEdge";
		this.curSubGraph = curSubGraph;
		this.removed = removed;
		this.vv = vv;
	}

	/* (non-Javadoc)
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		for (ValueEdge ve : removed.keySet()) {
			Global.graph.removeEdge(ve);
			curSubGraph.removeEdge(ve);
		}
		vv.repaint();
		
	}

	/* (non-Javadoc)
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		
		for (ValueEdge ve : removed.keySet()) {
			Global.graph.addEdge(ve, removed.get(ve));
			curSubGraph.addEdge(ve, removed.get(ve));
		}
		vv.repaint();
		
	}

}
