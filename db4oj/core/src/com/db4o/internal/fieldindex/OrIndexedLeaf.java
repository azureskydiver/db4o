/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.fieldindex;

import com.db4o.internal.query.processor.*;

public class OrIndexedLeaf extends JoinedLeaf {
	
	public OrIndexedLeaf(QCon constraint, IndexedNodeWithRange leaf1, IndexedNodeWithRange leaf2) {
		super(constraint, leaf1, leaf1.getRange().union(leaf2.getRange()));
	}
}
