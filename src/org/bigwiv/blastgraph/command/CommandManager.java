package org.bigwiv.blastgraph.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class CommandManager {
	public static final int CURRENT_THREAD = 0;
	public static final int NEW_THREAD = 1;
	/**
	 * Command to be concreteExecuted
	 */
	Command command;

	/**
	 * single work thread for executing command
	 */
	Thread workThread;
	
	/**
	 *run in current thread or new thread
	 *default is current thread 
	 */
	int threadType = CURRENT_THREAD;

	/**
	 * undo redo item
	 */
	JMenuItem undoItem, redoItem;
	
	/**
	 * Entry to put command in task queue. Currently only accept one command in
	 * task queue. Return false if failed
	 * 
	 * @param command
	 * @return
	 */
	public boolean putCommand(Command command, int threadType) {
		boolean ir = isRunning();

		if (!ir) {
			this.command = command;
			this.threadType = threadType;
			runCommand();
		}

		return !ir;
	}

	private void runCommand() {
		if (threadType == NEW_THREAD) {
			workThread = new Thread(command);
			workThread.start();
		} else {
			command.execute();
		}
		
		if(isUndoable()){
			redoItem.setEnabled(false);
			redoItem.setText("Redo");
			undoItem.setEnabled(true);
			undoItem.setText("Undo " + command.commandName);
		}else {
			redoItem.setEnabled(false);
			redoItem.setText("Redo");
			undoItem.setEnabled(false);
			undoItem.setText("Undo");
		}
	}

	/**
	 * stop current command return false is failed
	 * 
	 * @return
	 */
	public boolean stop() {

		if (isRunning()) {
			command.stop();
		}

		return !isRunning();
	}

	public boolean isUndoable(){
		return command.isUndoable;
	}
	
	public boolean isCancelable() {
		return command.isCancelable;
	}

	/**
	 * return true if command is running
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return (workThread != null) && workThread.isAlive();
	}

	/**
	 * redo command
	 * @return
	 */
	public boolean redo() {
		if (command != null) {
			runCommand();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * undo command
	 * @return
	 */
	public boolean undo() {
		if (command != null) {
			command.unExecute();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * register undo redo JMenuItem
	 * @param undoItem
	 * @param redoItem
	 */
	public void registerUndoRedoItem(JMenuItem undoItem, JMenuItem redoItem){
		this.undoItem = undoItem;
		this.redoItem = redoItem;
		this.undoItem.setEnabled(false);
		this.redoItem.setEnabled(false);
		this.undoItem.addActionListener(undoRedoListener);
		this.redoItem.addActionListener(undoRedoListener);
	}
	
	private ActionListener undoRedoListener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem)e.getSource();
			if(source.equals(undoItem)){
				undo();
				undoItem.setEnabled(false);
				undoItem.setText("Undo");
				redoItem.setEnabled(true);
				redoItem.setText("Redo " + command.commandName);
			}else if (source.equals(redoItem)) {
				redo();
				redoItem.setEnabled(false);
				redoItem.setText("Redo");
				undoItem.setEnabled(true);
				undoItem.setText("Undo " + command.commandName);
			}
		}
		
	};
}
