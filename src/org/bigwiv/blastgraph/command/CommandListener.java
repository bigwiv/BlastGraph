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
