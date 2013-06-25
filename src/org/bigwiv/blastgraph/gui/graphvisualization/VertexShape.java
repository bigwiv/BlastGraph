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

import java.awt.Shape;

import org.apache.commons.collections15.Transformer;
import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;

import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;

public class VertexShape extends AbstractVertexShapeTransformer<HitVertex>
		implements Transformer<HitVertex, Shape> {

	protected BlastGraph<HitVertex, ValueEdge> graph;

	// protected AffineTransform scaleTransform = new AffineTransform();

	public VertexShape(BlastGraph<HitVertex, ValueEdge> graph) {
		this.graph = graph;
		setSizeTransformer(new Transformer<HitVertex, Integer>() {

			public Integer transform(HitVertex v) {
				return (int) Math.sqrt(v.getLength());

			}
		});
	}

	@Override
	public Shape transform(HitVertex v) {
		return factory.getEllipse(v);
	}
}
