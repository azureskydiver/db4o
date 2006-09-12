/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.fieldindex;

import com.db4o.TreeInt;
import com.db4o.foundation.Iterator4;
import com.db4o.inside.btree.*;

/**
 * @exclude
 */
public class MultiFieldNode implements IndexedNodeWithRange {

	public BTreeRange getRange() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public BTree getIndex() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean isResolved() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public Iterator4 iterator() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public IndexedNode resolve() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public int resultSize() {
		return Integer.MAX_VALUE;
	}

	public TreeInt toTreeInt() {
		throw new com.db4o.foundation.NotImplementedException();
	}

}
