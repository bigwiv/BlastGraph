/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;


import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author yeyanbo
 * 
 */
public class FiltCommand extends Command {
	// filtBy type
	public static final int EVALUE = 0;
	public static final int COVERAGE = 1;
	public static final int COVERAGE2= 2;
	public static final int SCORE_DENSITY = 3;
	public static final int PERCENTAGE_IDENTITIES = 4;
	public static final int PERCENTAGE_POSITIVES = 5;
	public static final int COVERAGE_LENGTH = 6;

	private int filtBy;
	private Map<ValueEdge, Pair<HitVertex>> removed;
	// filt index(>=0) or all(<0)
	private int index;
	private double cutoff;

	/**
	 * Construct a default FiltCommand with evalue equals 10
	 * 
	 * @param Global.graph
	 */
	public FiltCommand(int index) {
		this.isUndoable = true;
		this.commandName = "Filt";
		this.index = index;
		this.filtBy = EVALUE;
		this.cutoff = 10;
	}

	/**
	 * 
	 * @param Global.graph
	 * @param filtBy
	 *            filt by EVALUE, COVERAGE, SCORE_DENSITY, PERCENTAGE_IDENTITIES
	 *            or PERCENTAGE_POSITIVES
	 * @param cutoff
	 */
	public FiltCommand(int index, int filtBy, double cutoff) {
		this(index);
		this.filtBy = filtBy;
		this.cutoff = cutoff;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		switch (filtBy) {
		case EVALUE:
			removed = Global.graph.filtByEvalue(cutoff, index);
			break;
		case COVERAGE:
			removed = Global.graph.filtByCoverage(cutoff, index);
			break;
		case COVERAGE2:
			removed = Global.graph.filtByCoverage2(cutoff, index);
			break;
		case SCORE_DENSITY:
			removed = Global.graph.filtByScoreDensity(cutoff, index);
			break;
		case PERCENTAGE_IDENTITIES:
			removed = Global.graph.filtByPercentageIdentity(cutoff, index);
			break;
		case PERCENTAGE_POSITIVES:
			removed = Global.graph.filtByPercentagePositive(cutoff, index);
			break;
		case COVERAGE_LENGTH:
			removed = Global.graph.filtByCoverageLength(cutoff, index);
		default:
			break;
		}

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		
		for (ValueEdge ve : removed.keySet()) {
			Global.graph.addEdge(ve, removed.get(ve));
		}
		
	}

	/**
	 * @return the filtBy
	 */
	public int getFiltBy() {
		return filtBy;
	}

	/**
	 * @param filtBy
	 *            the filtBy to set
	 */
	public void setFiltBy(int filtBy) {
		this.filtBy = filtBy;
	}

	/**
	 * @return the cutoff
	 */
	public double getCutoff() {
		return cutoff;
	}

	/**
	 * @param cutoff
	 *            the cutoff to set
	 */
	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
