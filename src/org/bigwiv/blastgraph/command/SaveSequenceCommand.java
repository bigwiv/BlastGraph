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
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;
import org.bigwiv.blastgraph.io.SequenceTool;
import org.biojava.bio.BioException;


public class SaveSequenceCommand extends Command {
	private Set<String> accList;
	
	private String db;
	
	private String type;
	
	private SelectedFile selectedFile;
	/**
	 * 
	 * @param accList
	 * @param db "nuccore" for nuceleotide, "protein" for protein
	 * @param type "fasta" for FASTA, "gb" or "gp" for GenBank file
	 * @param selectedFile
	 */
	public SaveSequenceCommand(Set<String> accList,  String db, String type, SelectedFile selectedFile){
		this.accList = accList;
		this.db = db;
		this.type = type;
		this.selectedFile = selectedFile;
		this.commandName = "SaveFasta";
	}
	
	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		
		File targetFasta = selectedFile.getFile();

		try {
			Global.WORK_STATUS.setMessage("Saving " + accList.size() + " picked sequences...");
			SequenceTool.getSequenceFromNCBI(accList, db, type, targetFasta);
			Global.WORK_STATUS.setMessage("Saving Completed.");
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
