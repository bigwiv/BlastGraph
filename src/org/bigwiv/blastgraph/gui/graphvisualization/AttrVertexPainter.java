package org.bigwiv.blastgraph.gui.graphvisualization;

import java.awt.Color;
import java.awt.Paint;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.bigwiv.blastgraph.HitVertex;


public class AttrVertexPainter implements Transformer<HitVertex, Paint> {
	private Map<String, Color> colorMap;
	private String attribute;
	
	public AttrVertexPainter() {
		this.attribute = null;
		colorMap = null;
	}
	
	public AttrVertexPainter(String attribute, Map<String, Color> colorMap){
		this.attribute = attribute;
		this.colorMap = colorMap;
	}

	@Override
	public Paint transform(HitVertex hv) {
		if (attribute != null) {
			String attrValue = hv.getAttribute(attribute);
			return colorMap.get(attrValue);
		}else {
			return Color.red;
		}
	}
}
