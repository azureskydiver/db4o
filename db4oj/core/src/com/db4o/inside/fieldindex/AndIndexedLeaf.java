/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.query.processor.*;

public class AndIndexedLeaf extends JoinedLeaf {

	public AndIndexedLeaf(QCon constraint, IndexedNodeWithRange leaf1, IndexedNodeWithRange leaf2) {
		super(constraint, leaf1, leaf1.getRange().intersect(leaf2.getRange()));
	}
}
