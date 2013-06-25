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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HitVertex {
	// protein.fasta info
	private String id;
	private String accession;
	private int length;
	private int index; // gene index or orf nummber in a genome
	private int strand; // 1 for forward, -1 for reverse, 0 for unknown or
						// doesn't matter
	private Segment location; // gene location in a genome
	private String genomeAcc;
	private Taxon taxon;
	private String description;
	private String organism;

	// Other information
	private HashMap<String, String> attributes;

	/**
	 * 
	 * @param id
	 * @param accession
	 * @param length
	 * @param strand
	 * @param index
	 * @param location
	 * @param genomeAcc
	 * @param taxon
	 * @param description
	 * @param organism
	 */
	public HitVertex(String id, String accession, int length, int index, int strand,
			 Segment location, String genomeAcc, Taxon taxon,
			String description, String organism) {
		super();
		this.id = id;
		this.accession = accession;
		this.length = length;
		this.index = index;
		this.strand = strand;
		this.location = location;
		this.genomeAcc = genomeAcc;
		this.taxon = taxon;
		this.description = description;
		this.organism = organism;
		this.attributes = new HashMap<String, String>();
	}

	/**
	 * @param accession
	 * @param length
	 * @param description
	 */
	public HitVertex(String id) {
		super();
		this.id = id;
		this.accession = "";
		this.length = 0;
		this.strand = 0;
		this.index = 0;
		this.location = null;
		this.genomeAcc = "";
		this.taxon = null;
		this.description = "";
		this.organism = "";
		this.attributes = new HashMap<String, String>();
	}

	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param accession
	 *            the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the organism
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * @param organism
	 *            the organism to set
	 */
	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public Map<String, String> getAllAttributes() {
		return attributes;
	}

	/**
	 * @return the strand
	 */
	public int getStrand() {
		return strand;
	}

	/**
	 * @param strand
	 *            the strand to set
	 */
	public void setStrand(int strand) {
		this.strand = strand;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the location
	 */
	public Segment getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Segment location) {
		this.location = location;
	}

	/**
	 * @return the genomeAcc
	 */
	public String getGenomeAcc() {
		return genomeAcc;
	}

	/**
	 * @param genomeAcc
	 *            the genomeAcc to set
	 */
	public void setGenomeAcc(String genomeAcc) {
		this.genomeAcc = genomeAcc;
	}

	/**
	 * @return the taxon
	 */
	public Taxon getTaxon() {
		return taxon;
	}

	/**
	 * @param taxon
	 *            the taxon to set
	 */
	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	/**
	 * @return value of the otherInfo by key
	 */
	public String getAttribute(String key) {
		return (String) attributes.get(key);
	}

	/**
	 * @param key
	 *            of otherInfo
	 * @param value
	 *            of otherInfo
	 */
	public void putAttribute(String key, String value) {
		this.attributes.put(key, value);
	}

	/**
	 * check all values to see whether they contain string, for search function
	 * 
	 * @param string
	 * @return
	 */
	public boolean contains(String string) {
		String query = string.toLowerCase().trim();
		Pattern pattern = Pattern.compile(".*" + query + ".*");
		if (id.toLowerCase().equals(query)) {
			return true;
		} else if (accession.toLowerCase().equals(query)) {
			return true;
		} else if (description != null
				&& pattern.matcher(description.toLowerCase()).matches()) {
			return true;
		} else if (organism != null
				&& pattern.matcher(organism.toLowerCase()).matches()) {
			return true;
		} else if (genomeAcc != null
				&& pattern.matcher(genomeAcc.toLowerCase()).matches()) {
			return true;
		} else {
			for (String key : attributes.keySet()) {
				if (pattern.matcher(key.toLowerCase()).matches()) {
					return true;
				} else if (pattern.matcher(attributes.get(key).toLowerCase())
						.matches()) {
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HitVertex other = (HitVertex) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


}
