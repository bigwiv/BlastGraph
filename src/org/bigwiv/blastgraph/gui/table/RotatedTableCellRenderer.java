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
