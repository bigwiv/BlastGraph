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

package org.bigwiv.blastgraph.command;

public interface CommandListener {
	
	/**
	 * This method will be called at the start of the command concreteExecute
	 */
	public void onExecuteStart();
	
	/**
	 * This method will be called when command concreteExecute finished or terminated
	 */
	public void onExecuteEnd();
	
	/**
	 * This method will be called at the start of the command unExecute
	 */
	public void onUnExecuteStart();
	
	/**
	 * This method will be called when command unExecute finished or terminated;
	 */
	public void onUnExecuteEnd();
}
