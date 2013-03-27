/**
 * 
 */
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
import org.bigwiv.blastgraph.io.GraphIOUtils;


/**
 * Import extra info into graph
 * 
 * @author yeyanbo
 * 
 */
public class ImportVertexAttrCommand extends Command {

	private SelectedFile selectedFile;
	
	/**
	 * 
	 */
	public ImportVertexAttrCommand(SelectedFile selectedFile) {
		this.selectedFile = selectedFile;
		this.commandName = "ImportVertexAttr";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		File file = selectedFile.getFile();

		try {
			GraphIOUtils.importVerticesInfo(Global.graph, file);
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
