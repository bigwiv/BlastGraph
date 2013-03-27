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
