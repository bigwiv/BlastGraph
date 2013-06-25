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

package org.bigwiv.blastgraph.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * 
 * @author yeyanbo
 *
 */
public class GridBagManager {
	
	public static GridBagConstraints GRID_BAG = new GridBagConstraints();
	
	public static void add(Container cn, Component c, int x,
			int y, int w, int h) {
		GRID_BAG.gridx = x;
		GRID_BAG.gridy = y;
		GRID_BAG.gridwidth = w;
		GRID_BAG.gridheight = h;
		cn.add(c, GRID_BAG);
	}
	
	public static void reset(){
		
		GRID_BAG.anchor = GridBagConstraints.CENTER;
		GRID_BAG.fill = GridBagConstraints.NONE;
		
		GRID_BAG.gridheight = 1;
		GRID_BAG.gridwidth = 1;
		
		GRID_BAG.gridx = GridBagConstraints.RELATIVE;
		GRID_BAG.gridy = GridBagConstraints.RELATIVE;
		
		GRID_BAG.insets = new Insets(0,0,0,0);
		
		GRID_BAG.ipadx = 0;
		GRID_BAG.ipady = 0;
		
		GRID_BAG.weightx = 0;
		GRID_BAG.weighty = 0;
	}
	
}
