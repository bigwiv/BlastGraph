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

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;


import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.BidirectionalTransformer;

/**
 * @author yeyanbo
 *
 */
public class EdgePainter implements Transformer<ValueEdge, Paint> {
	protected VisualizationViewer<HitVertex,ValueEdge> vv;
    protected BidirectionalTransformer transformer;
    protected Transformer<ValueEdge, Stroke> strokeTransformer;
    
	public EdgePainter(VisualizationViewer<HitVertex,ValueEdge> vv) {
		this.vv = vv;
		this.transformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
		this.strokeTransformer = vv.getRenderContext().getEdgeStrokeTransformer();

	}
	
	@Override
	public Paint transform(ValueEdge ve) {
		Layout<HitVertex, ValueEdge> layout = vv.getGraphLayout();
		BlastGraph<HitVertex, ValueEdge> graph = (BlastGraph<HitVertex, ValueEdge>) layout.getGraph();
        Pair<HitVertex> p = graph.getEndpoints(ve);
        HitVertex b = p.getFirst();
        HitVertex f = p.getSecond();
        
        BasicStroke stroke = (BasicStroke) strokeTransformer.transform(ve);
        float lineWith = stroke.getLineWidth();
        
    	Point2D pb = transformer.transform(layout.transform(b));
        Point2D pf = transformer.transform(layout.transform(f));

        float xB = (float) pb.getX();
        float yB = (float) pb.getY();
        float xF = (float) pf.getX();
        float yF = (float) pf.getY();
        
		return null;
	}

}
