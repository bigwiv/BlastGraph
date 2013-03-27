package org.bigwiv.blastgraph.command;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;


public class NewCommand extends Command {
	
	private SelectedFile selectedFile;

	/**
	 * 
	 * @param blastGraph
	 * @param selectedFile
	 */
	public NewCommand(SelectedFile selectedFile) {
		this.selectedFile = selectedFile;
		this.commandName = "Create";
	}

	@Override
	public void concreteExecute() {
		
		File file = selectedFile.getFile();

		Global.PREFERENCES.put("CURRENT_FILE", file.getAbsolutePath() + ".gml");
		
		try {
			Global.graph.createFromBlast(file);
		} catch (Exception e) {
			Global.APP_FRAME_PROXY.showError("Bad file format, "
					+ e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub
		
	}

}
