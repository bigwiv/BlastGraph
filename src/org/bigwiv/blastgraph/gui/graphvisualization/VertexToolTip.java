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
package org.bigwiv.blastgraph.gui.graphvisualization;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.bigwiv.blastgraph.HitVertex;


/**
 * @author yeyanbo
 *
 */
public class VertexToolTip<V> implements Transformer<HitVertex, String> {

	@Override
	public String transform(HitVertex hp) {
		String tipString = "<html><p><b>BasicAttrs:</b></p><p>gi: "
				+ hp.getId() + "</p>" + "<p>accession: "
				+ hp.getAccession() + "</p>" + "<p>length: "
				+ hp.getLength() + "</p>" + "<p>description: "
				+ hp.getDescription() + "</p>" + "<p>organism: "
				+ hp.getOrganism() + "</p>";
		Map<String, String> attrs = hp.getAllAttributes();
		if (!attrs.isEmpty()) {
			tipString += "<p><b>Attributes:</b></p>";
			for (Iterator iterator = attrs.keySet().iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				String value = attrs.get(key);
				tipString += "<p>" + key + ": " + value + "</p>";
			}
		}
		tipString += "</html>";
		return tipString;
	}

}
