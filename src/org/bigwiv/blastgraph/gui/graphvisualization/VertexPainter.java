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
