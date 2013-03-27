/**
 * 
 */
package org.bigwiv.blastgraph.workstatus;


/**
 * @author yeyanbo
 *
 */
public interface StatusChangeListener {
	
	public final StatusChangeListener DEFAULT_LISTENER = new StatusChangeListener() {
		@Override
		public void onStatusChange(WorkStatus ps) {
			//Doing nothing
		}
	};
	
	public void onStatusChange(WorkStatus ps);
}
