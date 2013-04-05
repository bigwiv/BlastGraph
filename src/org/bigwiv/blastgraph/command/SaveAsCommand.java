package org.bigwiv.blastgraph.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;
import org.bigwiv.blastgraph.io.GraphIOUtils;


public class SaveAsCommand extends Command {
	 /**
     * BlastGraph to be saved
     */
    private BlastGraph<HitVertex, ValueEdge> blastGraph;
    
    private SelectedFile selectedFile;
	
    /**
	 * @param blastGraph
	 */
	public SaveAsCommand(BlastGraph<HitVertex, ValueEdge> blastGraph, SelectedFile selectedFile) {
		super();
		this.blastGraph = blastGraph;
		this.selectedFile = selectedFile;
		this.commandName = "Save As";
	}
	
	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		File file = selectedFile.getFile();

		try {
			Global.WORK_STATUS.setMessage("Saving graph...");
			blastGraph.save(file);
			Global.WORK_STATUS.setMessage("Saving Complete: "
					+ blastGraph.getVertexCount() + " vertices "
					+ blastGraph.getEdgeCount() + " edges");
		} catch (IOException e) {
			Global.APP_FRAME_PROXY.showError(e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub
		
	}
}
