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

package org.bigwiv.blastgraph.gui.table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.biojava.bio.seq.impl.NewAssembledSymbolList;


import edu.uci.ics.jung.algorithms.filters.FilterUtils;

public class GeneContentModel extends SimpleTableModel {
	private Vector<String> rowHeaderName = null;
	private ArrayList<Set<HitVertex>> subSetList;


	public GeneContentModel(ArrayList<Set<HitVertex>> subSetList) {
		super();
		this.subSetList = subSetList;
		rowHeaderName = new Vector<String>();
		initModel();
	}
	

	private void initModel(){
		int count = 0;
		for (Iterator iterator = subSetList.iterator(); iterator
				.hasNext();) {
			count++;
			Set<HitVertex> subSet = (Set<HitVertex>) iterator
					.next();
			//System.out.println("count: " + count);
			this.addColumn(count + "", 0);
			int col = this.getColumnCount();
			//System.out.println("col: " + col);
			
			for (Iterator iterator2 = subSet.iterator(); iterator2
					.hasNext();) {
				HitVertex hitVertex = (HitVertex) iterator2.next();
				
				String organism = hitVertex.getOrganism();
				if (!this.rowHeaderName.contains(organism)){
					this.rowHeaderName.add(organism);
					this.addRow(0);
				}
				
				int row = this.rowHeaderName.indexOf(organism);
				//System.out.println("organism: " + organism + " row: " + row);
				int oldValue = (Integer)this.getValueAt(row, col - 1);
				this.setValueAt(oldValue + 1, row, col - 1);
			}
		}
	}


	/**
	 * @return the genomeNumber
	 */
	public int getGenomeNumber() {
		return rowHeaderName.size();
	}
	
	/**
	 * 
	 * @param row
	 * @return
	 */
	public String getRowName(int row){
		return rowHeaderName.get(row);
	}
	
	/**
	 * 
	 * @return
	 */
	public Vector<String> getRowHeader(){
		return this.rowHeaderName;
	}
	
	@Override
	public void writeTo(File file) throws IOException{

		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		String tmpString;
		
		for (int i = 0; i < content.size(); i++) {
			tmpString = getRowName(i);
			for (int j = 0; j < headerName.size(); j++) {
				tmpString = tmpString + "," + getValueAt(i, j);
			}
			
			br.write(tmpString + "\n");
		}
		
		br.close();
	}
}
