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
package org.bigwiv.blastgraph.command;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 * Generate weighted graph from evalue and run MCL
 * 
 * @author yeyanbo
 * 
 */
public class MclCommand extends Command {
	// MCL inflation parameter
	private double inflation;

	// Weighting types
	public static int EVALUE_WEIGHT = 0;
	public static int SCORE_WEIGHT = 1;
	public static int SCORE_DENSITY_WEIGHT = 2;

	// weighting parameter
	private int weightType;

	// for evalue weighting, larger evalue is low, smaller evalue is high.
	private double lowCutoff, highCutoff;

	/**
	 * inflation affect the granularity of clusters default is 2
	 * 
	 * @param inflation
	 */
	public MclCommand() {
		this.inflation = 2;
		this.weightType = 0;
		this.lowCutoff = 1;
		this.highCutoff = 1E-200;
	}

	/**
	 * @return the inflation
	 */
	public double getInflation() {
		return inflation;
	}

	/**
	 * @return the weightType
	 */
	public int getWeightType() {
		return weightType;
	}

	/**
	 * @return the lowCutoff
	 */
	public double getLowCutoff() {
		return lowCutoff;
	}

	/**
	 * @return the highCutoff
	 */
	public double getHighCutoff() {
		return highCutoff;
	}

	/**
	 * @param inflation
	 *            the inflation to set
	 */
	public void setInflation(double inflation) {
		this.inflation = inflation;
	}

	/**
	 * @param weightType
	 *            the weightType to set
	 */
	public void setWeightType(int weightType) {
		this.weightType = weightType;
	}

	/**
	 * @param lowCutoff
	 *            the lowCutoff to set
	 */
	public void setLowCutoff(double lowCutoff) {
		this.lowCutoff = lowCutoff;
	}

	/**
	 * @param highCutoff
	 *            the highCutoff to set
	 */
	public void setHighCutoff(double highCutoff) {
		this.highCutoff = highCutoff;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		String curPath = Global.getAppPath(this.getClass());
		File mclGraphFile = new File(curPath + File.separator
				+ "mcl_graph_file");
		File mclClustFile = new File(curPath + File.separator
				+ "out.mcl_graph_file.I"
				+ ("" + inflation).replace(".", "").substring(0, 2));
		// System.out.println(mclClustFile);
		try {
			ArrayList<ValueEdge> edges = new ArrayList<ValueEdge>(
					Global.graph.getEdges());

			BufferedWriter bw;

			bw = new BufferedWriter(new FileWriter(mclGraphFile));

			Global.WORK_STATUS.setMessage("");
			Global.WORK_STATUS
					.setMessage("Stage1: Generating Tabular Format...");

			for (ValueEdge valueEdge : edges) {
				Pair<HitVertex> pair = Global.graph.getEndpoints(valueEdge);
				HitVertex hv1 = pair.getFirst();
				HitVertex hv2 = pair.getSecond();

				double weight = 0;

				if (weightType == EVALUE_WEIGHT) {
					
					double evalue = valueEdge.getExpectValue();
					
					if(lowCutoff < highCutoff) return;
					
					if (evalue >= lowCutoff) {
						weight = 0;
					} else if (evalue < highCutoff) {
						if(lowCutoff > 1){
							weight = -Math.log10(highCutoff/lowCutoff);
						}else {
							weight = -Math.log10(highCutoff);
						}
					} else {
						if(lowCutoff > 1){
							weight = -Math.log10(evalue/lowCutoff);
						}else {
							weight = -Math.log10(evalue);
						}
					}

				} else if (weightType == SCORE_WEIGHT) {
					double score = valueEdge.getScore();
					
					if(lowCutoff > highCutoff) return;
					
					if (score <= lowCutoff) {
						weight = 0;
					} else if (score > highCutoff) {
						weight = highCutoff;
					} else {
						weight = score;
					}
				} else if (weightType == SCORE_DENSITY_WEIGHT) {
					double scoreDensity = Global.graph
							.getScoreDensity(valueEdge);
					
					if(lowCutoff > highCutoff) return;
					
					if (scoreDensity <= lowCutoff) {
						weight = 0;
					} else if (scoreDensity > highCutoff) {
						weight = highCutoff;
					} else {
						weight = scoreDensity;
					}
				}

				bw.write(hv1.getId() + "\t" + hv2.getId() + "\t" + weight
						+ "\n");
			}

			// store single vertices
			for (HitVertex hv : Global.graph.getVertices()) {
				if (Global.graph.getInEdges(hv).size() == 0)
					bw.write(hv.getId() + "\t" + hv.getId() + "\t" + 10 + "\n");
			}

			bw.close();
			Global.WORK_STATUS.setMessage("Stage1 Complete");
			Global.WORK_STATUS.setMessage("");

			String mclCmd;
			String cmdPath = Global.SETTING.get("MCL_PATH");
			if (cmdPath != null && (new File(cmdPath).isDirectory())) {
				mclCmd = cmdPath + File.separator + "mcl";
			} else {
				mclCmd = "mcl";
			}

			ProcessBuilder pb = new ProcessBuilder(mclCmd, "mcl_graph_file",
					"--abc", "-I", "" + inflation);
			// System.out.println(pb.command().toString());
			pb.redirectErrorStream(true);
			pb.directory(new File(curPath));
			Process p = pb.start();
			Global.WORK_STATUS.setMessage("Stage2: Markov Clustering...");
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
			String lineStr;

			while ((lineStr = inBr.readLine()) != null) {
				// System.out.println(lineStr);
				Global.WORK_STATUS.setMessage(lineStr);
			}

			if (p.waitFor() != 0) {
				if (p.exitValue() == 1)
					// System.err.println("Failed to run mcl!");
					Global.WORK_STATUS.setError("Failed to run mcl!");
			}

			inBr.close();
			in.close();
			Global.WORK_STATUS.setMessage("Stage2 Complete");
			
			mclGraphFile.delete();
			
			Global.WORK_STATUS.setMessage("");

			Global.WORK_STATUS.setMessage("Stage3: Regenerating SubGraphs...");
			BufferedReader br = new BufferedReader(new FileReader(mclClustFile));
			String line = br.readLine();

			BlastGraph<HitVertex, ValueEdge> tempGraph = new BlastGraph<HitVertex, ValueEdge>();
			int count = 0;
			while (line != null && !line.equals("")) {
				String[] ids = line.split("\t");
				// set to store vertices
				Set<HitVertex> hvs2 = new HashSet<HitVertex>();

				// get and add vertices into hvs by GI in ids
				for (int i = 0; i < ids.length; i++) {
					hvs2.add(Global.graph.getVertex(ids[i]));
				}

				tempGraph.union(FilterUtils.createInducedSubgraph(hvs2,
						Global.graph));

				line = br.readLine();
				Global.WORK_STATUS.setMessage("Subgraph: " + (++count));
			}

			br.close();

			mclClustFile.delete();
			
			Global.graph.copy(tempGraph);

			Global.WORK_STATUS.setMessage("Stage3 Complete");
			Global.WORK_STATUS.setMessage("");
			Global.WORK_STATUS.setMessage("Markov Clustering Complete");
			// System.out.println(curPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
