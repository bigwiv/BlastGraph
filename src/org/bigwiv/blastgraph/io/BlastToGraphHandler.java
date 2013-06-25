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

package org.bigwiv.blastgraph.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.Segment;
import org.bigwiv.blastgraph.SubHit;
import org.bigwiv.blastgraph.Taxon;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.biojava.bio.search.SearchContentHandler;
import org.biojava.bio.seq.db.SequenceDB;
import org.biojava.bio.seq.db.SequenceDBInstallation;


import edu.uci.ics.jung.graph.UndirectedGraph;

public class BlastToGraphHandler implements SearchContentHandler {
	private UndirectedGraph<HitVertex, ValueEdge> graph;
	// private WorkStatus Global.WORK_STATUS;

	// The ID of the database searched
	private String databaseID;
	// The ID of the query sequence
	private String queryID;

	// Data holders for search result properties
	private Map resultPreAnnotation;
	private Map hitData;
	private Map subHitData;

	private ArrayList<SubHit> subHits;
	// Flag indicating whether there are more results in the stream
	private boolean moreSearchesAvailable = false;

	private HitVertex queryVertex;

	public BlastToGraphHandler(UndirectedGraph<HitVertex, ValueEdge> graph) {
		this.graph = graph;
		// this.Global.WORK_STATUS = Global.WORK_STATUS;

		queryVertex = null;

		resultPreAnnotation = new HashMap();
		hitData = new HashMap();
		subHitData = new HashMap();

	}

	@Override
	public void setQueryID(String queryID) {
		this.queryID = queryID;
		addSearchProperty("queryId", queryID);
	}

	@Override
	public void setDatabaseID(String databaseID) {
		this.databaseID = databaseID;
		addSearchProperty("databaseId", databaseID);
	}

	@Override
	public boolean getMoreSearches() {
		return moreSearchesAvailable;
	}

	@Override
	public void setMoreSearches(boolean value) {
		moreSearchesAvailable = value;
		if (moreSearchesAvailable == false) {
			Global.WORK_STATUS.setMessage("Graph complete: "
					+ graph.getVertexCount() + " vertices; "
					+ graph.getEdgeCount() + " edges.");
		}
	}

	@Override
	public void startSearch() {
		// counter2 = 0 ;
	}

	@Override
	public void endSearch() {

		Global.WORK_STATUS.setMessage("Query " + queryID + " completed");

		// System.out.println(getMoreSearches());

		queryVertex = null;
		resultPreAnnotation.clear();
	}

	@Override
	public void startHeader() {
	}

	@Override
	public void endHeader() {
//		System.out
//				.println((String) resultPreAnnotation.get("queryDescription"));
	}

	@Override
	public void startHit() {
		hitData.clear();
		subHits = new ArrayList();
	}

	@Override
	public void endHit() {
		if (queryVertex == null) {
			String querySeqInfo = (String) resultPreAnnotation
			.get("queryDescription");
			int queryLength = Integer.parseInt((String) resultPreAnnotation
					.get("queryLength"));
			queryVertex = makeHitVertex(querySeqInfo, queryLength);
			graph.addVertex(queryVertex);
		}

		String subjectSeqInfo = (String) hitData.get("subjectDescription");
		
		int subjectLength = Integer.parseInt((String) hitData
				.get("subjectSequenceLength"));

		HitVertex subjectVertex = makeHitVertex(subjectSeqInfo, subjectLength);

		// skip self hit
		if (subjectVertex.equals(queryVertex))
			return;

		ValueEdge newValueEdge = makeValueEdge();

		ValueEdge existValueEdge = graph.findEdge(queryVertex, subjectVertex);

		if (existValueEdge == null) {
			graph.addEdge(newValueEdge, queryVertex, subjectVertex);
		} else if (existValueEdge.getExpectValue() > newValueEdge
				.getExpectValue()) {
			graph.removeEdge(existValueEdge);
			graph.addEdge(newValueEdge, queryVertex, subjectVertex);
		}
	}

	@Override
	public void startSubHit() {
		subHitData.clear();
	}

	@Override
	public void endSubHit() {
		subHits.add(makeSubHit());
	}

	@Override
	public void addSearchProperty(Object key, Object value) {
		resultPreAnnotation.put(key, value);
//		System.out.println(key + ": " + value);
	}

	@Override
	public void addHitProperty(Object key, Object value) {
		hitData.put(key, value);
	}

	@Override
	public void addSubHitProperty(Object key, Object value) {
		subHitData.put(key, value);
	}

	private ValueEdge makeValueEdge() {
		/*
		 * String tmp[] =
		 * ((String)resultPreAnnotation.get("queryDescription")).split("| ");
		 * String queryId = tmp[0] + "|"; String queryDescription = tmp[1];
		 * 
		 * String subjectAccession = (String)hitData.get("subjectId"); String
		 * subjectDescription = (String)hitData.get("subjectDescription"); int
		 * subjectSequenceLength =
		 * Integer.parseInt((String)hitData.get("subjectSequenceLength"));
		 */

		SubHit subHit[] = new SubHit[subHits.size()];
		subHits.toArray(subHit);
		Arrays.sort(subHit, SubHit.byScore);
		int numSubHit = subHit.length;

		double expectValue = subHit[0].getExpectValue();
		double score = subHit[0].getScore();

		return new ValueEdge(expectValue, score, numSubHit, subHit);
	}

	private SubHit makeSubHit() {

		double score = Double.parseDouble((String) subHitData.get("score"));

		double expectValue;
		String val = (String) subHitData.get("expectValue");
		// Blast sometimes uses invalid formatting such as 'e-156'
		// rather than '1e-156'
		if (val.startsWith("e"))
			expectValue = Double.parseDouble("1" + val);
		else
			expectValue = Double.parseDouble(val);

		int numberOfIdentities = Integer.parseInt((String) subHitData
				.get("numberOfIdentities"));
		int numberOfPositives = Integer.parseInt((String) subHitData
				.get("numberOfPositives"));
		double percentageIdentity = Double.parseDouble((String) subHitData
				.get("percentageIdentity"));
		int alignmentSize = Integer.parseInt((String) subHitData
				.get("alignmentSize"));
		int querySequenceStart = Integer.parseInt((String) subHitData
				.get("querySequenceStart"));
		int querySequenceEnd = Integer.parseInt((String) subHitData
				.get("querySequenceEnd"));
		int subjectSequenceStart = Integer.parseInt((String) subHitData
				.get("subjectSequenceStart"));
		int subjectSequenceEnd = Integer.parseInt((String) subHitData
				.get("subjectSequenceEnd"));

		return new SubHit(expectValue, score, numberOfIdentities,
				numberOfPositives, percentageIdentity, alignmentSize,
				querySequenceStart, querySequenceEnd, subjectSequenceStart,
				subjectSequenceEnd);
	}
	
	private HitVertex makeHitVertex(String seqInfo, int length){

		String gi = "", accession = "", description = "", organism = "", genomeAcc = "";
		int strand, index, geneStart, geneStop;
		Taxon taxon;
		
		String tmp[] = seqInfo.split("\\|");
		gi = tmp[1].trim();
		accession = (tmp[3].split("\\."))[0].trim();
		index = Integer.parseInt(tmp[5].trim());
		strand = Integer.parseInt(tmp[7].trim());
		String [] location = tmp[9].trim().split("-");
		geneStart = Integer.parseInt(location[0]);
		geneStop = Integer.parseInt(location[1]);
		genomeAcc = tmp[11].trim();
		taxon = new Taxon(tmp[13].trim());
		
		int i = tmp[14].lastIndexOf("[");
		
		description = tmp[14].substring(0, i).trim();
		organism = tmp[14].substring(i + 1, tmp[14].length() - 1).trim();
		//System.out.println(queryDescription + ": " + queryOrganism);

		// Store all query into Map
		return new HitVertex(gi, accession, length,
				 index, strand, new Segment(geneStart, geneStop),genomeAcc , taxon, description, organism);
	}
}
