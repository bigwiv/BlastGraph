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
