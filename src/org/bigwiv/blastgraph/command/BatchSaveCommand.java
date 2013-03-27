package org.bigwiv.blastgraph.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;
import org.bigwiv.blastgraph.gui.FileChooseTools.SimpleFileFilter;
import org.bigwiv.blastgraph.io.GraphIOUtils;

import edu.uci.ics.jung.algorithms.layout.Layout;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class BatchSaveCommand extends Command {
	private ArrayList<Set<HitVertex>> subSetList;
	private VisualizationViewer<HitVertex, ValueEdge> vv;
	private SelectedFile selectedFile;

	/**
	 * @param subSetList
	 * @param vv
	 */
	public BatchSaveCommand(ArrayList<Set<HitVertex>> subSetList,
			VisualizationViewer<HitVertex, ValueEdge> vv,
			SelectedFile selectedFile) {
		super();
		this.isCancelable = true;
		this.subSetList = subSetList;
		this.vv = vv;
		this.selectedFile = selectedFile;
		this.commandName = "BatchSave";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		SimpleFileFilter fileFilter = (SimpleFileFilter) selectedFile
				.getFileFilter();
		String pathRoot = selectedFile.getFile().getPath()
				.replaceAll("\\." + fileFilter.getExtension(), "");

		File file;

		try {
			if (fileFilter.equals(FileChooseTools.GML_FILTER)) {
				Global.WORK_STATUS.setMessage("Saving All SubGraphs");
				for (int i = 0; i < subSetList.size(); i++) {
					// break the cycle to stop the command
					if (blinker == null) {
						Global.WORK_STATUS.setMessage("Batch Saving Stoped");
						return;
					}
					Set<HitVertex> subSet = subSetList.get(i);
					file = new File(pathRoot + (i + 1) + "_" + subSet.size()
							+ ".gml");

					BlastGraph<HitVertex, ValueEdge> tmpGraph = FilterUtils
							.createInducedSubgraph(subSet, Global.graph);
					GraphIOUtils.toGraphML(tmpGraph, file);
					Global.WORK_STATUS.setMessage("SubGraph" + (i + 1)
							+ " Completed...");
				}
				Global.WORK_STATUS
						.setMessage("SubGraph Batch Saving Completed");
			} else if (fileFilter.equals(FileChooseTools.CSV_FILTER)) {
				Global.WORK_STATUS
						.setMessage("Saving All SubGraph Vertex Info");
				for (int i = 0; i < subSetList.size(); i++) {
					if (blinker == null) {
						Global.WORK_STATUS.setMessage("Batch Saving Stoped");
						return;
					}
					Set<HitVertex> subSet = subSetList.get(i);
					file = new File(pathRoot + (i + 1) + "_" + subSet.size()
							+ ".csv");

					GraphIOUtils.writeDescription(subSet, file);
					Global.WORK_STATUS.setMessage("SubGraph" + (i + 1)
							+ " Completed...");
				}

			} else if (fileFilter.equals(FileChooseTools.PNG_FILTER)) {
				File img;
				Global.WORK_STATUS.setMessage("Saving All SubGraph Images");
				for (int i = 0; i < subSetList.size(); i++) {
					if (blinker == null) {
						Global.WORK_STATUS.setMessage("Batch Saving Stoped");
						return;
					}
					try {
						Set<HitVertex> subSet = subSetList.get(i);

						img = new File(pathRoot + (i + 1) + "_" + subSet.size()
								+ ".png");
						BlastGraph<HitVertex, ValueEdge> curSubGraph = FilterUtils
								.createInducedSubgraph(subSet, Global.graph);

						Layout layout = vv.getGraphLayout();
						layout.setGraph(curSubGraph);
						vv.setGraphLayout(layout);
						int time = curSubGraph.getVertexCount() * 12
								+ curSubGraph.getEdgeCount() / 10;
						//System.out.println(time);
						Thread.sleep(time);
						GraphIOUtils.saveImage(img, "png", vv);
						Global.WORK_STATUS.setMessage("SubGraph" + (i + 1)
								+ " Completed...");
					} catch (Exception e) {
						e.printStackTrace();
						Global.WORK_STATUS.setError("SubGraph" + (i + 1)
								+ " failed...");
					}
				}
			}
			Global.WORK_STATUS.setMessage("Saving Completed");
		} catch (Exception e) {
			Global.APP_FRAME_PROXY.showError(e.getMessage());
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub
		
	}

}
