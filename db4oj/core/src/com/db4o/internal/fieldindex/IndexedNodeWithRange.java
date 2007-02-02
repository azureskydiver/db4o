/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.fieldindex;

import com.db4o.internal.btree.*;

public interface IndexedNodeWithRange extends IndexedNode {
	BTreeRange getRange();
}
