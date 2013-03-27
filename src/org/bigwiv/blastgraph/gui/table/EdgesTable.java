package org.bigwiv.blastgraph.gui.table;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.gui.graphvisualization.CollectionChangeListener;


public class EdgesTable extends JTable implements
		CollectionChangeListener<ValueEdge> {

	public EdgesTable() {
		super();
		this.setModel(new DefaultTableModel());
		this.setAutoCreateRowSorter(true);
	}

	@Override
	public void onCollectionChange(Set<ValueEdge> set) {
		SimpleTableModel eModel = null;
		Object[] rowValues = null;
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			ValueEdge valueEdge = (ValueEdge) iterator.next();

			if (eModel == null) {
				eModel = new SimpleTableModel(){
					@Override
					public boolean isCellEditable(int rowIndex, int columnIndex) {
				        return false;
				    }
				};
				eModel.addColumn("expectValue");
				eModel.addColumn("score");
				eModel.addColumn("numSubHit");
				eModel.addColumn("queryCoverage");
				eModel.addColumn("subjectCoverage");
				eModel.addColumn("firstHSPIdentity");

				rowValues = new Object[6];
			}

			DecimalFormat formatter = new DecimalFormat("0.##E0");
			rowValues[0] = formatter.format(valueEdge.getExpectValue());
			rowValues[1] = valueEdge.getScore();
			rowValues[2] = valueEdge.getNumSubHit();
			rowValues[3] = valueEdge.getQueryCoverage();
			rowValues[4] = valueEdge.getSubjectCoverage();
			rowValues[5] = valueEdge.getSubHit()[0].getPercentageIdentity();

			eModel.addRow(rowValues);
		}

		if (eModel != null) {
			this.setModel(eModel);
			this.updateUI();
		}
	}

}
