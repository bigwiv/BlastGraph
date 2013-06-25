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
