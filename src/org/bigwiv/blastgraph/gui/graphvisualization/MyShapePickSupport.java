/**
 * 
 */
package org.bigwiv.blastgraph.gui.graphvisualization;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;

/**
 * @author yeyanbo
 * 
 */
public class MyShapePickSupport<V, E> extends ShapePickSupport<V, E> {

	public MyShapePickSupport(VisualizationServer<V, E> vv) {
		super(vv);
	}

	public MyShapePickSupport(VisualizationServer<V, E> vv, float pickSize) {
		super(vv);
	}

	/**
	 * Returns edges whose shape intersects the 'pickArea' footprint of the
	 * passed x,y, coordinates.
	 */
	public Collection<E> getEdges(Layout<V, E> layout, Shape shape) {
		Set<E> pickedEdges = new HashSet<E>();

		Rectangle2D pickRectangle = (Rectangle2D) shape;

		Point2D p1 = new Point2D.Double(pickRectangle.getMinX(),
				pickRectangle.getMinY());
		Point2D p2 = new Point2D.Double(pickRectangle.getMaxX(),
				pickRectangle.getMaxY());

		// remove the view transform from the rectangle
		p1 = vv.getRenderContext().getMultiLayerTransformer()
				.inverseTransform(Layer.VIEW, p1);
		p2 = vv.getRenderContext().getMultiLayerTransformer()
				.inverseTransform(Layer.VIEW, p2);

		pickRectangle = new Rectangle2D.Double(p1.getX(), p1.getY(), p2.getX()
				- p1.getX(), p2.getY() - p1.getY());

		while (true) {
			try {
				for (E e : getFilteredEdges(layout)) {
					Shape edgeShape = getTransformedEdgeShape(layout, e);
					if (edgeShape == null)
						continue;

					// because of the transform, the edgeShape is now a
					// GeneralPath
					if (edgeShape.intersects(pickRectangle)) {
						pickedEdges.add(e);
					}
				}
				break;
			} catch (ConcurrentModificationException cme) {
			}
		}
		return pickedEdges;
	}

	/**
	 * Retrieves the shape template for <code>e</code> and transforms it
	 * according to the positions of its endpoints in <code>layout</code>.
	 * 
	 * @param layout
	 *            the <code>Layout</code> which specifies <code>e</code>'s
	 *            endpoints' positions
	 * @param e
	 *            the edge whose shape is to be returned
	 * @return
	 */
	private Shape getTransformedEdgeShape(Layout<V, E> layout, E e) {
		Pair<V> pair = layout.getGraph().getEndpoints(e);
		V v1 = pair.getFirst();
		V v2 = pair.getSecond();
		boolean isLoop = v1.equals(v2);
		Point2D p1 = vv.getRenderContext().getMultiLayerTransformer()
				.transform(Layer.LAYOUT, layout.transform(v1));
		Point2D p2 = vv.getRenderContext().getMultiLayerTransformer()
				.transform(Layer.LAYOUT, layout.transform(v2));
		if (p1 == null || p2 == null)
			return null;
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();

		// translate the edge to the starting vertex
		AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

		Shape edgeShape = vv
				.getRenderContext()
				.getEdgeShapeTransformer()
				.transform(
						Context.<Graph<V, E>, E> getInstance(vv
								.getGraphLayout().getGraph(), e));
		if (isLoop) {
			// make the loops proportional to the size of the vertex
			Shape s2 = vv.getRenderContext().getVertexShapeTransformer()
					.transform(v2);
			Rectangle2D s2Bounds = s2.getBounds2D();
			xform.scale(s2Bounds.getWidth(), s2Bounds.getHeight());
			// move the loop so that the nadir is centered in the vertex
			xform.translate(0, -edgeShape.getBounds2D().getHeight() / 2);
		} else {
			float dx = x2 - x1;
			float dy = y2 - y1;
			// rotate the edge to the angle between the vertices
			double theta = Math.atan2(dy, dx);
			xform.rotate(theta);
			// stretch the edge to span the distance between the vertices
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			xform.scale(dist, 1.0f);
		}

		// transform the edge to its location and dimensions
		edgeShape = xform.createTransformedShape(edgeShape);
		return edgeShape;
	}
}
