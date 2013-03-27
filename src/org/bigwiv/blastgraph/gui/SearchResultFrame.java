/**
 * 
 */
package org.bigwiv.blastgraph.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.bigwiv.blastgraph.gui.table.SearchResultModel;


/**
 * @author yeyanbo
 *
 */
public class SearchResultFrame extends JFrame {
	private SearchResultModel model;
	private JTable searchResultTable;
	
	public SearchResultFrame(SearchResultModel model) {
		super();
		this.setTitle("Search Result");
		this.model = model;
		initComponents();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void initComponents(){
		searchResultTable = new JTable();
		searchResultTable.setAutoCreateRowSorter(true);
		searchResultTable.setModel(model);
		JScrollPane tablePane = new JScrollPane(searchResultTable);
		this.add(tablePane);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((screen.width * 2) / 3, (screen.height * 9) / 10);
		setLocation(screen.width / 6, screen.height / 16);
	}
	
}
