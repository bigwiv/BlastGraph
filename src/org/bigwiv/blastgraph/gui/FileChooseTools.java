package org.bigwiv.blastgraph.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.bigwiv.blastgraph.global.Global;


public final class FileChooseTools {
	public final static SimpleFileFilter GML_FILTER = new SimpleFileFilter(
			"Graph File", "gml");
	public final static SimpleFileFilter BLAST_XML_FILTER = new SimpleFileFilter(
			"Blast XML File", "xml");
	public final static FileFilter FASTA_FILTER = new SimpleFileFilter(
			"Fasta File", "fasta");

	public final static SimpleFileFilter CSV_FILTER = new SimpleFileFilter(
			"CSV table file", "csv");

	// other file Filters
	public final static SimpleFileFilter NT_FILTER = new SimpleFileFilter(
			"Network Table File", "ntf");
	public final static SimpleFileFilter SP_FILTER = new SimpleFileFilter(
			"Single Points File", "spf");
	public final static SimpleFileFilter CF_FILTER = new SimpleFileFilter(
			"Cluster File", "cf");

	// Image Filters
	public final static SimpleFileFilter PNG_FILTER = new SimpleFileFilter(
			"PNG format image", "png");
	public final static SimpleFileFilter GIF_FILTER = new SimpleFileFilter(
			"GIF format image", "gif");
	public final static SimpleFileFilter JPG_FILTER = new SimpleFileFilter(
			"JPG format image", "jpg");
	
	// tree filter
	public final static SimpleFileFilter NWK_FILTER = new SimpleFileFilter(
			"newick format tree", "nwk");

	public final static SelectedFile saveFile(JFrame parentFrame,
			String dialogTitle, ArrayList<FileFilter> filterList) {
		return getFile(parentFrame, dialogTitle, JFileChooser.SAVE_DIALOG,
				filterList);
	}

	public final static SelectedFile openFile(JFrame parentFrame,
			String dialogTitle, ArrayList<FileFilter> filterList) {
		return getFile(parentFrame, dialogTitle, JFileChooser.OPEN_DIALOG,
				filterList);
	}

	private final static SelectedFile getFile(JFrame parentFrame,
			String dialogTitle, int dialogType, ArrayList<FileFilter> filterList) {
		JFileChooser fileChooser = new JFileChooser();

		String lastPath = Global.PREFERENCES.get("LAST_PATH", "");

		if (!lastPath.equals("")) {
			fileChooser.setCurrentDirectory(new File(lastPath));
		}

		fileChooser.setDialogTitle(dialogTitle);
		fileChooser.setDialogType(dialogType);

		for (FileFilter fileFilter : filterList) {
			fileChooser.addChoosableFileFilter(fileFilter);
		}
		
		fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[1]);
		// set selected FileFilter

		int c = fileChooser.showDialog(parentFrame, null);

		if (c != JFileChooser.APPROVE_OPTION) {
			return null;
		} else {
			SimpleFileFilter fileFilter = null;
			File file = fileChooser.getSelectedFile();
			File newFile;
			
			if(fileChooser.getFileFilter().getClass() == SimpleFileFilter.class){
				fileFilter = (SimpleFileFilter) fileChooser
						.getFileFilter();
				
				if (file.getAbsolutePath().toLowerCase()
						.endsWith(fileFilter.getExtension())) {
					newFile = file;
				} else {
					newFile = new File(file.getAbsolutePath() + "."
							+ fileFilter.getExtension());
				}
			}else {
				newFile = file;
			}

			Global.PREFERENCES.put("LAST_PATH", file.getPath());
			return new SelectedFile(newFile, fileFilter);
		}
	}

	public final static FileFilter getFileFilter(final String description,
			final String extension) {
		return new FileFilter() {

			@Override
			public String getDescription() {

				return description + " (*." + extension.toLowerCase() + ")";
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				String ext = null;
				String s = f.getName();
				int i = s.lastIndexOf('.');

				if (i > 0 && i < s.length() - 1) {
					ext = s.substring(i + 1).toLowerCase();
				}

				if (ext != null) {
					if (ext.equals(extension)) {
						return true;
					} else {
						return false;
					}
				}

				return false;
			}

			public String getExtension() {
				return extension.toLowerCase();
			}
		};

	}

	public static final class SimpleFileFilter extends FileFilter {
		private String description;
		private String extension;

		public SimpleFileFilter(String description, String extension) {
			this.description = description;
			this.extension = extension.toLowerCase();
		}

		@Override
		public String getDescription() {

			return description + " (*." + extension + ")";
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}

			if (ext != null) {
				if (ext.equals(extension)) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		public String getExtension() {
			return extension;
		}
	}
}
