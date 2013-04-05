/**
 * 
 */
package org.bigwiv.blastgraph.command;

import java.io.File;
import java.io.IOException;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.SelectedFile;


/**
 * @author yeyanbo
 *
 */
public class SaveCommand extends Command {
	 /**
     * BlastGraph to be saved
     */
    private BlastGraph<HitVertex, ValueEdge> blastGraph;

    public SaveCommand(BlastGraph<HitVertex, ValueEdge> blastGraph) {
		super();
		this.blastGraph = blastGraph;
		this.commandName = "Save";
	}

	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	protected void concreteExecute() {
		File file = new File(Global.PREFERENCES.get("CURRENT_FILE", 
				Global.getAppPath(this.getClass()) + File.separator + "graph.gml"));

		try {
			Global.WORK_STATUS.setMessage("Saving graph to " + file.getAbsolutePath() + "...");
			blastGraph.save(file);
			Global.WORK_STATUS.setMessage("Saving Complete: "
					+ blastGraph.getVertexCount() + " vertices "
					+ blastGraph.getEdgeCount() + " edges");
		} catch (IOException e) {
			Global.APP_FRAME_PROXY.showError(e.getMessage());
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see org.bigwiv.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	protected void concreteUnExecute() {
		// TODO Auto-generated method stub

	}

}
