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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;
import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;


import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Edge weighted FRLayout
 * Based on original FRLayout
 * 
 * @author yeyanbo
 *
 * @param <V>
 * @param <E>
 */
public class EWLayout<V, E> extends AbstractLayout<V, E> implements
		IterativeContext {

	private double forceConstant;

	private double temperature;

	private int currentIteration;

	private int mMaxIterations = 700;

	private Map<V, VertexData> vertexData = LazyMap.decorate(
			new HashMap<V, VertexData>(), new Factory<VertexData>() {
				public VertexData create() {
					return new VertexData();
				}
			});

	private double attraction_multiplier = 0.75;

	private double attraction_constant;

	private double repulsion_multiplier = 0.75;

	private double repulsion_constant;

	private double max_dimension;

	private double minEvalue = Double.POSITIVE_INFINITY;

	private double maxEvalue = 0;

	public EWLayout(Graph<V, E> g) {
		super(g);
		findMinMax();
	}

	public EWLayout(Graph<V, E> g, Dimension d) {
		super((Graph<V, E>) g, new RandomLocationTransformer<V>(d), d);
		findMinMax();
		initialize();
		max_dimension = Math.max(d.height, d.width);
	}

	private void findMinMax() {
		BlastGraph<HitVertex, ValueEdge> graph = (BlastGraph<HitVertex, ValueEdge>) getGraph();
		// find min and max evalue
		double evalue;
		for (ValueEdge e : graph.getEdges()) {
			evalue = e.getExpectValue();
			minEvalue = minEvalue > evalue ? evalue : minEvalue;
			maxEvalue = maxEvalue < evalue ? evalue : maxEvalue;
		}
		// solve log zero problem
		minEvalue = (minEvalue == 0) ? 1E-200 : minEvalue;

	}

	@Override
	public void setSize(Dimension size) {
		if (initialized == false) {
			setInitializer(new RandomLocationTransformer<V>(size));
		}
		super.setSize(size);
		max_dimension = Math.max(size.height, size.width);
	}

	/**
	 * Sets the attraction multiplier.
	 */
	public void setAttractionMultiplier(double attraction) {
		this.attraction_multiplier = attraction;
	}

	/**
	 * Sets the repulsion multiplier.
	 */
	public void setRepulsionMultiplier(double repulsion) {
		this.repulsion_multiplier = repulsion;
	}

	public void reset() {
		doInit();
	}

	public void initialize() {
		doInit();
	}

	private void doInit() {
		Graph<V, E> graph = getGraph();
		Dimension d = getSize();

		if (graph != null && d != null) {
			currentIteration = 0;
			temperature = d.getWidth() / 10;

			forceConstant = Math.sqrt(d.getHeight() * d.getWidth()
					/ graph.getVertexCount());

			attraction_constant = attraction_multiplier * forceConstant;
			repulsion_constant = repulsion_multiplier * forceConstant;
		}
	}

	private double EPSILON = 0.000001D;

	/**
	 * Moves the iteration forward one notch, calculation attraction and
	 * repulsion between vertices and edges and cooling the temperature.
	 */
	public synchronized void step() {
		currentIteration++;

		/**
		 * Calculate repulsion
		 */
		while (true) {

			try {
				for (V v1 : getGraph().getVertices()) {
					calcRepulsion(v1);
				}
				break;
			} catch (ConcurrentModificationException cme) {
			}
		}

		/**
		 * Calculate attraction
		 */
		while (true) {
			try {
				for (E e : getGraph().getEdges()) {

					calcAttraction(e);
				}
				break;
			} catch (ConcurrentModificationException cme) {
			}
		}

		while (true) {
			try {
				for (V v : getGraph().getVertices()) {
					if (isLocked(v))
						continue;
					calcPositions(v);
				}
				break;
			} catch (ConcurrentModificationException cme) {
			}
		}
		cool();
	}

	protected synchronized void calcPositions(V v) {
		VertexData fvd = getData(v);
		if (fvd == null)
			return;
		Point2D xyd = transform(v);
		double deltaLength = Math.max(EPSILON, fvd.norm());

		double newXDisp = fvd.getX() / deltaLength
				* Math.min(deltaLength, temperature);

		if (Double.isNaN(newXDisp)) {
			throw new IllegalArgumentException(
					"Unexpected mathematical result in FRLayout:calcPositions [xdisp]");
		}

		double newYDisp = fvd.getY() / deltaLength
				* Math.min(deltaLength, temperature);
		xyd.setLocation(xyd.getX() + newXDisp, xyd.getY() + newYDisp);

		double borderWidth = getSize().getWidth() / 50.0;
		double newXPos = xyd.getX();
		if (newXPos < borderWidth) {
			newXPos = borderWidth + Math.random() * borderWidth * 2.0;
		} else if (newXPos > (getSize().getWidth() - borderWidth)) {
			newXPos = getSize().getWidth() - borderWidth - Math.random()
					* borderWidth * 2.0;
		}

		double newYPos = xyd.getY();
		if (newYPos < borderWidth) {
			newYPos = borderWidth + Math.random() * borderWidth * 2.0;
		} else if (newYPos > (getSize().getHeight() - borderWidth)) {
			newYPos = getSize().getHeight() - borderWidth - Math.random()
					* borderWidth * 2.0;
		}

		xyd.setLocation(newXPos, newYPos);
	}

	protected void calcAttraction(E e) {
		ValueEdge edge = (ValueEdge) e;

		double evalue = edge.getExpectValue();
		double ew; // edge weight

		if (evalue <= 0) {
			ew = 1;
		} else {
			ew = (Math.log10(maxEvalue) - Math.log10(evalue))
					/ (Math.log10(maxEvalue) - Math.log10(minEvalue));
		}
		
		Pair<V> endpoints = getGraph().getEndpoints(e);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();
		boolean v1_locked = isLocked(v1);
		boolean v2_locked = isLocked(v2);

		if (v1_locked && v2_locked) {
			// both locked, do nothing
			return;
		}
		Point2D p1 = transform(v1);
		Point2D p2 = transform(v2);
		if (p1 == null || p2 == null)
			return;
		double xDelta = p1.getX() - p2.getX();
		double yDelta = p1.getY() - p2.getY();

		double deltaLength = Math.max(EPSILON,
				Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)));

		// force by edge weight
		double force = (deltaLength * deltaLength) * ew / attraction_constant;

		if (Double.isNaN(force)) {
			throw new IllegalArgumentException(
					"Unexpected mathematical result in FRLayout:calcPositions [force]");
		}

		double dx = (xDelta / deltaLength) * force;
		double dy = (yDelta / deltaLength) * force;
		if (v1_locked == false) {
			VertexData fvd1 = getData(v1);
			fvd1.offset(-dx, -dy);
		}
		if (v2_locked == false) {
			VertexData fvd2 = getData(v2);
			fvd2.offset(dx, dy);
		}
	}

	protected void calcRepulsion(V v1) {
		VertexData fvd1 = getData(v1);
		if (fvd1 == null)
			return;
		fvd1.setLocation(0, 0);

		try {
			for (V v2 : getGraph().getVertices()) {

				// if (isLocked(v2)) continue;
				if (v1 != v2) {
					Point2D p1 = transform(v1);
					Point2D p2 = transform(v2);
					if (p1 == null || p2 == null)
						continue;
					double xDelta = p1.getX() - p2.getX();
					double yDelta = p1.getY() - p2.getY();

					double deltaLength = Math.max(EPSILON,
							Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)));

					double force = (repulsion_constant * repulsion_constant)
							/ deltaLength;

					if (Double.isNaN(force)) {
						throw new RuntimeException(
								"Unexpected mathematical result in FRLayout:calcPositions [repulsion]");
					}

					fvd1.offset((xDelta / deltaLength) * force,
							(yDelta / deltaLength) * force);
				}
			}
		} catch (ConcurrentModificationException cme) {
			calcRepulsion(v1);
		}
	}

	private void cool() {
		temperature *= (1.0 - currentIteration / (double) mMaxIterations);
	}

	/**
	 * Sets the maximum number of iterations.
	 */
	public void setMaxIterations(int maxIterations) {
		mMaxIterations = maxIterations;
	}

	protected VertexData getData(V v) {
		return vertexData.get(v);
	}

	/**
	 * This one is an incremental visualization.
	 */
	public boolean isIncremental() {
		return true;
	}

	/**
	 * Returns true once the current iteration has passed the maximum count,
	 * <tt>MAX_ITERATIONS</tt>.
	 */
	public boolean done() {
		if (currentIteration > mMaxIterations
				|| temperature < 1.0 / max_dimension) {
			return true;
		}
		return false;
	}

	protected static class VertexData extends Point2D.Double {
		protected void offset(double x, double y) {
			this.x += x;
			this.y += y;
		}

		protected double norm() {
			return Math.sqrt(x * x + y * y);
		}
	}
}
