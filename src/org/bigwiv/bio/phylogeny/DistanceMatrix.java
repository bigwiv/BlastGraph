/**
 * 
 */
package org.bigwiv.bio.phylogeny;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * @author yeyanbo
 * 
 */
public class DistanceMatrix {
	private double[][] matrix;
	private int size;
	private List<String> labels;

	public DistanceMatrix(List<String> labels) {
		this.labels = labels;
		this.size = labels.size();
		this.matrix = new double[size][size];
	}

	/**
	 * @return the matrix
	 */
	public double get(int i, int j) {
		if (inRange(i) && inRange(j)) {
			return matrix[i][j];
		} else {
			return -1;
		}
	}

	public double get(String label1, String label2) {

		int i = labels.indexOf(label1);
		int j = labels.indexOf(label2);
		if (i != -1 && j != -1) {
			return get(i, j);
		} else {
			return -1;
		}
	}

	/**
	 * @param matrix
	 *            the matrix to set
	 */
	public void set(int i, int j, double matrix) {

		if (matrix < 0) {
			matrix = 0;
		}

		if (inRange(i) && inRange(j) && i != j) {
			this.matrix[i][j] = matrix;
			this.matrix[j][i] = matrix;
		}
	}

	/**
	 * 
	 * @param label1
	 * @param label2
	 * @param matrix
	 */
	public void set(String label1, String label2, double matrix) {

		int i = labels.indexOf(label1);
		int j = labels.indexOf(label2);

		if (i != -1 && j != -1) {
			set(i, j, matrix);
		}
	}

	/**
	 * @return the matrix
	 */
	public double[][] getMatrix() {
		return matrix;
	}

	/**
	 * @return the labels
	 */
	public List<String> getLabels() {
		return labels;
	}

	/**
	 * @return the labels
	 */
	public String getLabel(int index) {
		return labels.get(index);
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabel(int index, String label) {
		if (inRange(index)) {
			this.labels.set(index, label);
		}
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	private boolean inRange(int index) {
		if (index >= 0 && index < this.size) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}
	
	public void write(Writer writer) throws IOException{
		for (int i = 0; i < size; i++) {
			if(i == 0){
				writer.write(labels.get(i));
			}
			writer.write("\t" + labels.get(i));
		}
		
		writer.write("\n");
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if(j == 0){
					writer.write(matrix[i][j] + "");
				}
				writer.write("\t" + matrix[i][j]);
			}
			writer.write("\n");
		}
	}
}
