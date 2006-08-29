package com.db4o.inside.fieldindex;

import com.db4o.TreeInt;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.*;

public class AndIndexedLeaf implements IndexedNode {

	private final IndexedLeaf _leaf1;
	private final BTreeRange _range;

	public AndIndexedLeaf(IndexedLeaf leaf1, IndexedLeaf leaf2) {
		_leaf1 = leaf1;
		_range = _leaf1.getRange().intersect(leaf2.getRange());
	}

	public KeyValueIterator iterator() {
		return _range.iterator();
	}

	public TreeInt toTreeInt() {
    	return IndexedNodeBase.addRangeToTree(null, _range);
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
