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

package org.bigwiv.blastgraph.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Store selected File and FileFilter 
 * @author yeyanbo
 *
 */

public class SelectedFile{
	private File file;
	private FileFilter fileFilter;
	
	/**
	 * @param file
	 * @param fileFilter
	 */
	public SelectedFile(File file, FileFilter fileFilter) {
		super();
		this.file = file;
		this.fileFilter = fileFilter;
	}
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * @return the fileFilter
	 */
	public FileFilter getFileFilter() {
		return fileFilter;
	}
	/**
	 * @param fileFilter the fileFilter to set
	 */
	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}
	
}
