package org.bigwiv.blastgraph.gui.graphvisualization;

import java.awt.Color;
import java.awt.Paint;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections15.Transformer;
import org.bigwiv.blastgraph.HitVertex;


public class VertexPainter implements Transformer<HitVertex, Paint> {
	private Map<String, Color> colorMap;
	private int index;

	public VertexPainter() {
		this.index = 0;
		colorMap = null;
	}

	public VertexPainter(int index, Map<String, Color> colorMap) {
		this.index = index;
		this.colorMap = colorMap;
	}

	@Override
	public Paint transform(HitVertex hv) {
		String attrValue;
		if (index != 0) {
			switch (index) {
			case 1:
				attrValue = hv.getIndex() + "";
				break;
			case 2:
				attrValue = hv.getStrand() + "";
				break;
			case 3:
				attrValue = hv.getGenomeAcc();
				break;
			case 4:
				attrValue = hv.getOrganism();
				break;
			default:
				attrValue = hv.getTaxon().getTaxon(index - 5);
				break;
			}
			return colorMap.get(attrValue);
		} else {
			return Color.red;
		}
	}
}
