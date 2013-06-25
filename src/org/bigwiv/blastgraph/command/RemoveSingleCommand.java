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
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
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
	 * @see org.bigwiv.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		
		for (HitVertex hitVertex : removed) {
			Global.graph.removeVertex(hitVertex);
		}
		
	}

}
