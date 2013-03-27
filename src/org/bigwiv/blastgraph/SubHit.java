package org.bigwiv.blastgraph;

import java.util.Comparator;

public class SubHit{
	private double expectValue;
	private double score;
	private int numberOfIdentities;
	private int numberOfPositives;
	private double percentageIdentity;
	private int alignmentSize;
	private int querySequenceStart;
	private int querySequenceEnd;
	private int subjectSequenceStart;
	private int subjectSequenceEnd;
	
	//Comparator
	public static final ByScoreComparator byScore = new ByScoreComparator();
	
	/**
	 * @param expectValue
	 * @param score
	 * @param numberOfIdentities
	 * @param numberOfPositives
	 * @param percentageIdentity
	 * @param alignmentSize
	 * @param querySequenceStart
	 * @param querySequenceEnd
	 * @param subjectSequenceStart
	 * @param subjectSequenceEnd
	 */
	public SubHit(double expectValue, double score, int numberOfIdentities,
			int numberOfPositives, double percentageIdentity,
			int alignmentSize, int querySequenceStart, int querySequenceEnd,
			int subjectSequenceStart, int subjectSequenceEnd) {
		super();
		this.expectValue = expectValue;
		this.score = score;
		this.numberOfIdentities = numberOfIdentities;
		this.numberOfPositives = numberOfPositives;
		this.percentageIdentity = percentageIdentity;
		this.alignmentSize = alignmentSize;
		this.querySequenceStart = querySequenceStart;
		this.querySequenceEnd = querySequenceEnd;
		this.subjectSequenceStart = subjectSequenceStart;
		this.subjectSequenceEnd = subjectSequenceEnd;
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
	 * @return the numberOfIdentities
	 */
	public int getNumberOfIdentities() {
		return numberOfIdentities;
	}
	/**
	 * @param numberOfIdentities the numberOfIdentities to set
	 */
	public void setNumberOfIdentities(int numberOfIdentities) {
		this.numberOfIdentities = numberOfIdentities;
	}
	/**
	 * @return the numberOfPositives
	 */
	public int getNumberOfPositives() {
		return numberOfPositives;
	}
	/**
	 * @param numberOfPositives the numberOfPositives to set
	 */
	public void setNumberOfPositives(int numberOfPositives) {
		this.numberOfPositives = numberOfPositives;
	}
	/**
	 * @return the percentageIdentity
	 */
	public double getPercentageIdentity() {
		return percentageIdentity;
	}
	/**
	 * @param percentageIdentity the percentageIdentity to set
	 */
	public void setPercentageIdentity(double percentageIdentity) {
		this.percentageIdentity = percentageIdentity;
	}
	/**
	 * @return the alignmentSize
	 */
	public int getAlignmentSize() {
		return alignmentSize;
	}
	/**
	 * @param alignmentSize the alignmentSize to set
	 */
	public void setAlignmentSize(int alignmentSize) {
		this.alignmentSize = alignmentSize;
	}
	/**
	 * @return the querySequenceStart
	 */
	public int getQuerySequenceStart() {
		return querySequenceStart;
	}
	/**
	 * @param querySequenceStart the querySequenceStart to set
	 */
	public void setQuerySequenceStart(int querySequenceStart) {
		this.querySequenceStart = querySequenceStart;
	}
	/**
	 * @return the querySequenceEnd
	 */
	public int getQuerySequenceEnd() {
		return querySequenceEnd;
	}
	/**
	 * @param querySequenceEnd the querySequenceEnd to set
	 */
	public void setQuerySequenceEnd(int querySequenceEnd) {
		this.querySequenceEnd = querySequenceEnd;
	}
	/**
	 * @return the subjectSequenceStart
	 */
	public int getSubjectSequenceStart() {
		return subjectSequenceStart;
	}
	/**
	 * @param subjectSequenceStart the subjectSequenceStart to set
	 */
	public void setSubjectSequenceStart(int subjectSequenceStart) {
		this.subjectSequenceStart = subjectSequenceStart;
	}
	/**
	 * @return the subjectSequenceEnd
	 */
	public int getSubjectSequenceEnd() {
		return subjectSequenceEnd;
	}
	/**
	 * @param subjectSequenceEnd the subjectSequenceEnd to set
	 */
	public void setSubjectSequenceEnd(int subjectSequenceEnd) {
		this.subjectSequenceEnd = subjectSequenceEnd;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alignmentSize;
		long temp;
		temp = Double.doubleToLongBits(expectValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + numberOfIdentities;
		result = prime * result + numberOfPositives;
		temp = Double.doubleToLongBits(percentageIdentity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + querySequenceEnd;
		result = prime * result + querySequenceStart;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + subjectSequenceEnd;
		result = prime * result + subjectSequenceStart;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubHit other = (SubHit) obj;
		if (alignmentSize != other.alignmentSize)
			return false;
		if (Double.doubleToLongBits(expectValue) != Double
				.doubleToLongBits(other.expectValue))
			return false;
		if (numberOfIdentities != other.numberOfIdentities)
			return false;
		if (numberOfPositives != other.numberOfPositives)
			return false;
		if (Double.doubleToLongBits(percentageIdentity) != Double
				.doubleToLongBits(other.percentageIdentity))
			return false;
		if (querySequenceEnd != other.querySequenceEnd)
			return false;
		if (querySequenceStart != other.querySequenceStart)
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		if (subjectSequenceEnd != other.subjectSequenceEnd)
			return false;
		if (subjectSequenceStart != other.subjectSequenceStart)
			return false;
		return true;
	}
	
	
	/**
     * <code>ByScoreComparator</code> compares
     * <code>SubHit</code>s by their score.
     * from biggest to smallest
     */
    public static final class ByScoreComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            SubHit h1 = (SubHit) o1;
            SubHit h2 = (SubHit) o2;

            if (h1.getScore() > h2.getScore())
                return -1;
            else if (h1.getScore() < h2.getScore())
                return 1;
            else
                return 0;
        }
    }
}
