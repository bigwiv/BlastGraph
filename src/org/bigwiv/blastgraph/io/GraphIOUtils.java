package org.bigwiv.blastgraph.io;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.Segment;
import org.bigwiv.blastgraph.SubHit;
import org.bigwiv.blastgraph.Taxon;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.biojava.bio.BioException;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.xml.sax.SAXException;


import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import edu.uci.ics.jung.io.graphml.NodeMetadata;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class GraphIOUtils {

	public static void saveNetwork(BlastGraph<HitVertex, ValueEdge> graph,
			File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write("gi1,gi2,evalue,score,coverage\n");
		for (ValueEdge edge : graph.getEdges()) {
			Pair pair = graph.getEndpoints(edge);
			HitVertex hv1 = (HitVertex) pair.getFirst();
			HitVertex hv2 = (HitVertex) pair.getSecond();
			fw.write(hv1.getId() + "," + hv2.getId() + ","
					+ edge.getExpectValue() + "," + edge.getScore() + ","
					+ edge.getQueryCoverage() * edge.getSubjectCoverage() * 1.0
					/ (hv1.getLength() * hv2.getLength()) + "\n");
		}
		fw.close();
	}

	public static void saveSinglePoints(BlastGraph<HitVertex, ValueEdge> graph,
			File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		for (Set<HitVertex> set : graph.getSubSet()) {
			if (set.size() == 1)
				fw.write(set.iterator().next().getAccession() + "\n");
		}
		fw.close();
	}

	public static void saveCluster(BlastGraph<HitVertex, ValueEdge> graph,
			File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		List<Set<HitVertex>> myList = graph.getSubSet();
		// output each cluster
		int groupCount = 0;

		for (Iterator iterator = myList.iterator(); iterator.hasNext();) {
			Set<HitVertex> set = (Set<HitVertex>) iterator.next();
			// fw.write("Gene" + groupCount++ + "(" + set.size() + "):");
			// System.out.println(set.size());
			for (HitVertex hitVertex : set) {
				fw.write(hitVertex.getId() + " ");
			}
			fw.write("\n");

		}
		fw.close();
	}

	public static void writeDescription(Collection<HitVertex> vertexs, File file)
			throws IOException {
		FileWriter outputWriter = new FileWriter(file);
		outputWriter
				.write("gi\taccession\tlength\tstrand\tindex\tlocation\tgenomeAcc\ttaxon\tdescription\torganism\n");
		for (HitVertex hv : vertexs) {
			outputWriter.write(hv.getId() + "\t"
					+  hv.getAccession() +  "\t"
					+ hv.getLength() +  "\t"
					+ hv.getStrand() +  "\t"
					+ hv.getIndex() +  "\t"
					+ hv.getLocation() +  "\t"
					+ hv.getGenomeAcc() +  "\t"
					+ hv.getTaxon() +  "\t"
					+ hv.getDescription() +  "\t"
					+ hv.getOrganism() +  "\n");
		}
		outputWriter.close();
	}

	public static void toGraphML(
			final UndirectedGraph<HitVertex, ValueEdge> graph, File gmlFile)
			throws IOException {

		GraphMLWriter<HitVertex, ValueEdge> graphMLWriter = new GraphMLWriter<HitVertex, ValueEdge>();

		// -------------------------------vertex data-------------------------
		graphMLWriter.setVertexIDs(new Transformer<HitVertex, String>() {
			@Override
			public String transform(HitVertex hv) {
				return hv.getId();
			}
		});

		graphMLWriter.addVertexData("gi", "gi", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return hv.getId();
					}
				});

		graphMLWriter.addVertexData("accession", "accession", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return StringEscapeUtils.escapeXml(hv.getAccession());
					}
				});

		graphMLWriter.addVertexData("length", "length", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return Integer.toString(hv.getLength());
					}
				});
		
		graphMLWriter.addVertexData("index", "index", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return Integer.toString(hv.getIndex());
					}
				});
		
		graphMLWriter.addVertexData("strand", "strand", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return Integer.toString(hv.getStrand());
					}
				});

		graphMLWriter.addVertexData("location", "location", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return StringEscapeUtils.escapeXml(hv.getLocation().toString());
					}
				});

		graphMLWriter.addVertexData("genomeAcc", "genomeAcc", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return StringEscapeUtils.escapeXml(hv.getGenomeAcc());
					}
				});

		graphMLWriter.addVertexData("taxon", "taxon", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return StringEscapeUtils.escapeXml(hv.getTaxon().toString());
					}
				});

		graphMLWriter.addVertexData("description", "description", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return StringEscapeUtils.escapeXml(hv.getDescription());
					}
				});

		graphMLWriter.addVertexData("organism", "organism", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						return StringEscapeUtils.escapeXml(hv.getOrganism());
					}
				});

		graphMLWriter.addVertexData("attributes", "attributes", "",
				new Transformer<HitVertex, String>() {
					@Override
					public String transform(HitVertex hv) {
						String attrsString = "";
						Map<String, String> attributes = hv.getAllAttributes();
						for (String key : attributes.keySet()) {
							attrsString += key + Global.getSeparator(':')
									+ attributes.get(key)
									+ Global.getSeparator(';');
						}
						return StringEscapeUtils.escapeXml(attrsString);
					}
				});

		// -------------------------------edge data-------------------------
		graphMLWriter.setEdgeIDs(new Transformer<ValueEdge, String>() {
			@Override
			public String transform(ValueEdge ve) {
				Pair<HitVertex> hvs = graph.getEndpoints(ve);
				return hvs.getFirst().getId() + "-" + hvs.getSecond().getId();
			}

		});

		graphMLWriter.addEdgeData("expectValue", "expectValue", "",
				new Transformer<ValueEdge, String>() {
					@Override
					public String transform(ValueEdge ve) {
						return Double.toString(ve.getExpectValue());
					}
				});

		graphMLWriter.addEdgeData("score", "score", "",
				new Transformer<ValueEdge, String>() {
					@Override
					public String transform(ValueEdge ve) {
						return Double.toString(ve.getScore());
					}
				});

		graphMLWriter.addEdgeData("numSubHit", "numSubHit", "",
				new Transformer<ValueEdge, String>() {
					@Override
					public String transform(ValueEdge ve) {
						return Integer.toString(ve.getNumSubHit());
					}
				});

		graphMLWriter.addEdgeData("subHit", "subHit", "",
				new Transformer<ValueEdge, String>() {
					@Override
					public String transform(ValueEdge ve) {
						SubHit[] subHits = ve.getSubHit();
						String subhitString = "";
						for (int i = 0; i < subHits.length; i++) {
							SubHit subHit = subHits[i];
							subhitString = subhitString + i + ","
									+ subHit.getExpectValue() + ","
									+ subHit.getScore() + ","
									+ subHit.getNumberOfIdentities() + ","
									+ subHit.getNumberOfPositives() + ","
									+ subHit.getPercentageIdentity() + ","
									+ subHit.getAlignmentSize() + ","
									+ subHit.getQuerySequenceStart() + ","
									+ subHit.getQuerySequenceEnd() + ","
									+ subHit.getSubjectSequenceStart() + ","
									+ subHit.getSubjectSequenceEnd()
									+ Global.getSeparator();
						}
						return subhitString;
					}
				});

		FileWriter outputWriter = new FileWriter(gmlFile);

		graphMLWriter.save(graph, outputWriter);
		outputWriter.close();
	}

	/**
	 * populate graph with a giving graphML file referred by fr
	 * 
	 * @param graph
	 * @param fr
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	/*
	 * public static void readFromGraphML(BlastGraph<HitVertex, ValueEdge>
	 * graph, FileReader fr) throws ParserConfigurationException, SAXException,
	 * IOException { GraphMLReader<BlastGraph<HitVertex, ValueEdge>, HitVertex,
	 * ValueEdge> reader = new GraphMLReader<BlastGraph<HitVertex, ValueEdge>,
	 * HitVertex, ValueEdge>();
	 * 
	 * reader.load(fr, graph);
	 * 
	 * Map<String, GraphMLMetadata<HitVertex>> verticesData = reader
	 * .getVertexMetadata(); Map<String, GraphMLMetadata<ValueEdge>> edgesData =
	 * reader .getEdgeMetadata();
	 * 
	 * for (HitVertex hv : graph.getVertices()) {
	 * hv.setGi(verticesData.get("gi").transformer.transform(hv));
	 * hv.setAccession(verticesData.get("accession").transformer
	 * .transform(hv));
	 * hv.setLength(Integer.parseInt(verticesData.get("length").transformer
	 * .transform(hv)));
	 * hv.setDescription(verticesData.get("description").transformer
	 * .transform(hv)); hv.setOrganism(verticesData.get("organism").transformer
	 * .transform(hv)); if
	 * (verticesData.get("organism").transformer.transform(hv) != null) {
	 * String[] attrsString = verticesData.get("organism").transformer
	 * .transform(hv).split(separator + ";"); for (int i = 0; i <
	 * attrsString.length; i++) { String attr[] = attrsString[i].split(separator
	 * + ":"); hv.putAttribute(attr[0], attr[1]); } } }
	 * 
	 * for (ValueEdge ve : graph.getEdges()) {
	 * ve.setExpectValue(Double.parseDouble
	 * (edgesData.get("expectValue").transformer .transform(ve)));
	 * ve.setScore(Double.parseDouble(edgesData.get("score").transformer
	 * .transform(ve)));
	 * ve.setNumSubHit(Integer.parseInt(edgesData.get("numSubHit").transformer
	 * .transform(ve))); String[] subhitStrings =
	 * edgesData.get("subHit").transformer .transform(ve).split(separator);
	 * 
	 * SubHit[] subHit = new SubHit[subhitStrings.length]; for (int i = 0; i <
	 * subhitStrings.length; i++) { if (!subhitStrings[i].equals("")) { String[]
	 * subHitValues = subhitStrings[i].split(","); double subExpectValue =
	 * Double.parseDouble(subHitValues[1]); double subScore =
	 * Double.parseDouble(subHitValues[2]); int numberOfIdentities =
	 * Integer.parseInt(subHitValues[3]); int numberOfPositives =
	 * Integer.parseInt(subHitValues[4]); double percentageIdentity = Double
	 * .parseDouble(subHitValues[5]); int alignmentSize =
	 * Integer.parseInt(subHitValues[6]); int querySequenceStart =
	 * Integer.parseInt(subHitValues[7]); int querySequenceEnd =
	 * Integer.parseInt(subHitValues[8]); int subjectSequenceStart = Integer
	 * .parseInt(subHitValues[9]); int subjectSequenceEnd =
	 * Integer.parseInt(subHitValues[10]); subHit[i] = new
	 * SubHit(subExpectValue, subScore, numberOfIdentities, numberOfPositives,
	 * percentageIdentity, alignmentSize, querySequenceStart, querySequenceEnd,
	 * subjectSequenceStart, subjectSequenceEnd); } }
	 * 
	 * ve.setSubHit(subHit); }
	 * 
	 * }
	 */

	/**
	 * read and return a graph from graphML file referred by fr
	 * 
	 * @param fr
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws GraphIOException
	 */
	public static BlastGraph<HitVertex, ValueEdge> readFromGraphML2(File gmlFile)
			throws IOException, ParserConfigurationException, SAXException,
			GraphIOException {

		/* Create the Graph Transformer */
		Transformer<GraphMetadata, Graph<HitVertex, ValueEdge>> graphTransformer = new Transformer<GraphMetadata, Graph<HitVertex, ValueEdge>>() {

			public Graph<HitVertex, ValueEdge> transform(GraphMetadata metadata) {
				if (metadata.getEdgeDefault().equals(
						metadata.getEdgeDefault().DIRECTED)) {
					return new DirectedSparseGraph<HitVertex, ValueEdge>();
				} else {
					return new BlastGraph<HitVertex, ValueEdge>();
				}
			}
		};

		/* Create the Vertex Transformer */
		Transformer<NodeMetadata, HitVertex> vertexTransformer = new Transformer<NodeMetadata, HitVertex>() {
			public HitVertex transform(NodeMetadata metadata) {
				String gi = metadata.getProperty("gi");
				String accession = metadata.getProperty("accession");
				int length = Integer.parseInt(metadata.getProperty("length"));
				int strand = Integer.parseInt(metadata.getProperty("strand"));
				int index = Integer.parseInt(metadata.getProperty("index"));
				Segment location = new Segment(metadata.getProperty("location"));
				String genomeAcc = metadata.getProperty("genomeAcc");
				Taxon taxon = new Taxon(metadata.getProperty("taxon"));
				String description = metadata.getProperty("description");
				String organism = metadata.getProperty("organism");
				HitVertex hv = new HitVertex(gi, accession, length,
						index, strand,  location, genomeAcc, taxon, description, organism);
				if (metadata.getProperty("attributes") != null) {
					String[] attrsString = metadata.getProperty("attributes")
							.split(Global.getSeparator(';'));
					for (int i = 0; i < attrsString.length; i++) {
						String attr[] = attrsString[i].split(Global
								.getSeparator(':'));
						if (attr.length == 2) {
							hv.putAttribute(attr[0], attr[1]);
						} else {
							hv.putAttribute(attr[0], "");
						}
					}
				}
				return hv;
			}
		};

		Transformer<EdgeMetadata, ValueEdge> edgeTransformer = new Transformer<EdgeMetadata, ValueEdge>() {
			public ValueEdge transform(EdgeMetadata metadata) {
				double expectValue = Double.parseDouble(metadata
						.getProperty("expectValue"));
				double score = Double
						.parseDouble(metadata.getProperty("score"));
				int numSubHit = Integer.parseInt(metadata
						.getProperty("numSubHit"));
				String[] subhitStrings = metadata.getProperty("subHit").split(
						Global.getSeparator());
				SubHit[] subHit = new SubHit[subhitStrings.length];
				for (int i = 0; i < subhitStrings.length; i++) {
					if (!subhitStrings[i].equals("")) {
						String[] subHitValues = subhitStrings[i].split(",");
						double subExpectValue = Double
								.parseDouble(subHitValues[1]);
						double subScore = Double.parseDouble(subHitValues[2]);
						int numberOfIdentities = Integer
								.parseInt(subHitValues[3]);
						int numberOfPositives = Integer
								.parseInt(subHitValues[4]);
						double percentageIdentity = Double
								.parseDouble(subHitValues[5]);
						int alignmentSize = Integer.parseInt(subHitValues[6]);
						int querySequenceStart = Integer
								.parseInt(subHitValues[7]);
						int querySequenceEnd = Integer
								.parseInt(subHitValues[8]);
						int subjectSequenceStart = Integer
								.parseInt(subHitValues[9]);
						int subjectSequenceEnd = Integer
								.parseInt(subHitValues[10]);
						subHit[i] = new SubHit(subExpectValue, subScore,
								numberOfIdentities, numberOfPositives,
								percentageIdentity, alignmentSize,
								querySequenceStart, querySequenceEnd,
								subjectSequenceStart, subjectSequenceEnd);
					}
				}

				return new ValueEdge(expectValue, score, numSubHit, subHit);

			}
		};

		Transformer<HyperEdgeMetadata, ValueEdge> hyperEdgeTransformer = new Transformer<HyperEdgeMetadata, ValueEdge>() {
			public ValueEdge transform(HyperEdgeMetadata metadata) {
				double expectValue = Double.parseDouble(metadata
						.getProperty("expectValue"));
				double score = Double
						.parseDouble(metadata.getProperty("score"));
				int numSubHit = Integer.parseInt(metadata
						.getProperty("numSubHit"));
				String[] subhitStrings = metadata.getProperty("subhitStrings")
						.split(Global.getSeparator());
				SubHit[] subHit = new SubHit[numSubHit];
				for (int i = 0; i < subhitStrings.length; i++) {
					if (!subhitStrings[i].equals("")) {
						String[] subHitValues = subhitStrings[i].split(",");
						double subExpectValue = Double
								.parseDouble(subHitValues[1]);
						double subScore = Double.parseDouble(subHitValues[2]);
						int numberOfIdentities = Integer
								.parseInt(subHitValues[3]);
						int numberOfPositives = Integer
								.parseInt(subHitValues[4]);
						double percentageIdentity = Double
								.parseDouble(subHitValues[5]);
						int alignmentSize = Integer.parseInt(subHitValues[6]);
						int querySequenceStart = Integer
								.parseInt(subHitValues[7]);
						int querySequenceEnd = Integer
								.parseInt(subHitValues[8]);
						int subjectSequenceStart = Integer
								.parseInt(subHitValues[9]);
						int subjectSequenceEnd = Integer
								.parseInt(subHitValues[10]);
						subHit[i] = new SubHit(subExpectValue, subScore,
								numberOfIdentities, numberOfPositives,
								percentageIdentity, alignmentSize,
								querySequenceStart, querySequenceEnd,
								subjectSequenceStart, subjectSequenceEnd);
					}
				}

				return new ValueEdge(expectValue, score, numSubHit, subHit);

			}
		};

		FileReader fr = new FileReader(gmlFile);
		GraphMLReader2<Graph<HitVertex, ValueEdge>, HitVertex, ValueEdge> reader = new GraphMLReader2<Graph<HitVertex, ValueEdge>, HitVertex, ValueEdge>(
				fr, graphTransformer, vertexTransformer, edgeTransformer,
				hyperEdgeTransformer);

		return (BlastGraph<HitVertex, ValueEdge>) reader.readGraph();
	}

	/**
	 * Returns a graph which consists of the union of the two input graphs.
	 * Assumes that both graphs are of a type that can accept the vertices and
	 * edges found in both graphs. The resultant graph contains all constraints
	 * that are common to both graphs.
	 */
	public static BlastGraph<HitVertex, ValueEdge> union(
			BlastGraph<HitVertex, ValueEdge> g1,
			BlastGraph<HitVertex, ValueEdge> g2) {
		BlastGraph<HitVertex, ValueEdge> g = new BlastGraph<HitVertex, ValueEdge>();
		for (HitVertex hv : CollectionUtils.union(g1.getVertices(),
				g2.getVertices())) {
			g.addVertex(hv);
		}
		for (ValueEdge ve : g1.getEdges()) {
			g.addEdge(ve, g1.getEndpoints(ve));
		}
		for (ValueEdge ve : g2.getEdges()) {
			g.addEdge(ve, g2.getEndpoints(ve));
		}
		return g;
	}

	/**
	 * @param blastGraph
	 *            the graph of which info to import to
	 * @param br
	 *            BufferedFilereader refer to a csv file import a csv file with
	 *            other information of hit vertices, file should contain header
	 *            describing attributes and the first column should be Gi number
	 * @throws IOException
	 */
	public static void importVerticesInfo(
			BlastGraph<HitVertex, ValueEdge> graph, File file)
			throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));

		// String quotes = Global.SETTING.get("CSV_QUOTES");
		// quotes = quotes.equals("null") ? "" : quotes;
		// String separator = Global.SETTING.get("CSV_SEPARATOR");

		String line = br.readLine();
		boolean isFirstLine = true;
		String[] attrNames = null;
		String[] attrs;
		while (line != null) {
			if (isFirstLine) {
				isFirstLine = false;
				attrNames = line.trim().split("\\|");
			} else {
				attrs = line.trim().split("\\|");
				HitVertex hv = graph.getVertex(attrs[0].trim());
				if (hv != null) {
					for (int i = 1; i < attrs.length; i++) {
						hv.putAttribute(attrNames[i].trim(), attrs[i].trim());
						// System.out.println(hv.getAttribute(attrNames[i].trim()));
					}

				} else {
					System.err.println(attrs[0].trim() + " not found!");
				}
			}
			line = br.readLine();
		}
		br.close();
	}

	public static HashMap<String, RichSequence> importFasta(FileReader fr)
			throws NoSuchElementException, BioException {
		HashMap<String, RichSequence> seqMap = new HashMap<String, RichSequence>();

		RichSequenceIterator sequences = RichSequence.IOTools.readFastaProtein(
				new BufferedReader(fr), null);
		while (sequences.hasNext()) {
			RichSequence rs = sequences.nextRichSequence();
			seqMap.put(rs.getIdentifier(), rs);
		}
		return seqMap;
	}

	public static void exportFastaCluster(
			BlastGraph<HitVertex, ValueEdge> graph,
			HashMap<String, RichSequence> seqMap, String saveDir)
			throws IOException {

		List<Set<HitVertex>> myList = graph.getSubSet();
		// output each fasta cluster
		OutputStream os = null;
		OutputStream noHitOS = new FileOutputStream(saveDir + "noHitSeqs.fasta");
		int groupCount = 1;
		for (Iterator iterator = myList.iterator(); iterator.hasNext();) {
			Set<HitVertex> set = (Set<HitVertex>) iterator.next();
			if (set.size() >= 2) {
				os = new FileOutputStream(saveDir + groupCount + "_"
						+ set.size() + "seq.fasta");
				for (HitVertex hitVertex : set) {
					RichSequence.IOTools.writeFasta(os,
							seqMap.get(hitVertex.getId()), null);
				}
				os.close();
			} else {
				for (HitVertex hitVertex : set) {
					RichSequence.IOTools.writeFasta(noHitOS,
							seqMap.get(hitVertex.getId()), null);
				}
			}
			groupCount++;
		}
		noHitOS.close();
	}

	/**
	 * Write
	 * 
	 * @param file
	 * @param vv
	 * @throws IOException
	 */
	public static void saveImage(File imageFile, String format,
			VisualizationViewer<HitVertex, ValueEdge> vv) throws IOException {
		vv.setDoubleBuffered(false);
		int width = vv.getWidth();
		int height = vv.getHeight();

		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		vv.paint(graphics);
		graphics.dispose();
		ImageIO.write(bi, format, imageFile);

		vv.setDoubleBuffered(true);
	}
}
