/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.fieldindex;

import com.db4o.inside.btree.*;

public interface IndexedNodeWithRange extends IndexedNode {
	BTreeRange getRange();
}
