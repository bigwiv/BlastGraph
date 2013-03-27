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
