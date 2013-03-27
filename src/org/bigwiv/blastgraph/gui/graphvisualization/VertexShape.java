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
