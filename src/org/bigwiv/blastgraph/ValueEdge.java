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

package org.bigwiv.blastgraph;

import java.util.Arrays;
import java.util.Comparator;

import org.bigwiv.blastgraph.global.Global;


public class ValueEdge {
	private double expectValue;
	private double score;
	private int numSubHit;
	private SubHit[] subHit;
	
	/**
	 * @param expectValue
	 * @param score
	 * @param numSubHit
	 * @param subHit
	 */
	public ValueEdge(double expectValue, double score, int numSubHit, SubHit[] subHit) {
		super();
		this.expectValue = expectValue;
		this.score = score;
		this.numSubHit = numSubHit;
		this.subHit = subHit;
	}

	/**
	 * @return the expectValue
	 */
	public double getExpectValue() {
		return expectValue;
	}

	/**
	 * @param expectValue the expectValue to set
	 */
	public void setExpectValue(double expectValue) {
		this.expectValue = expectValue;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the numSubHit
	 */
	public int getNumSubHit() {
		return numSubHit;
	}
	
	/**
	 * @return the queryCoverage of the alignment
	 * queryCoverage is the used length of query sequence
	 * of all HSPs in a hit 
	 */
	public int getQueryCoverage(){
		int queryCoverage = 0;
		Segment querySegment[] = new Segment[subHit.length];
		for (int i = 0; i < subHit.length; i++) {
			querySegment[i] = new Segment(
					subHit[i].getQuerySequenceStart(), subHit[i]
							.getQuerySequenceEnd());
		}
		
		queryCoverage = Segment.segmentCoverage(querySegment);
		return queryCoverage;
	}
	
	/**
	 * @return the subjectCoverage of the alignment
	 * subjectCoverage is the used length of subject sequence
	 * of all HSPs in a hit 
	 */
	public int getSubjectCoverage(){
		int subjectCoverage = 0;
		Segment subjectSegment[] = new Segment[subHit.length];
		for (int i = 0; i < subHit.length; i++) {
			subjectSegment[i] = new Segment(
					subHit[i].getSubjectSequenceStart(), subHit[i]
							.getSubjectSequenceEnd());
		}
		
		subjectCoverage = Segment.segmentCoverage(subjectSegment);
		return subjectCoverage;
	}

	/**
	 * @param numSubHit the numSubHit to set
	 */
	public void setNumSubHit(int numSubHit) {
		this.numSubHit = numSubHit;
	}

	/**
	 * @return the subHit
	 */
	public SubHit[] getSubHit() {
		return subHit;
	}

	/**
	 * @param subHit the subHit to set
	 */
	public void setSubHit(SubHit[] subHit) {
		this.subHit = subHit;
	}
}
