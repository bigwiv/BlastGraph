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

package org.bigwiv.blastgraph.gui.table;

import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class GeneContentTable extends JTable implements ChangeListener,
		PropertyChangeListener {
	private JTable mainTable;
	private GeneContentModel geneContentModel;

	public GeneContentTable(JTable table) {
		mainTable = table;
		mainTable.addPropertyChangeListener(this);
		geneContentModel = (GeneContentModel) this.mainTable.getModel();
		setFocusable(false);
		setAutoCreateColumnsFromModel(false);
		setModel(mainTable.getModel());
		setSelectionModel(mainTable.getSelectionModel());

		TableColumn column = new TableColumn();
		column.setHeaderValue(" ");
		addColumn(column);
		column.setCellRenderer(new RowHeaderRenderer());
		int width = this.getRowHeaderWidth();
		getColumnModel().getColumn(0).setPreferredWidth(width * 7);
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	private int getRowHeaderWidth() {
		int max = 0;
		Vector<String> rowHeader = geneContentModel.getRowHeader();
		for (String string : rowHeader) {
			int length = string.length();
			max = max > length ? max : length;
		}

		return max;
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Component c = getParent();

		if (c instanceof JViewport) {
			JViewport viewport = (JViewport) c;
			viewport.addChangeListener(this);
		}
	}

	/*
	 * Delegate method to mainTable table
	 */
	@Override
	public int getRowCount() {
		return mainTable.getRowCount();
	}

	@Override
	public int getRowHeight(int row) {
		return mainTable.getRowHeight(row);
	}

	@Override
	public Object getValueAt(int row, int column) {
		;
		return geneContentModel.getRowName(row);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void stateChanged(ChangeEvent e) {
		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane) viewport.getParent();
		scrollPane.getVerticalScrollBar()
				.setValue(viewport.getViewPosition().y);
	}

	public void propertyChange(PropertyChangeEvent e) {
		// Keep the row table in sync with the mainTable table

		if ("rowHeight".equals(e.getPropertyName())) {
			updateRowHeight();
		}

		if ("selectionModel".equals(e.getPropertyName())) {
			updateSelectionModel();
		}

		if ("model".equals(e.getPropertyName())) {
			updateModel();
		}
	}

	private void updateRowHeight() {
		setRowHeight(mainTable.getRowHeight());
	}

	private void updateModel() {
		setModel(mainTable.getModel());
	}

	private void updateSelectionModel() {
		setSelectionModel(mainTable.getSelectionModel());
	}

	private static class RowHeaderRenderer extends DefaultTableCellRenderer {
		public RowHeaderRenderer() {
			setHorizontalAlignment(JLabel.LEFT);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();

				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			if (isSelected) {
				setFont(getFont().deriveFont(Font.BOLD));
			}

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return this;
		}
	}
}
