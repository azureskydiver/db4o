package com.db4o.inside;

import com.db4o.TreeInt;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.*;

public class AndIndexedLeaf implements IndexedNode {

	private IndexedLeaf _leaf1;
	private IndexedLeaf _leaf2;
	private BTreeRange _range;

	public AndIndexedLeaf(IndexedLeaf leaf1, IndexedLeaf leaf2) {
		_leaf1 = leaf1;
		_leaf2 = leaf2;
		_range = _leaf1.getRange().intersect(_leaf2.getRange());
	}

	public KeyValueIterator iterator() {
		return _range.iterator();
	}

	public TreeInt toTreeInt() {
		Exceptions4.notSupported();
		return null;
	}

	public BTree getIndex() {
		return _leaf1.getIndex();
	}

	public boolean isResolved() {
		return _leaf1.isResolved();
	}

	public IndexedNode resolve() {
		return IndexedPath.newParentPath(this, _leaf1.constraint());
	}

	public int resultSize() {
		return _range.size();
	}
}
