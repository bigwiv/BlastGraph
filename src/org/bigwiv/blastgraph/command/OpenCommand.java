package org.bigwiv.blastgraph.command;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import org.bigwiv.bio.sax.blastxml.BlastXMLParserFacade;
import org.bigwiv.bio.ssbind.SeqSimilarityAdapter;
import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;
import org.bigwiv.blastgraph.io.BlastToGraphHandler;
import org.bigwiv.blastgraph.io.GraphIOUtils;
import org.xml.sax.InputSource;


/**
 * Open a GML file and populate a BlastGraph object
 * 
 * @author yeyanbo
 * 
 */
public class OpenCommand extends Command {
	
	private SelectedFile selectedFile;

	public OpenCommand(SelectedFile selectedFile) {
		this.selectedFile = selectedFile;
		this.commandName = "Open";
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		File file = selectedFile.getFile();
		
		Global.PREFERENCES.put("CURRENT_FILE", file.getAbsolutePath());
		
		try {
			// read graph file
			Global.WORK_STATUS.setMessage("Reading graph from \"" + file
					+ "\"...");
			Global.graph.readGraph(file);
			Global.WORK_STATUS.setMessage("Reading Completed");
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
