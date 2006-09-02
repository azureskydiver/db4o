package com.db4o.inside.fieldindex;

import com.db4o.*;

public class AndIndexedLeaf extends JoinedLeaf {

	public AndIndexedLeaf(QCon constraint, IndexedNodeWithRange leaf1, IndexedNodeWithRange leaf2) {
		super(constraint, leaf1, leaf1.getRange().intersect(leaf2.getRange()));
	}
}
