/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.util.HashSet;
import java.util.Set;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;


/**
 * @author yeyanbo
 *
 */
public class RemoveSingleCommand extends Command {
	Set<HitVertex> removed;
	
	public RemoveSingleCommand() {
		this.isUndoable = true;
		this.commandName = "Remove Single";
		this.removed = new HashSet<HitVertex>();
	}

	/* (non-Javadoc)
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		
		for (HitVertex hv : Global.graph.getVertices()) {
			if(Global.graph.getInEdges(hv).size() == 0) removed.add(hv);
		}
		
		for (HitVertex hitVertex : removed) {
			Global.graph.removeVertex(hitVertex);
		}
		
		
	}

	/* (non-Javadoc)
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		
		for (HitVertex hitVertex : removed) {
			Global.graph.removeVertex(hitVertex);
		}
		
	}

}
