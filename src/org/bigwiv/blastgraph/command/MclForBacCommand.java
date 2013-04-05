/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.ValueEdgeComparator;
import org.bigwiv.blastgraph.global.Global;


import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author yeyanbo
 * 
 */
public class MclForBacCommand extends Command {
	private double inflation;

	/**
	 * inflation affect the granularity of clusters default is 2
	 * 
	 * @param inflation
	 */
	public MclForBacCommand() {
		inflation = 2;
	}

	public MclForBacCommand(double inflation) {
		this.inflation = inflation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
//		String curPath = Global.getAppPath(this.getClass());
//		File mclGraphFile = new File(curPath + File.separator
//				+ "mcl_graph_file");
//		File mclClustFile = new File(curPath + File.separator
//				+ "out.mcl_graph_file.I"
//				+ ("" + inflation).replace(".", "").substring(0, 2));
//		// System.out.println(mclClustFile);
//
//		ArrayList<ValueEdge> edges = new ArrayList<ValueEdge>(
//				Global.graph.getEdges());
//
//		for (ValueEdge valueEdge : edges) {
//			Pair<HitVertex> pair = Global.graph.getEndpoints(valueEdge);
//			HitVertex hv1 = pair.getFirst();
//			HitVertex hv2 = pair.getSecond();
//
//			ArrayList<ValueEdge> ves1 = new ArrayList<ValueEdge>(
//					Global.graph.getIncidentEdges(hv1));
//			Collections.sort(ves1, new ValueEdgeComparator<ValueEdge>());
//
//			ArrayList<ValueEdge> ves2 = new ArrayList<ValueEdge>(
//					Global.graph.getIncidentEdges(hv2));
//			Collections.sort(ves2, new ValueEdgeComparator<ValueEdge>());
//
//			String hv1Taxon = hv1.getTaxon().getTaxon(3);
//			String hv2Taxon = hv2.getTaxon().getTaxon(3);
//
//			String type = hv1Taxon.compareTo(hv2Taxon) < 0 ? hv1Taxon
//					+ hv2Taxon : hv2Taxon + hv1Taxon;
//
//			double evalue = valueEdge.getExpectValue();
//
//			if (hv1Taxon.equals(hv2Taxon)
//					|| type.equals("AlphabaculovirusBetabaculovirus")) {
//				if (evalue >= 1) {
//					valueEdge.setCalculatedScore(0);
//				} else if (evalue < 10e-10) {
//					valueEdge.setCalculatedScore(10);
//				} else {
//					valueEdge.setCalculatedScore(-Math.log10(evalue));
//				}
//			} else {
//				if (evalue >= 1) {
//					ArrayList<ValueEdge> tmpList;
//					if (type.equals("DeltabaculovirusGammabaculovirus")) {
//						if (hv1Taxon.equals("Deltabaculovirus")) {
//							tmpList = ves1;
//						}else {
//							tmpList = ves2;
//						}
//					} else {
//						if (hv1Taxon.equals("Alphabaculovirus") || hv1Taxon.equals("Betabaculovirus")) {
//							tmpList = ves2;
//						} else {
//							tmpList = ves1;
//						}
//					}
//					
//					int size = tmpList.size();
//					int num = 0;
//					
//					if(tmpList.get(size - 1).getExpectValue() < 1){
//						num = 3;
//					}else {
//						num = 10;
//					}
//					
//					if (size >= num) {
//						if (tmpList.subList(size - num, size)
//								.contains(valueEdge)) {
//							valueEdge.setCalculatedScore(1);
//						} else {
//							valueEdge.setCalculatedScore(0);
//						}
//					} else {
//						valueEdge.setCalculatedScore(1);
//					}
//					
//				} else if (evalue < 10e-10) {
//					valueEdge.setCalculatedScore(10);
//				} else {
//					valueEdge.setCalculatedScore(-Math.log10(evalue));
//				}
//			}
//
//		}
//
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(mclGraphFile));
//
//			Global.WORK_STATUS.setMessage("");
//			Global.WORK_STATUS
//					.setMessage("Stage1: Generating Tabular Format...");
//			for (ValueEdge valueEdge : edges) {
//				Pair<HitVertex> pair = Global.graph.getEndpoints(valueEdge);
//				HitVertex hv1 = pair.getFirst();
//				HitVertex hv2 = pair.getSecond();
//
//				String hv1Taxon = hv1.getTaxon().getTaxon(3);
//				String hv2Taxon = hv2.getTaxon().getTaxon(3);
//
//				String type = hv1Taxon.compareTo(hv2Taxon) < 0 ? hv1Taxon
//						+ hv2Taxon : hv2Taxon + hv1Taxon;
//
//				double score = valueEdge.getCalculatedScore();
//
//				bw.write(hv1.getId() + "\t" + hv2.getId() + "\t" + score + "\n");
//
//			}
//
//			for (HitVertex hv : Global.graph.getVertices()) {
//				if (Global.graph.getInEdges(hv).size() == 0)
//					bw.write(hv.getId() + "\t" + hv.getId() + "\t" + 10 + "\n");
//			}
//
//			bw.close();
//			Global.WORK_STATUS.setMessage("Stage1 Complete");
//			Global.WORK_STATUS.setMessage("");
//
//			ProcessBuilder pb = new ProcessBuilder("mcl", "mcl_graph_file",
//					"--abc", "-I", "" + inflation);
//			// System.out.println(pb.command().toString());
//			pb.redirectErrorStream(true);
//			pb.directory(new File(curPath));
//			Process p = pb.start();
//			Global.WORK_STATUS.setMessage("Stage2: Markov Clustering...");
//			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
//			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
//			String lineStr;
//
//			while ((lineStr = inBr.readLine()) != null) {
//				// System.out.println(lineStr);
//				Global.WORK_STATUS.setMessage(lineStr);
//			}
//
//			if (p.waitFor() != 0) {
//				if (p.exitValue() == 1)
//					// System.err.println("Failed to run mcl!");
//					Global.WORK_STATUS.setError("Failed to run mcl!");
//			}
//
//			inBr.close();
//			in.close();
//			Global.WORK_STATUS.setMessage("Stage2 Complete");
//			Global.WORK_STATUS.setMessage("");
//
//			Global.WORK_STATUS.setMessage("Stage3: Regenerating SubGraphs...");
//			BufferedReader br = new BufferedReader(new FileReader(mclClustFile));
//			String line = br.readLine();
//
//			BlastGraph<HitVertex, ValueEdge> tempGraph = new BlastGraph<HitVertex, ValueEdge>();
//			int count = 0;
//			while (line != null && !line.equals("")) {
//				String[] ids = line.split("\t");
//				// set to store vertices
//				Set<HitVertex> hvs2 = new HashSet<HitVertex>();
//
//				// get and add vertices into hvs by GI in ids
//				for (int i = 0; i < ids.length; i++) {
//					hvs2.add(Global.graph.getVertex(ids[i]));
//				}
//
//				tempGraph.union(FilterUtils.createInducedSubgraph(hvs2,
//						Global.graph));
//
//				line = br.readLine();
//				Global.WORK_STATUS.setMessage("Subgraph: " + count++);
//			}
//
//			br.close();
//
//			Global.graph.copy(tempGraph);
//
//			Global.WORK_STATUS.setMessage("Stage3 Complete");
//			Global.WORK_STATUS.setMessage("");
//			Global.WORK_STATUS.setMessage("Markov Clustering Complete");
//		} catch (Exception e) {
//
//			e.printStackTrace();
//		}
		// System.out.println(curPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub

	}

}
