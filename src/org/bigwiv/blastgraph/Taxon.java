package org.bigwiv.blastgraph;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * A string list of taxons from top taxon to bottom
 * 
 * @author yeyanbo
 * 
 */
public class Taxon {
	private List<String> taxons;
	
	/**
	 * 
	 * @param taxons 
	 */
	public Taxon(List<String> taxons) {
		this.taxons = taxons;
	}
	
	/**
	 * construct taxon from a string with levels of taxons separated by ";"
	 * @param taxons
	 */
	public Taxon(String taxons){
		this.taxons = Arrays.asList(taxons.split(";"));
	}
	
	/**
	 * get the short version of this taxon, the last level 
	 * @return
	 */
	public String getTaxon(){
		return taxons.get(taxons.size() - 1);
	}
	
	/**
	 * get the parent, the second last level
	 * @return
	 */
	public String getParentTaxon(){
		return taxons.get(taxons.size() - 2);
	}
	
	/**
	 * get the full version of this taxon,
	 * the same with toString method
	 * @return
	 */
	public String getFullTaxon(){
		return this.toString();
	}
	
	/**
	 * get the ith taxon
	 * @param i
	 * @return
	 */
	public String getTaxon(int i) {
		return taxons.get(i);
	}
	
	/**
	 * get the number of level of this taxon
	 * @return
	 */
	public int getLevels(){
		return taxons.size();
	}
	
	/**
	 * get the top level of this taxon
	 * @return
	 */
	public String getTop(){
		return taxons.get(0);
	}
	
	@Override
	public String toString() {
		return StringUtils.join(taxons, ";");
	}
}
