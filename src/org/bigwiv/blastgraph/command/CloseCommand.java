package org.bigwiv.blastgraph.command;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;

/**
 * Empty a given graph
 * @author yeyanbo
 *
 */
public class CloseCommand extends Command {
	
	public CloseCommand() {
		this.commandName = "Close";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		Global.graph.empty();

		Global.PREFERENCES.remove("CURRENT_FILE");
	}

	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub
		
	}

}
