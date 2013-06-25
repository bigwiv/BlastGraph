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
