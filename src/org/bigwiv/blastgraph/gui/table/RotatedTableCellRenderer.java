package org.bigwiv.blastgraph.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class RotatedTableCellRenderer extends JLabel implements TableCellRenderer {
	protected int m_degreesRotation = -90;

	public RotatedTableCellRenderer(int degrees) {
		m_degreesRotation = degrees;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			this.setText(value.toString());
		} catch (NullPointerException ne) {
			this.setText("Nullvalue");
		}
		return this;
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setClip(0, 0, 500, 500);
		g2.setColor(Color.black);
		g2.setFont(new Font("Arial", Font.PLAIN, 12));
		AffineTransform at = new AffineTransform();
		at.setToTranslation(this.getWidth(), this.getHeight());
		g2.transform(at);
		double radianAngle = (((double) m_degreesRotation) / ((double) 180))
				* Math.PI;
		at.setToRotation(radianAngle);
		g2.transform(at);
		g2.drawString(this.getText(), 0.0f, 0.0f);
	}
}
