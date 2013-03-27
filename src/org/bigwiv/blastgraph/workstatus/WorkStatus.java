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
