package com.db4o.inside.fieldindex;

import com.db4o.QCon;

public class OrIndexedLeaf extends JoinedLeaf {
	
	public OrIndexedLeaf(QCon constraint, IndexedNodeWithRange leaf1, IndexedNodeWithRange leaf2) {
		super(constraint, leaf1, leaf1.getRange().union(leaf2.getRange()));
	}
	
}
