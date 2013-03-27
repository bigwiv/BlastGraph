package org.bigwiv.blastgraph.gui.table;

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
}
