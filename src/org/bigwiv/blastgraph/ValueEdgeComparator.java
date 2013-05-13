/**
 * 
 */
package org.bigwiv.blastgraph;

import java.util.Comparator;

/**
 * @author yeyanbo
 *
 */
public class ValueEdgeComparator<T> implements Comparator<ValueEdge> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ValueEdge arg0, ValueEdge arg1) {
		// from weak to strong
		double evalue = Math.log10(arg0.getExpectValue()) - Math.log10(arg1.getExpectValue());
		if (evalue < 0) {
			return 1;
		} else if (evalue > 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
