/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;


/**
 * Open a GML file and append to a BlastGraph object
 * 
 * @author yeyanbo
 * 
 */
public class AppendCommand extends Command {

	private SelectedFile selectedFile;
	
	public AppendCommand(SelectedFile selectedFile) {
		this.selectedFile = selectedFile;
		this.commandName = "Append";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		BlastGraph<HitVertex, ValueEdge> importGraph = new BlastGraph<HitVertex, ValueEdge>();
		
		File file = selectedFile.getFile();

		try {
			// read graph file
			Global.WORK_STATUS.setMessage("Reading graph from \"" + file
					+ "\"...");
			importGraph.readGraph(file);
		} catch (Exception e) {
			Global.APP_FRAME_PROXY.showError(e.getMessage());
			e.printStackTrace();
		}

//		// set color of importGraph
//		Color blueColor = Color.blue;
//		for (HitVertex hv : importGraph.getVertices()) {
//			if (!blastGraph.containsVertex(hv)) {
//				hv.putAttribute("color",
//						blueColor.getRed() + "," + blueColor.getGreen() + ","
//								+ blueColor.getBlue());
//			}
//		}

		Global.WORK_STATUS.setMessage("Union two graphs...");
		Global.graph.union(importGraph);
		Global.WORK_STATUS.setMessage("Appending Completed");
		
	}

	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub
		
	}

}
