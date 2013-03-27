package org.bigwiv.blastgraph;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Class represent sequence segment denote by start and stop; also provide static
 * methods to calculate coverage length of a Segment array
 * 
 * @author yeyanbo
 * 
 */
public class Segment {

	int start, stop;

	public final static ByStartComparator byStart = new ByStartComparator();

	/**
	 * @param start
	 * @param stop
	 */
	public Segment(int start, int stop) {
		super();
		this.start = start;
		this.stop = stop;
	}
	
	/**
	 * construct from segment string with start and stop separated by "-"
	 * @param segment
	 */
	public Segment(String segment){
		String[] pos = segment.split("-");
		start = Integer.parseInt(pos[0]);
		stop = Integer.parseInt(pos[1]);
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public int getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(int stop) {
		this.stop = stop;
	}

	/**
	 * Order segments by start point.
	 * @author yeyanbo
	 *
	 */
	static final class ByStartComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Segment h1 = (Segment) o1;
			Segment h2 = (Segment) o2;

			if (h1.start > h2.start)
				return 1;
			else if (h1.start < h2.start)
				return -1;
			else
				return 0;
		}
	}
	/**
	 * Calculate coverage length of a Segment array
	 * @param segments
	 * @return
	 */
	public static int segmentCoverage(Segment[] segments) {

		int coverage = 0;
		int start = 0, stop = 0;

		Arrays.sort(segments, Segment.byStart);

		for (int i = 0; i < segments.length; i++) {
			if (i == 0) {
				start = segments[i].start;
				stop = segments[i].stop;
			} else if (i > 0 && stop >= segments[i].start
					&& stop < segments[i].stop) {
				stop = segments[i].stop;
			} else if (i > 0 && stop < segments[i].start) {
				coverage = coverage + (stop - start + 1);
				start = segments[i].start;
				stop = segments[i].stop;
			}
		}

		coverage = coverage + (stop - start + 1);

		return coverage;
	}
	
	@Override
	public String toString() {
		return start + "-" + stop;
	}
}
