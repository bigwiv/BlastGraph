package org.bigwiv.blastgraph;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.bigwiv.bio.sax.blastxml.BlastXMLParserFacade;
import org.bigwiv.bio.ssbind.SeqSimilarityAdapter;
import org.bigwiv.blastgraph.io.BlastToGraphHandler;
import org.bigwiv.blastgraph.io.GraphIOUtils;
import org.biojava.bio.BioException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphIOException;

public class BlastGraph<V, E> extends
		UndirectedSparseGraph<HitVertex, ValueEdge> {
	/**
	 * map gi with HitVertex object used by getVertex() and
	 * containVertex() methods
	 */
	protected Map<String, HitVertex> hitVertices;
	
	protected ArrayList<Set<HitVertex>> subSetList;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2280763146567617357L;

	// Comparator to sort graph by number of vertices
	public static final ByVerticesComparator byVertices = new ByVerticesComparator();

	public BlastGraph() {
		super();
		hitVertices = new HashMap<String, HitVertex>();
	}

	@Override
	/**
	 * The same with super method, except add gi vertex map to hitVertices 
	 */
	public boolean addVertex(HitVertex vertex) {
		if (vertex == null) {
			throw new IllegalArgumentException("vertex may not be null");
		}
		if (!containsVertex(vertex)) {
			vertices.put(vertex, new HashMap<HitVertex, ValueEdge>());
			hitVertices.put(vertex.getId(), vertex);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get HitVertex by gi number
	 * 
	 * @param id
	 * @return
	 */
	public HitVertex getVertex(String id) {
		return hitVertices.get(id);
	}

	/**
	 * is this graph contain Vertex with specified gi number
	 * 
	 * @param id
	 * @return
	 */
	public boolean containsVertex(String id) {
		return hitVertices.containsKey(id);
	}

	/**
	 * get the coverage of and edge
	 * (queryCoverage*subjectCoverage)/(queryLength*subjectLength)
	 * @param edge
	 * @return
	 */
	public double getCoverage(ValueEdge edge) {
		Pair<HitVertex> pair = this.getEndpoints(edge);
		return edge.getQueryCoverage() * edge.getSubjectCoverage() * 1.0
				/ (pair.getFirst().getLength() * pair.getSecond().getLength());
	}

	/**
	 * get the coverage of and edge
	 * MIN(queryCoverage, subjectCoverage)/MAX(queryLength, subjectLength)
	 * @param edge
	 * @return
	 */
	public double getCoverage2(ValueEdge edge) {
		Pair<HitVertex> pair = this.getEndpoints(edge);
		return Math.min(edge.getQueryCoverage(), edge.getSubjectCoverage()) * 1.0
				/ Math.max(pair.getFirst().getLength(), pair.getSecond().getLength());
	}
	
	/**
	 * Average score density of all HSPs
	 * 
	 * @param edge
	 * @return
	 */
	public double getScoreDensity(ValueEdge edge) {
		Pair<HitVertex> pair = this.getEndpoints(edge);

		SubHit subHit[] = edge.getSubHit();
		double scoreSum = 0;
		int lengthSum = 0;
		for (int i = 0; i < subHit.length; i++) {
			scoreSum += subHit[i].getScore();
			lengthSum += Math.max(
					subHit[i].getQuerySequenceEnd()
							- subHit[i].getQuerySequenceStart(),
					subHit[i].getSubjectSequenceEnd()
							- subHit[i].getSubjectSequenceStart());
		}
		return scoreSum * 1.0 / lengthSum;
	}

	/**
	 * Average percentage identity of all HSPs
	 * 
	 * @param edge
	 * @return
	 */
	public double getPercentageIdentity(ValueEdge edge) {
		Pair<HitVertex> pair = this.getEndpoints(edge);
		SubHit subHit[] = edge.getSubHit();
		int identitiesSum = 0;
		int lengthSum = 0;
		for (int i = 0; i < subHit.length; i++) {
			identitiesSum += subHit[i].getNumberOfIdentities();
			lengthSum += Math.max(
					subHit[i].getQuerySequenceEnd()
							- subHit[i].getQuerySequenceStart(),
					subHit[i].getSubjectSequenceEnd()
							- subHit[i].getSubjectSequenceStart());
		}

		return identitiesSum * 100.0 / lengthSum;
	}

	/**
	 * Average percentage positive of all HSPs
	 * 
	 * @param edge
	 * @return
	 */
	public double getPercentagePositive(ValueEdge edge) {
		Pair<HitVertex> pair = this.getEndpoints(edge);
		SubHit subHit[] = edge.getSubHit();
		int posititoRemoveSum = 0;
		int lengthSum = 0;
		for (int i = 0; i < subHit.length; i++) {
			posititoRemoveSum += subHit[i].getNumberOfPositives();
			lengthSum += Math.max(
					subHit[i].getQuerySequenceEnd()
							- subHit[i].getQuerySequenceStart(),
					subHit[i].getSubjectSequenceEnd()
							- subHit[i].getSubjectSequenceStart());
		}

		return posititoRemoveSum * 100.0 / lengthSum;
	}

	/**
	 * 
	 *filt edge by evalue,
	 * 
	 * @param evalue
	 * @param index
	 *            filt to the <b>index</b>th subgraph(index>=0 && index< submax)
	 *            or all(index<0)
	 * @return removed edges
	 */
	public Map<ValueEdge, Pair<HitVertex>> filtByEvalue(double evalue, int index) {
		Collection<ValueEdge> toRemove;
		Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>> ();
		if (index >= 0) {
			if(index >= subSetList.size()) return null;
			BlastGraph indexGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(index), this);
			toRemove = indexGraph.getEdges();
			System.out.println(toRemove.size());
		} else {
			toRemove = new HashSet<ValueEdge>();
			for (ValueEdge ve : this.getEdges()) {
				toRemove.add(ve);
			}
		}

		for (ValueEdge ve : toRemove) {
			Pair<HitVertex> pair = this.getEndpoints(ve);
			HitVertex hv1 = pair.getFirst();
			HitVertex hv2 = pair.getSecond();

			double ev = ve.getExpectValue();
			if (ev >= evalue) {

//				DecimalFormat formatter = new DecimalFormat("0.##E0");
//				hv1.putAttribute("related(E" + evalue + ")", hv2.getId()
//						+ "(" + formatter.format(ev) + ")");
//				hv2.putAttribute("related(E" + evalue + ")", hv1.getId()
//						+ "(" + formatter.format(ev) + ")");
				removed.put(ve, pair);
				this.removeEdge(ve);
				
			}
		}
		
		return removed;
	}

	// public BlastGraph<HitVertex, ValueEdge> filtByEvalue(double evalue) {
	// BlastGraph<HitVertex, ValueEdge> newGraph = new BlastGraph<HitVertex,
	// ValueEdge>();
	//
	// for (HitVertex v : this.getVertices()) {
	// newGraph.addVertex(v);
	// }
	//
	// Set<ValueEdge> deleted = new HashSet<ValueEdge>();
	// for (ValueEdge e : this.getEdges()) {
	// Pair<HitVertex> pair = this.getEndpoints(e);
	// if (e.getExpectValue() <= evalue) {
	// newGraph.addEdge(e, pair);
	// } else {
	// deleted.add(e);
	// }
	// }
	//
	//
	// // add relation information to the HitVertex attribute which represent
	// // where the two subGraphs split
	//
	// Set<Set<HitVertex>> subSets = newGraph.generateSubSet();
	// for (ValueEdge e : deleted) {
	// Pair<HitVertex> pair = this.getEndpoints(e);
	// DecimalFormat formatter = new DecimalFormat("0.##E0");
	// for (Set<HitVertex> subSet : subSets) {
	// if (subSet.contains(pair.getFirst())
	// && !subSet.contains(pair.getSecond())) {
	// pair.getFirst().putAttribute(
	// "related(E" + evalue + ")",
	// pair.getSecond().getAccession() + "("
	// + formatter.format(e.getExpectValue())
	// + ")");
	// pair.getSecond().putAttribute(
	// "related(E" + evalue + ")",
	// pair.getFirst().getAccession() + "("
	// + formatter.format(e.getExpectValue())
	// + ")");
	// break;
	// }
	// }
	// }
	//
	// return newGraph;
	// }

	/**
	 * 
	 * @param coverage
	 * @param index
	 * @return
	 */
	public Map<ValueEdge, Pair<HitVertex>> filtByCoverage(double coverage, int index) {
		Collection<ValueEdge> toRemove;
		Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>> ();

		if (index >= 0) {
			if(index >= subSetList.size()) return null;
			BlastGraph indexGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(index), this);
			toRemove = indexGraph.getEdges();
			// System.out.println(toRemove.size());
		} else {
			toRemove = new HashSet<ValueEdge>();
			for (ValueEdge ve : this.getEdges()) {
				toRemove.add(ve);
			}
		}

		for (ValueEdge ve : toRemove) {
			Pair<HitVertex> pair = this.getEndpoints(ve);
			HitVertex hv1 = pair.getFirst();
			HitVertex hv2 = pair.getSecond();

			double c = this.getCoverage(ve);
			if (c <= coverage) {
//				hv1.putAttribute("related(C" + coverage + ")",
//						hv2.getId() + "(" + c + ")");
//				hv2.putAttribute("related(C" + coverage + ")",
//						hv1.getId() + "(" + c + ")");
				removed.put(ve, pair);;
				this.removeEdge(ve);
			}
		}
		
		return removed;
	}
	
	/**
	 * filt by MIN(queryCoverage, subjectCoverage)/MAX(queryLength, subjectLength) 
	 * @param coverage
	 * @param index
	 * @return
	 */
	public Map<ValueEdge, Pair<HitVertex>> filtByCoverage2(double coverage, int index) {
		Collection<ValueEdge> toRemove;
		Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>> ();

		if (index >= 0) {
			if(index >= subSetList.size()) return null;
			BlastGraph indexGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(index), this);
			toRemove = indexGraph.getEdges();
			// System.out.println(toRemove.size());
		} else {
			toRemove = new HashSet<ValueEdge>();
			for (ValueEdge ve : this.getEdges()) {
				toRemove.add(ve);
			}
		}

		for (ValueEdge ve : toRemove) {
			Pair<HitVertex> pair = this.getEndpoints(ve);
			HitVertex hv1 = pair.getFirst();
			HitVertex hv2 = pair.getSecond();

			double c = this.getCoverage2(ve);
			if (c <= coverage) {
//				hv1.putAttribute("related(C2" + coverage + ")",
//						hv2.getId() + "(" + c + ")");
//				hv2.putAttribute("related(C2" + coverage + ")",
//						hv1.getId() + "(" + c + ")");
				removed.put(ve, pair);;
				this.removeEdge(ve);
			}
		}
		
		return removed;
	}

	/**
	 * filt by average Coverage Length of queryCoverage & subjectCoverage
	 * @param coverage
	 * @param index
	 * @return
	 */
	public Map<ValueEdge, Pair<HitVertex>> filtByCoverageLength(double coverageLength,
			int index) {
		Collection<ValueEdge> toRemove;
		Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>> ();

		if (index >= 0) {
			if(index >= subSetList.size()) return null;
			BlastGraph indexGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(index), this);
			toRemove = indexGraph.getEdges();
			// System.out.println(toRemove.size());
		} else {
			toRemove = new HashSet<ValueEdge>();
			for (ValueEdge ve : this.getEdges()) {
				toRemove.add(ve);
			}
		}

		for (ValueEdge ve : toRemove) {
			Pair<HitVertex> pair = this.getEndpoints(ve);
//			HitVertex hv1 = pair.getFirst();
//			HitVertex hv2 = pair.getSecond();

			double c = (ve.getQueryCoverage() + ve.getSubjectCoverage()) / 2.0;
			if (c <= coverageLength) {
//				hv1.putAttribute("related(C2" + coverage + ")",
//						hv2.getId() + "(" + c + ")");
//				hv2.putAttribute("related(C2" + coverage + ")",
//						hv1.getId() + "(" + c + ")");
				removed.put(ve, pair);;
				this.removeEdge(ve);
			}
		}
		
		return removed;
	}
	
	// public BlastGraph<HitVertex, ValueEdge> filtByCoverage(double coverage) {
	//
	// BlastGraph<HitVertex, ValueEdge> newGraph = new BlastGraph<HitVertex,
	// ValueEdge>();
	//
	// for (HitVertex v : this.getVertices()) {
	// newGraph.addVertex(v);
	// }
	//
	// Set<ValueEdge> deleted = new HashSet<ValueEdge>();
	// for (ValueEdge e : this.getEdges()) {
	// Pair<HitVertex> pair = this.getEndpoints(e);
	// if (this.getCoverage(e) >= coverage) {
	// newGraph.addEdge(e, pair);
	// } else {
	// deleted.add(e);
	// }
	// }
	//
	// /*
	// * add relation information to the HitVertex attribute which represent
	// * where the two subGraphs split
	// */
	// Set<Set<HitVertex>> subSets = newGraph.generateSubSet();
	// for (ValueEdge e : deleted) {
	// Pair<HitVertex> pair = this.getEndpoints(e);
	//
	// for (Set<HitVertex> subSet : subSets) {
	// if (subSet.contains(pair.getFirst())
	// && !subSet.contains(pair.getSecond())) {
	// pair.getFirst().putAttribute(
	// "related(C" + coverage + ")",
	// pair.getSecond().getAccession() + "("
	// + this.getCoverage(e) + ")");
	// pair.getSecond().putAttribute(
	// "related(C" + coverage + ")",
	// pair.getFirst().getAccession() + "("
	// + this.getCoverage(e) + ")");
	// break;
	// }
	// }
	// }
	//
	// return newGraph;
	// }

	/**
	 * 
	 * Average scoreDensity of HSPs
	 * @param scoreDensity
	 * @param index
	 * @return
	 */
	public Map<ValueEdge, Pair<HitVertex>> filtByScoreDensity(double scoreDensity, int index) {
		Collection<ValueEdge> toRemove;
		Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>> ();
		if (index >= 0) {
			if(index >= subSetList.size()) return null;
			BlastGraph indexGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(index), this);
			toRemove = indexGraph.getEdges();
			// System.out.println(toRemove.size());
		} else {
			toRemove = new HashSet<ValueEdge>();
			for (ValueEdge ve : this.getEdges()) {
				toRemove.add(ve);
			}
		}

		for (ValueEdge ve : toRemove) {
			Pair<HitVertex> pair = this.getEndpoints(ve);
			HitVertex hv1 = pair.getFirst();
			HitVertex hv2 = pair.getSecond();

			double scoreDensityAvr = this.getScoreDensity(ve);
			// System.out.println(scoreDensityAvr);

			if (scoreDensityAvr <= scoreDensity) {
//				hv1.putAttribute("related(SD" + scoreDensity + ")",
//						hv2.getId() + "(" + scoreDensityAvr + ")");
//				hv2.putAttribute("related(SD" + scoreDensity + ")",
//						hv1.getId() + "(" + scoreDensityAvr + ")");
				removed.put(ve, pair);;
				this.removeEdge(ve);
			}
		}
		
		return removed;
	}

	/**
	 * 
	 * @param percentageIdentity
	 *            (0~100)
	 * @param index
	 * @return 
	 */
	public Map<ValueEdge, Pair<HitVertex>>  filtByPercentageIdentity(double percentageIdentity, int index) {
		Collection<ValueEdge> toRemove;
		Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>> ();

		if (index >= 0) {
			if(index >= subSetList.size()) return null;
			BlastGraph indexGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(index), this);
			toRemove = indexGraph.getEdges();
			// System.out.println(toRemove.size());
		} else {
			toRemove = new HashSet<ValueEdge>();
			for (ValueEdge ve : this.getEdges()) {
				toRemove.add(ve);
			}
		}

		for (ValueEdge ve : toRemove) {
			Pair<HitVertex> pair = this.getEndpoints(ve);
			HitVertex hv1 = pair.getFirst();
			HitVertex hv2 = pair.getSecond();

			double pi = this.getPercentageIdentity(ve);
			// System.out.println(pi);
			if (pi <= percentageIdentity) {
//				hv1.putAttribute("related(PI" + percentageIdentity + ")",
//						hv2.getId() + "(" + pi + ")");
//				hv2.putAttribute("related(PI" + percentageIdentity + ")",
//						hv1.getId() + "(" + pi + ")");
				removed.put(ve, pair);;
				this.removeEdge(ve);
			}
		}
		
		return removed;
	}

	/**
	 * 
	 * @param percentagePositive (0~100)
	 * @param index
	 * @return
	 */
	public Map<ValueEdge, Pair<HitVertex>>  filtByPercentagePositive(double percentagePositive, int index) {
		Collection<ValueEdge> toRemove;
		Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>> ();
		if (index >= 0) {
			if(index >= subSetList.size()) return null;
			BlastGraph indexGraph = FilterUtils.createInducedSubgraph(
					subSetList.get(index), this);
			toRemove = indexGraph.getEdges();
			// System.out.println(toRemove.size());
		} else {
			toRemove = new HashSet<ValueEdge>();
			for (ValueEdge ve : this.getEdges()) {
				toRemove.add(ve);
			}
		}

		for (ValueEdge ve : toRemove) {
			Pair<HitVertex> pair = this.getEndpoints(ve);
			HitVertex hv1 = pair.getFirst();
			HitVertex hv2 = pair.getSecond();

			double pp = this.getPercentagePositive(ve);
			// System.out.println(pp);
			if (pp <= percentagePositive) {
//				hv1.putAttribute("related(PP" + percentagePositive + ")",
//						hv2.getId() + "(" + pp + ")");
//				hv2.putAttribute("related(PP" + percentagePositive + ")",
//						hv1.getId() + "(" + pp + ")");

				this.removeEdge(ve);
			}
		}
		
		return removed;
	}

	// public BlastGraph<HitVertex, ValueEdge> filtByEvalueOrCoverage(
	// double evalue, double coverage) {
	//
	// BlastGraph<HitVertex, ValueEdge> newGraph = new BlastGraph<HitVertex,
	// ValueEdge>();
	//
	// for (HitVertex v : this.getVertices()) {
	// newGraph.addVertex(v);
	// }
	//
	// for (ValueEdge e : this.getEdges()) {
	// Pair<HitVertex> pair = this.getEndpoints(e);
	// int length1 = pair.getFirst().getLength();
	// int length2 = pair.getSecond().getLength();
	// if (e.getExpectValue() <= evalue
	// || e.getQueryCoverage() * e.getSubjectCoverage() * 1.0
	// / (length1 * length2) >= coverage) {
	// newGraph.addEdge(e, pair);
	// }
	// }
	// return newGraph;
	// }

	// public void filtByConnectivity() {
	//
	// }

	public void generateSubSet() {
		//SubSetCluster<HitVertex, ValueEdge> clusterer = new SubSetCluster<HitVertex, ValueEdge>();
		WeakComponentClusterer<HitVertex, ValueEdge> clusterer = new WeakComponentClusterer<HitVertex, ValueEdge>();
		ArrayList<Set<HitVertex>> subSetList = new ArrayList<Set<HitVertex>>(
				clusterer.transform(this));
		Collections.sort(subSetList, new Comparator<Set<HitVertex>>() {
			@Override
			public int compare(Set<HitVertex> arg0, Set<HitVertex> arg1) {
				// from biggest to smallest
				return arg1.size() - arg0.size();
			}
		});
		
		this.subSetList = subSetList;
	}
	
	public ArrayList<Set<HitVertex>> getSubSet(){
		return this.subSetList;
	}

	public double getConserveness() {
		int vc = this.getVertexCount();
		if (vc > 1) {
			int ec = this.getEdgeCount();
			int maxEdges = (vc - 1) * vc / 2;
			return ec * 1.0 / maxEdges;
		} else {
			return 0;
		}
	}

	/**
	 * <code>ByVerticesComparator</code> compares <code>BlastGraph</code>s by
	 * their count of vertices. Arrays.sort() will sort <code>BlastGraph</code>
	 * array from more to less.
	 */
	public static final class ByVerticesComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			BlastGraph h1 = (BlastGraph) o1;
			BlastGraph h2 = (BlastGraph) o2;
			// biggest to smallest
			return h2.getVertexCount() - h1.getVertexCount();
		}
	}

	/**
	 * Union this graph with another
	 * 
	 * @param graphToUnion
	 */
	public void union(BlastGraph<HitVertex, ValueEdge> graphToUnion) {
		for (HitVertex hv : graphToUnion.getVertices()) {
			this.addVertex(hv);
		}

		//if the existing edge is weaker than the edge to be add, replace existing one
		//else do nothing
		ValueEdgeComparator<ValueEdge> comparator = new ValueEdgeComparator<ValueEdge>();
		for (ValueEdge ve : graphToUnion.getEdges()) {
			Pair<HitVertex> pair = graphToUnion.getEndpoints(ve);
			ValueEdge existEdge = this.findEdge(pair.getFirst(), pair.getSecond());
			if(existEdge == null){
				this.addEdge(ve, pair);
			}else if(comparator.compare(ve, existEdge) == 1){
				this.removeEdge(existEdge);
				this.addEdge(ve, pair);
			}
		}
	}

	/**
	 * Empty this Graph
	 */
	public void empty() {
		// remove all edges
		edges.clear();

		// remove all vertices
		vertices.clear();
	}

	/**
	 * Shallow copy from a given instance
	 * 
	 * @param blastGraph
	 */
	public void copy(BlastGraph<HitVertex, ValueEdge> graphToCopy) {
		// empty this graph
		this.empty();

		// union this empty graph with graphToCopy
		this.union(graphToCopy);
	}

	/**
	 * Save this graph into a GML file
	 * 
	 * @param gmlFile
	 * @throws IOException
	 */
	public void save(File gmlFile) throws IOException {
		GraphIOUtils.toGraphML(this, gmlFile);
	}

	/**
	 * Read GML graph file and populate this BlastGraph
	 * 
	 * @param gmlFile
	 * @throws GraphIOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public void readGraph(File gmlFile) throws IOException,
			ParserConfigurationException, SAXException, GraphIOException {
		this.copy(GraphIOUtils.readFromGraphML2(gmlFile));
	}

	/**
	 * Read blast XML file and populate this BlastGraph
	 * 
	 * @param blastXmlFile
	 * @throws BioException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void createFromBlast(File blastXmlFile) throws BioException,
			IOException, SAXException {
		InputSource is = new InputSource(new FileReader(blastXmlFile));
		// set parsing handler
		BlastToGraphHandler handler = new BlastToGraphHandler(this);
		SeqSimilarityAdapter adapter = new SeqSimilarityAdapter();
		adapter.setSearchContentHandler(handler);
		BlastXMLParserFacade reader = new BlastXMLParserFacade();

		// start parsing "blastFile"
		reader.setContentHandler(adapter);
		reader.parse(is);
	}

	
}
