/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;
import org.bigwiv.blastgraph.io.GraphIOUtils;


/**
 * @author yeyanbo
 * 
 */
public class ExportVertexAttrCommand extends Command {

	private Collection<HitVertex> vertexs;
	
	private SelectedFile selectedFile;

	/**
	 * 
	 */
	public ExportVertexAttrCommand(Collection<HitVertex> vertexs, SelectedFile selectedFile) {
		this.vertexs = vertexs;
		this.selectedFile = selectedFile;
		this.commandName = "ExportVertexAttr";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		File file = selectedFile.getFile();

		try {
			GraphIOUtils.writeDescription(vertexs, file);
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
