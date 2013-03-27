package org.bigwiv.blastgraph.gui.table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.biojava.bio.seq.impl.NewAssembledSymbolList;

public class SimpleTableModel extends AbstractTableModel {

	private Vector<Vector<Object>> content = null;
	private Vector<String> headerName = null;

	public SimpleTableModel() {
		super();
        headerName = new Vector<String>();
        content = new Vector<Vector<Object>>();
	}
	
	public SimpleTableModel(int count){
		super();
        headerName = new Vector<String>();
        content = new Vector<Vector<Object>>(count);
	}
	
	public void addColumn(String name) {
		headerName.add(name);
		for (int i = 0; i < content.size(); i++) {
			content.get(i).add(new Object());
		}
	}
	
	/**
	 * @param name column name
	 * @param object default value of this column
	 */
	public void addColumn(String name, Object object) {
		headerName.add(name);
		for (int i = 0; i < content.size(); i++) {
			content.get(i).add(object);
		}
	}
	
	public int getColumnIndex(String name){
		return headerName.indexOf(name);
	}
	
	public boolean containsColumn(String name){
		return headerName.contains(name);
	}
	
	public void addRow(Object args[]) throws IllegalArgumentException {
		if (args.length != headerName.size()) {
			throw new IllegalArgumentException("Incorrect array length: should be" + headerName.size());
		}else {
	        Vector<Object> v = new Vector<Object>();
	        for (int i = 0; i < args.length; i++) {
				v.add(args[i]);
			}
	        content.add(v);
		}
	}
	
	public void addRow() {
		Vector<Object> v = new Vector<Object>();
		for (int i = 0; i < headerName.size(); i++) {
			v.add(new Object());
		}
		content.add(v);
	}
	
	/**
	 * 
	 * @param object default value of this row
	 */
	public void addRow(Object object) {
		Vector<Object> v = new Vector<Object>();
		for (int i = 0; i < headerName.size(); i++) {
			v.add(object);
		}
		content.add(v);
	}
	
	public void removeRow(){
		
	}
	
	public void removeRows(){
		
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        }
        return true;
    }
	
	@Override
	public void setValueAt(Object value, int row, int col) {
        ((Vector) content.get(row)).set(col, value);
        this.fireTableCellUpdated(row, col);
    }
	
	@Override
	public String getColumnName(int col) {
		return headerName.get(col);
	}

	@Override
	public int getColumnCount() {
		return headerName.size();
	}

	@Override
	public int getRowCount() {
        return content.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector) content.get(rowIndex)).get(columnIndex);
	}

	@Override
	public Class getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	}

	public void writeTo(File file) throws IOException{

		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		String tmpString = "";
		for (int i = 0; i < headerName.size(); i++) {
			tmpString = tmpString + headerName.get(i) + ",";
		}
		tmpString = tmpString.substring(0, tmpString.length() - 1);
		
		br.write(tmpString + "\n");
		
		for (int i = 0; i < content.size(); i++) {
			tmpString = "";
			for (int j = 0; j < headerName.size(); j++) {
				tmpString = tmpString + getValueAt(i, j) + ",";
			}
			tmpString = tmpString.substring(0, tmpString.length() - 1);
			br.write(tmpString + "\n");
		}
		
		br.close();
	}
	
}
