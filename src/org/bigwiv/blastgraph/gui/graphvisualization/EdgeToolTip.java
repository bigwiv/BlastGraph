package org.bigwiv.blastgraph.gui.graphvisualization;

import org.apache.commons.collections15.Transformer;
import org.bigwiv.blastgraph.ValueEdge;


public class EdgeToolTip<E> implements Transformer<ValueEdge, String> {
	
	@Override
	public String transform(ValueEdge ve) {
		return "<html><p>e-value: " + ve.getExpectValue() + "</p>"
				+ "<p>score: " + ve.getScore() + "</p>" + "<p>numSubhit: "
				+ ve.getNumSubHit() + "</p></html>";
	}

}
