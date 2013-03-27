package org.bigwiv.blastgraph.gui.table;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.graphvisualization.CollectionChangeListener;


public class VerticesTable extends JTable implements
		CollectionChangeListener<HitVertex> {

	public VerticesTable() {
		super();
		this.setModel(new DefaultTableModel());
		this.setAutoCreateRowSorter(true);
	}

	@Override
	public void onCollectionChange(Set<HitVertex> set) {
		SimpleTableModel vModel = null;
		// Object [] rowValues = null;

		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			HitVertex hitVertex = (HitVertex) iterator.next();
			Map attrs = hitVertex.getAllAttributes();

			if (vModel == null) {
				vModel = new SimpleTableModel(){
//					@Override
//					public boolean isCellEditable(int rowIndex, int columnIndex) {
//				        if (columnIndex == 9) {
//				            return true;
//				        }
//				        return false;
//				    }
				};
				vModel.addColumn("gi");
				vModel.addColumn("accession");
				vModel.addColumn("length");
				vModel.addColumn("strand");
				vModel.addColumn("index");
				vModel.addColumn("location");
				vModel.addColumn("genomeAcc");
				vModel.addColumn("taxon");
				vModel.addColumn("organism");
				vModel.addColumn("description");

				for (Iterator iterator2 = attrs.keySet().iterator(); iterator2
						.hasNext();) {
					String string = (String) iterator2.next();
					vModel.addColumn(string);
				}
			}
			// rowValues = new Object[4 + attrs.keySet().size()];

			vModel.addRow("");
			int rowIndex = vModel.getRowCount() - 1;
			vModel.setValueAt(hitVertex.getId(), rowIndex, 0);
			vModel.setValueAt(hitVertex.getAccession(), rowIndex, 1);
			vModel.setValueAt(hitVertex.getLength(), rowIndex, 2);
			vModel.setValueAt(hitVertex.getStrand(), rowIndex, 3);
			vModel.setValueAt(hitVertex.getIndex(), rowIndex, 4);
			vModel.setValueAt(hitVertex.getLocation(), rowIndex, 5);
			vModel.setValueAt(hitVertex.getGenomeAcc(), rowIndex, 6);
			vModel.setValueAt(hitVertex.getTaxon(), rowIndex, 7);
			vModel.setValueAt(hitVertex.getOrganism(), rowIndex, 8);
			vModel.setValueAt(hitVertex.getDescription(), rowIndex, 9);

			for (Iterator iterator2 = attrs.keySet().iterator(); iterator2
					.hasNext();) {
				String key = (String) iterator2.next();
				if (!vModel.containsColumn(key)) {
					vModel.addColumn(key, "");
				}
				int colIndex = vModel.getColumnIndex(key);
				vModel.setValueAt(attrs.get(key), rowIndex, colIndex);
			}

			/*
			 * //rowValues[0] = hitVertex.getId(); //rowValues[1] =
			 * hitVertex.getAccession(); //rowValues[2] = hitVertex.getLength();
			 * //rowValues[3] = hitVertex.getDescription(); int i = 4; for
			 * (Iterator iterator3 = attrs.keySet().iterator(); iterator3
			 * .hasNext();){ String string = (String) iterator3.next();
			 * rowValues[i] = attrs.get(string); i++; }
			 * vModel.addRow(rowValues);
			 */
		}
		
		if (vModel != null) {
			vModel.addTableModelListener(new TableModelListener() {
				
				@Override
				public void tableChanged(TableModelEvent e) {
					int row = e.getFirstRow();
					int column = e.getColumn();

					if (column == 9) {
						SimpleTableModel model = (SimpleTableModel) e.getSource();
						String columnName = model.getColumnName(column);
						
						String description = (String)model.getValueAt(row, column);
						String gi = (String) model.getValueAt(row, 0);
						
						Global.graph.getVertex(gi).setDescription(description);
						
						Global.WORK_STATUS.setMessage("Set vertex \"" + gi + "\" description to \"" + description + "\"");
					}
				}
			});
			this.setModel(vModel);
			this.updateUI();
		}
	}
}