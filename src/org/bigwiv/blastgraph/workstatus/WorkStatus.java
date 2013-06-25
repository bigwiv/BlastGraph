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

/*
 * WorkStatus.java
 * 
 */

package org.bigwiv.blastgraph.workstatus;

public class WorkStatus {
	private int statusType;
	private String message;
	private StatusChangeListener statusChangeListener;
	
	public final static int MESSAGE = 0;
	public final static int WARNING = 1;
	public final static int ERROR = 2;
	
	public WorkStatus(){
		this.setStatusType(MESSAGE);
		this.message = "";
		this.statusChangeListener = StatusChangeListener.DEFAULT_LISTENER;
	}
	
	/**
	 * @param message
	 */
	public WorkStatus(String message) {
		this.setStatusType(MESSAGE);
		this.message = message;
		this.statusChangeListener = StatusChangeListener.DEFAULT_LISTENER;
	}
	
	/**
	 * @return the statusType
	 */
	public int getStatusType() {
		return statusType;
	}

	/**
	 * @param statusType the statusType to set
	 */
	private void setStatusType(int statusType) {
		this.statusType = statusType;
	}

	public String getMessage() {
		return message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.setStatusType(MESSAGE);
		this.message = message;
		statusChangeListener.onStatusChange(this);
	}
	
	/**
	 * @param message the message to set
	 */
	public void setWarning(String message){
		this.setStatusType(WARNING);
		this.message = message;
		statusChangeListener.onStatusChange(this);
	}
	
	/**
	 * @param message the message to set
	 */
	public void setError(String message){
		this.setStatusType(ERROR);
		this.message = message;
		statusChangeListener.onStatusChange(this);
	}
	
	/**
	 * @param scListener the statusChangeListener to set
	 */
	public void setStatusChangeListener(StatusChangeListener statusChangeListener) {
		this.statusChangeListener = statusChangeListener;
	}
	/**
	 * @return the statusChangeListener
	 */
	public StatusChangeListener getStatusChangeListener() {
		return statusChangeListener;
	}

	/**
	 * @return the formated message
	 */
	public String toString(){
		String string = "";
		
		if(statusType == 0){
			string = message;
		}else if(statusType == 1) {
			string = "Warning: " + message;
		}else {
			string = "Error: " + message;
		}
		
		return string;
	}

}
