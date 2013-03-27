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
