/**
 * 
 */
package org.bigwiv.blastgraph.command;

import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.SettingDialog;

/**
 * @author yeyanbo
 *
 */
public class SettingCommand extends Command {

	/**
	 * 
	 */
	public SettingCommand() {
		this.commandName = "Setting";
	}

	/* (non-Javadoc)
	 * @see com.yeyanbo.bio.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
		
		SettingDialog settingDialog = new SettingDialog(Global.APP_FRAME_PROXY.getFrame());
		settingDialog.setVisible(true);
		
	}

	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub
		
	}

}
