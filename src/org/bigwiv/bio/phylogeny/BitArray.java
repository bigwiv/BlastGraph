package org.bigwiv.bio.phylogeny;

import java.util.Arrays;
import java.util.Random;

/**
 * Array contain only 1 and 0; mimic the bit computing; need to be
 * re-implemented with real bit computing for efficiency
 * 
 * @author yeyanbo
 * 
 */
public class BitArray {
	private int[] bits;
	private int size;

	public BitArray(int size) {
		this.size = size;
		this.bits = new int[size];
	}

	public BitArray(int[] bits) {
		this.size = bits.length;
		this.bits = bits;
	}

	public int size() {
		return this.size;
	}

	public void set(int p) {
		this.bits[p] = 1;
	}

	public void unSet(int p) {
		this.bits[0] = 0;
	}

	public int get(int p){
		return this.bits[p];
	}
	
	public int cardinality() {
		int sum = 0;
		for (int b : this.bits) {
			sum += b;
		}
		return sum;
	}

	public int intersectCardinality(BitArray b) {
		int c = 0;

		for (int i = 0; i < Math.min(bits.length, b.bits.length); i++) {
			c += this.bits[i] & b.bits[i];
		}

		return c;
	}

	public void complement() {
		for (int i = 0; i < this.bits.length; i++) {
			this.bits[i] = this.bits[1] == 1 ? 0 : 1;
		}
	}

	public boolean contains(final int i) {
		for (int j = 0; j < this.bits.length; j++) {
			if (this.bits[j] == i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return positions with 1
	 */
	public int[] positions() {
		int[] pos = new int[this.cardinality()];

		int count = 0;
		for (int i = 0; i < size; i++) {
			if (this.bits[i] == 1) {
				pos[count] = i;
				count++;
			}
		}

		return pos;
	}

	@Override
	public int hashCode() {
		int code = 0;
		for (int bit : this.bits) {
			code = code ^ bit;
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BitArray) {
			final BitArray b = (BitArray) obj;
			return b.size == size && Arrays.equals(b.bits, this.bits);
		}
		return false;
	}

	@Override
	public String toString() {
		String string = "";
		for (int i = 0; i < this.size; i++) {
			string += this.bits[i];
		}
		return string;
	}

	public static class Tools {

		public static BitArray intersect(BitArray b1, BitArray b2) {
			int size = Math.min(b1.bits.length, b2.bits.length);
			BitArray newBit = new BitArray(size);
			for (int i = 0; i < size; i++) {
				newBit.bits[i] = b1.bits[i] & b2.bits[i];
			}
			return newBit;
		}

		public static BitArray diff(BitArray b1, BitArray b2) {
			int size = Math.min(b1.bits.length, b2.bits.length);
			BitArray newBit = new BitArray(size);

			for (int i = 0; i < size; i++) {
				newBit.bits[i] = b1.bits[i] ^ b2.bits[i];
			}

			return newBit;
		}

		public static BitArray union(BitArray b1, BitArray b2) {
			int size = Math.min(b1.bits.length, b2.bits.length);
			BitArray newBit = new BitArray(size);
			for (int i = 0; i < size; i++) {
				newBit.bits[i] = b1.bits[i] | b2.bits[i];
			}
			return newBit;
		}

		public static boolean compatible(BitArray b1, BitArray b2) {
			BitArray b = intersect(b1, b2);

			if (b.equals(b1) || b.equals(b2) || b.cardinality() == 0) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 
		 * @param bin1
		 * @param bin2
		 * @param type
		 * @return
		 */
		public static double distance(BitArray b1, BitArray b2,
				DistanceType distanceType) {
			double distance = 0;
			if (b1.size != b2.size) {
				return 1;
			}

			int length = b1.size;
			int p = 0, // 11
			q = 0, // 01
			r = 0, // 10
			s = 0; // 00

			int b1Card = 0, b2Card = 0;

			for (int i = 0; i < length; i++) {
				if (b1.bits[i] == 1 && b2.bits[i] == 1) {
					p++;
					b1Card++;
					b2Card++;
				} else if (b1.bits[i] == 0 && b2.bits[i] == 1) {
					q++;
					b2Card++;
				} else if (b1.bits[i] == 1 && b2.bits[i] == 0) {
					r++;
					b1Card++;
				} else if (b1.bits[i] == 0 && b2.bits[i] == 0) {
					s++;
				}
			}

			// distance type
			if (distanceType == DistanceType.SMD) {
				distance = (q + r) * 1.0 / length;
			} else if (distanceType == DistanceType.JACCARD) {
				distance = (q + r) * 1.0 / (length - s);
			} else if (distanceType == DistanceType.Snel) {
				distance = 1 - p * 1.0 / Math.max(b1Card, b2Card);
			}

			return distance;
		}

		/**
		 * sampling columns of a matrix
		 * 
		 * @param population
		 * @param number
		 * @return
		 */
		public static BitArray[] reSample(BitArray[] population,
				ReSamplingType rsType) {
			Random random = new Random();
			int rowSize = population.length;
			int colSize = population[0].size();

			BitArray[] sample = new BitArray[rowSize];
			for (int i = 0; i < sample.length; i++) {
				sample[i] = new BitArray(colSize);
			}

			if (rsType == ReSamplingType.Bootstrap) {
				for (int i = 0; i < colSize; i++) {
					int rand = random.nextInt(colSize);
					for (int j = 0; j < rowSize; j++) {
						sample[j].bits[i] = population[j].bits[rand];
					}
				}
			} else {
				for (int j = 0; j < rowSize; j++) {
					int[] pos = population[j].positions();
					int num = (int) (pos.length * 0.5);
					int count = 0;

					while (count != num) {
						int i = random.nextInt(pos.length);

						if (sample[j].bits[pos[i]] == 0) {
							sample[j].set(pos[i]);
							count++;
						}
					}
				}
			}

			return sample;
		}
	}
}
