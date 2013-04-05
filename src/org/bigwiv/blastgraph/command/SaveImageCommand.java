package org.bigwiv.blastgraph.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;
import org.bigwiv.blastgraph.io.GraphIOUtils;


import edu.uci.ics.jung.visualization.VisualizationViewer;

public class SaveImageCommand extends Command {

	private VisualizationViewer<HitVertex, ValueEdge> vv;
	private SelectedFile selectedFile;
	
	public SaveImageCommand(VisualizationViewer<HitVertex, ValueEdge> vv, SelectedFile selectedFile) {
		this.vv = vv;
		this.selectedFile = selectedFile;
		this.commandName = "SaveImage";
	}

	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		File imageFile = selectedFile.getFile();
		FileFilter fileFilter = selectedFile.getFileFilter();

		try {
			if(fileFilter.equals(FileChooseTools.PNG_FILTER)){
				GraphIOUtils.saveImage(imageFile, "png", vv);
			}else if (fileFilter.equals(FileChooseTools.JPG_FILTER)) {
				GraphIOUtils.saveImage(imageFile, "jpg", vv);
			}else {
				GraphIOUtils.saveImage(imageFile, "gif", vv);
			}
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
