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
