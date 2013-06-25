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

/**
 * 
 */
package org.bigwiv.blastgraph.command;


/**
 * @author yeyanbo
 * 
 */
public class GetTaxonIdCommand extends Command {

	/**
	 * 
	 */
	public GetTaxonIdCommand() {
		this.commandName = "GetTaxonID";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteExecute()
	 */
	@Override
	public void concreteExecute() {
//		
//		try {
//			String id = new String();
//			String ids[] = new String[Global.graph.getVertexCount()];
//			String taxonIds[] = new String[Global.graph.getVertexCount()];
//			for (HitVertex hv : Global.graph.getVertices()) {
//				// break the cycle to stop the command
//				if (blinker == null) {
//					Global.WORK_STATUS.setMessage("Get TaxonId Stoped");
//					return;
//				}
//				id += hv.getId() + ",";
//			}
//
//			EUtilsServiceStub service = new EUtilsServiceStub();
//			
//			EUtilsServiceStub.EPostRequest postRequest = new EUtilsServiceStub.EPostRequest();
//			postRequest.setDb("protein");
//			postRequest.setId(id);
//			EUtilsServiceStub.EPostResult postResult = service
//					.run_ePost(postRequest);
//
//			// call NCBI ELink utility
//			EUtilsServiceStub.ELinkRequest linkRequest = new EUtilsServiceStub.ELinkRequest();
//
//			linkRequest.setDb("taxonomy");
//			linkRequest.setDbfrom("protein");
//			System.out.println("WebEnv: " + postResult.getWebEnv()
//					+ "\nQueryKey: " + postResult.getQueryKey());
//			linkRequest.setWebEnv(postResult.getWebEnv());
//			linkRequest.setQuery_key(postResult.getQueryKey());
//			linkRequest.setCmd("neighbor_history");
//			EUtilsServiceStub.ELinkResult linkResult = service
//					.run_eLink(linkRequest);
//
//			LinkSetType linkSet = linkResult.getLinkSet()[0];
//			System.out.println(linkSet.getIdList());
//			for (int k = 0; k < linkSet.getIdList().getId().length; k++) {
//				if (blinker == null) {
//					Global.WORK_STATUS.setMessage("Get TaxonId Stoped");
//					return;
//				}
//				System.out.println("	LinkId: " + k);
//				ids[k] = linkSet.getIdList().getId()[k]
//						.getString();
//			}
//
//			for (int k = 0; k < linkSet.getLinkSetDb()[0]
//					.getLink().length; k++) {
//				if (blinker == null) {
//					Global.WORK_STATUS.setMessage("Get TaxonId Stoped");
//					return;
//				}
//				System.out.println("	LinkDb: " + k);
//				taxonIds[k] = linkSet.getLinkSetDb()[0]
//						.getLink()[k].getId().getString();
//			}
//
//			for (int i = 0; i < taxonIds.length; i++) {
//				// break the cycle to stop the command
//				if (blinker == null) {
//					Global.WORK_STATUS.setMessage("Get TaxonId Stoped");
//					return;
//				}
//				System.out.println(ids[i]);
//				// Global.graph.getVertex(ids[i]).putAttribute("taxonId", taxonIds[i]);
//				Global.WORK_STATUS.setMessage("Gi: " + ids[i] + "; taxonId: "
//						+ taxonIds[i]);
//			}
//
//		} catch (AxisFault e) {
//			Global.APP_FRAME_PROXY.showError(e.getMessage());
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			Global.APP_FRAME_PROXY.showError(e.getMessage());
//			e.printStackTrace();
//		}

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bigwiv.blastgraph.command.Command#concreteUnExecute()
	 */
	@Override
	public void concreteUnExecute() {
		// TODO Auto-generated method stub

	}

}
