package com.db4o.inside.fieldindex;

import com.db4o.inside.btree.*;

public interface IndexedNodeWithRange extends IndexedNode {
	BTreeRange getRange();
}
