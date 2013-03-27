package org.bigwiv.blastgraph.gui.graphvisualization;

import java.util.Set;

public interface CollectionChangeListener<T> {
	public void onCollectionChange(Set<T> set);
}
