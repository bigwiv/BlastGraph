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

/**
 * 
 */
package org.bigwiv.blastgraph.io;

import java.awt.*;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.awt.print.*;

/**
 * @author yeyanbo
 * 
 */

public class PrintUtilities implements Printable {
	private Component componentToBePrinted;

	public static void printComponent(Component c) {
		new PrintUtilities(c).print();
	}

	public PrintUtilities(Component componentToBePrinted) {
		this.componentToBePrinted = componentToBePrinted;
	}

	public void print() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if (printJob.printDialog())
			try {
				printJob.print();
			} catch (PrinterException pe) {
				System.out.println("Error printing: " + pe);
			}
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		} else {
			//double xoff; 
		    //double yoff; 
	        double scale; 
	        double px = pageFormat.getWidth();
	        double py = pageFormat.getHeight();
			int sx = componentToBePrinted.getWidth() - 1;
			int sy = componentToBePrinted.getHeight() - 1;
	        
			if (px / py < sx / sy) 
	        { 
	        	 scale = sy / py;
	            //xoff = 0.5 * (sx - scale * px);
	            //yoff = 0; 
	        } 
	        else 
	        { 
	        	 scale = sx / px;
	            //xoff = 0; 
	            //yoff = 0.5 * (sy - scale * py);
	        }
			
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.translate(pageFormat.getImageableX(),
					pageFormat.getImageableY());
			
			g2d.scale((float)scale, (float)scale);
			
			disableDoubleBuffering(componentToBePrinted);
			componentToBePrinted.paint(g2d);
			enableDoubleBuffering(componentToBePrinted);
			return (PAGE_EXISTS);
		}
	}

	public static void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	public static void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}
}
