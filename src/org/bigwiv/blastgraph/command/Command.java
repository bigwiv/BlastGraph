package org.bigwiv.blastgraph.command;

import java.util.HashSet;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * Interface for Command Pattern
 * 
 * @author yeyanbo
 * 
 */
public abstract class Command implements Runnable {
	protected String commandName;
	
	/*
	 * Is this Command cancelable during processing
	 */
	protected boolean isCancelable = false;
	
	/*
	 * Is this Command undoable after processing
	 */
	protected boolean isUndoable = false;
	
	/*
	 * Assign current thread of command to blinker and monitor it in the cycle
	 * to decide whether to break the cycle to stop the command
	 */ 
	protected volatile Thread blinker;

	protected Set<CommandListener> listeners = new HashSet<CommandListener>();

	@Override
	public void run() {
		blinker = Thread.currentThread();
		execute();
	}

	/**
	 * Stop the command
	 */
	public void stop() {
		blinker = null;
	}

	/**
	 * Add CommandListener
	 * 
	 * @param listener
	 */
	public void addCommandListener(CommandListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Remove CommandListener
	 * 
	 * @param listener
	 */
	public void removeCommandListener(CommandListener listener) {
		this.listeners.remove(listener);
	}


	protected void onExecuteStart() {
		for (CommandListener listener : listeners) {
			listener.onExecuteStart();
		}
	}

	protected void onExecuteEnd() {
		for (CommandListener listener : listeners) {
			listener.onExecuteEnd();
		}
	}

	protected void onUnExecuteStart() {
		for (CommandListener listener : listeners) {
			listener.onUnExecuteStart();
		}
	}

	protected void onUnExecuteEnd() {
		for (CommandListener listener : listeners) {
			listener.onUnExecuteEnd();
		}
	}
	
	/**
	 * To run this command in current thread, call this method directly. Otherwise
	 * put this runnable command in another thread.
	 */
	public void execute(){
		onExecuteStart();
		concreteExecute();
		onExecuteEnd();
	}

	/**
	 * Call this method to undo this command
	 */
	public void unExecute(){
		onUnExecuteStart();
		concreteUnExecute();
		onUnExecuteEnd();
	}
	
	/**
	 *  All subclasses should implement this method.
	 */
	protected abstract void concreteExecute();
	
	/**
	 * Subclasses implement this method to undo this command
	 */
	protected abstract void concreteUnExecute();;
	
}
