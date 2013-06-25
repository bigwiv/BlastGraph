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
