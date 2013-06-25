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
package org.bigwiv.blastgraph.gui.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;

import com.sun.corba.se.impl.orbutil.graph.Graph;

/**
 * @author yeyanbo
 *
 */
public class SearchResultModel extends SimpleTableModel {
	ArrayList<Set<HitVertex>> subSetList;
	String query;
	
	public SearchResultModel(ArrayList<Set<HitVertex>> subSetList, String query){
		this.subSetList = subSetList;
		this.query = query;
		initModel();
	}
	
	private void initModel(){
		this.addColumn("GraphIndex");
		this.addColumn("GI");
		this.addColumn("Accession");
		this.addColumn("Length");
		this.addColumn("OrfIndex");
		this.addColumn("Strand");
		this.addColumn("Location");
		this.addColumn("Description");
		this.addColumn("Organism");
		
		for(int i = 0; i < subSetList.size(); i++){
			Set<HitVertex> set = subSetList.get(i);
			for (HitVertex hitVertex : set) {
				if(hitVertex.contains(query)){
					this.addRow();
					int row = this.getRowCount() - 1;
					this.setValueAt(i + 1, row, 0);
					this.setValueAt(hitVertex.getId(), row, 1);
					this.setValueAt(hitVertex.getAccession(), row, 2);
					this.setValueAt(hitVertex.getLength(), row, 3);
					this.setValueAt(hitVertex.getIndex(), row, 4);
					this.setValueAt(hitVertex.getStrand(), row, 5);
					this.setValueAt(hitVertex.getLocation().toString(), row, 6);
					this.setValueAt(hitVertex.getDescription(), row, 7);
					this.setValueAt(hitVertex.getOrganism(), row, 8);
				}
			}
		}
	}
}
