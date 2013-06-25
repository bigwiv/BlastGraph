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

package org.bigwiv.blastgraph.gui.graphvisualization;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.command.ExportVertexAttrCommand;
import org.bigwiv.blastgraph.command.RemoveEdgeCommand;
import org.bigwiv.blastgraph.command.SaveSequenceCommand;
import org.bigwiv.blastgraph.command.SettingCommand;
import org.bigwiv.blastgraph.global.Global;
import org.bigwiv.blastgraph.gui.FileChooseTools;
import org.bigwiv.blastgraph.gui.SelectedFile;
import org.bigwiv.blastgraph.gui.SettingDialog;
import org.bigwiv.blastgraph.io.SequenceTool;
import org.biojava.bio.BioException;


import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyPickingPopupGraphMousePlugin<V, E> extends
		AbstractPopupGraphMousePlugin {
	protected JPopupMenu popup;

	public MyPickingPopupGraphMousePlugin() {
		popup = new JPopupMenu();
	}

	// private File getFastaSource() {
	// if (!Global.SETTING.containsKey("FASTA_SOURCE")) {
	// Global.APP_FRAME_PROXY.showError("No Sequence Source Setted!");
	// SettingCommand settingCommand = new SettingCommand();
	// Global.COMMAND_MANAGER.putCommand(settingCommand,
	// CommandManager.CURRENT_THREAD);
	// return null;
	// } else {
	// return new File(Global.SETTING.get("FASTA_SOURCE"));
	// }
	// }

	@Override
	protected void handlePopup(MouseEvent e) {
		final VisualizationViewer<HitVertex, ValueEdge> vv = (VisualizationViewer<HitVertex, ValueEdge>) e
				.getSource();
		final Layout<HitVertex, ValueEdge> layout = (Layout<HitVertex, ValueEdge>) vv
				.getGraphLayout();
		final BlastGraph<HitVertex, ValueEdge> graph = (BlastGraph<HitVertex, ValueEdge>) layout
				.getGraph();
		final Point2D p = e.getPoint();
		final Point2D ivp = p;
		GraphElementAccessor<HitVertex, ValueEdge> pickSupport = vv
				.getPickSupport();
		if (pickSupport != null) {
			final HitVertex vertex = pickSupport.getVertex(layout, ivp.getX(),
					ivp.getY());
			final ValueEdge edge = pickSupport.getEdge(layout, ivp.getX(),
					ivp.getY());
			final PickedState<HitVertex> pickedVertexState = vv
					.getPickedVertexState();
			final PickedState<ValueEdge> pickedEdgeState = vv
					.getPickedEdgeState();
			final Collection<HitVertex> allVertex = (Collection<HitVertex>) graph
					.getVertices();
			final Collection<HitVertex> pickedVertex = (Collection<HitVertex>) pickedVertexState
					.getPicked();
			final Collection<ValueEdge> pickedEdge = (Collection<ValueEdge>) pickedEdgeState
					.getPicked();
			// all action
			final ArrayList<FileFilter> filterList = new ArrayList<FileFilter>();
			AbstractAction saveAllAttrs = new AbstractAction(
					"Save All Vertices Attributes") {

				@Override
				public void actionPerformed(ActionEvent e) {

					filterList.clear();
					filterList.add(FileChooseTools.CSV_FILTER);

					SelectedFile selectedFile = FileChooseTools.saveFile(
							Global.APP_FRAME_PROXY.getFrame(),
							"Export Vertex Info", filterList);

					if (selectedFile == null)
						return;

					ExportVertexAttrCommand exportVertexAttrCommand = new ExportVertexAttrCommand(
							allVertex, selectedFile);
					Global.COMMAND_MANAGER.putCommand(exportVertexAttrCommand,
							CommandManager.CURRENT_THREAD);
				}
			};

			AbstractAction savePickedAttrs = new AbstractAction(
					"Save Selected Vertices Attributes") {

				@Override
				public void actionPerformed(ActionEvent e) {
					filterList.clear();
					filterList.add(FileChooseTools.CSV_FILTER);

					SelectedFile selectedFile = FileChooseTools.saveFile(
							Global.APP_FRAME_PROXY.getFrame(),
							"Export Vertex Info", filterList);

					if (selectedFile == null)
						return;

					ExportVertexAttrCommand exportVertexAttrCommand = new ExportVertexAttrCommand(
							pickedVertex, selectedFile);
					Global.COMMAND_MANAGER.putCommand(exportVertexAttrCommand,
							CommandManager.CURRENT_THREAD);
				}
			};

			AbstractAction saveAllFasta = new AbstractAction(
					"Save All Sequences") {

				@Override
				public void actionPerformed(ActionEvent e) {

					// final File sourceFasta = getFastaSource();
					// if (sourceFasta == null)
					// return;
					final Set<String> list = new HashSet<String>();
					for (HitVertex v : allVertex) {
						list.add(v.getId());
					}

					filterList.clear();
					filterList.add(FileChooseTools.FASTA_FILTER);

					SelectedFile selectedFile = FileChooseTools.saveFile(
							Global.APP_FRAME_PROXY.getFrame(), "Save Fasta",
							filterList);

					if (selectedFile == null)
						return;

					SaveSequenceCommand saveFastaCommand = new SaveSequenceCommand(
							list, "protein", "fasta", selectedFile);
					Global.COMMAND_MANAGER.putCommand(saveFastaCommand,
							CommandManager.NEW_THREAD);
				}
			};
			AbstractAction savePickedFasta = new AbstractAction(
					"Save Selected Sequences") {

				@Override
				public void actionPerformed(ActionEvent e) {
					// final File sourceFasta = getFastaSource();
					// if (sourceFasta == null)
					// return;
					final Set<String> list = new HashSet<String>();
					for (HitVertex v : pickedVertex) {
						list.add(v.getId());
					}

					filterList.clear();
					filterList.add(FileChooseTools.FASTA_FILTER);

					SelectedFile selectedFile = FileChooseTools.saveFile(
							Global.APP_FRAME_PROXY.getFrame(), "Save Fasta",
							filterList);

					if (selectedFile == null)
						return;

					SaveSequenceCommand saveFastaCommand = new SaveSequenceCommand(
							list, "protein", "fasta", selectedFile);
					Global.COMMAND_MANAGER.putCommand(saveFastaCommand,
							CommandManager.NEW_THREAD);
				}
			};

			// init popup menu
			popup.removeAll();
			if (pickedVertex.size() > 0) {
				popup.add(saveAllAttrs);
				popup.add(savePickedAttrs);
				popup.add(saveAllFasta);
				popup.add(savePickedFasta);
			} else {
				popup.add(saveAllAttrs);
				popup.add(saveAllFasta);
			}

			if (pickedEdge.size() > 0) {
				popup.add(new AbstractAction("Remove Selected Edge") {
					public void actionPerformed(ActionEvent e) {
						Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>>();
						for (ValueEdge ve : pickedEdge) {
							removed.put(ve, graph.getEndpoints(ve));
						}
						pickedEdgeState.clear();
						RemoveEdgeCommand command = new RemoveEdgeCommand(
								graph, removed, vv);
						Global.COMMAND_MANAGER.putCommand(command,
								Global.COMMAND_MANAGER.CURRENT_THREAD);
					}
				});
			}

			if (edge != null) {
				popup.add(new AbstractAction("Remove Edge") {
					public void actionPerformed(ActionEvent e) {
						Map<ValueEdge, Pair<HitVertex>> removed = new HashMap<ValueEdge, Pair<HitVertex>>();
						pickedEdgeState.pick(edge, false);
						removed.put(edge, graph.getEndpoints(edge));
						RemoveEdgeCommand command = new RemoveEdgeCommand(
								graph, removed, vv);
						Global.COMMAND_MANAGER.putCommand(command,
								Global.COMMAND_MANAGER.CURRENT_THREAD);
					}
				});
			}

			// show popup
			if (popup.getComponentCount() > 0) {
				popup.show(vv, e.getX(), e.getY());
			}
		}
	}

}
