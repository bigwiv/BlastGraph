/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.util.Collection;

import org.bigwiv.blastgraph.HitVertex;


/**
 * @author yeyanbo
 *
 */
public class TreeClusterCommand extends Command {

	/**
	 * 
	 */
	public TreeClusterCommand() {
		this.isUndoable = true;
		this.commandName = "TreeCluster";
	}

	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		Collection<HitVertex> neighbors;

	}

	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub

	}

}
