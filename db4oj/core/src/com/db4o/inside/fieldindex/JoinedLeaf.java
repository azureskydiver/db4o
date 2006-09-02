package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;

public class JoinedLeaf implements IndexedNodeWithRange {

	private final QCon _constraint;
	private final IndexedNodeWithRange _leaf1;
	private final BTreeRange _range;
	
	public JoinedLeaf(final QCon constraint, final IndexedNodeWithRange leaf1, final BTreeRange range) {
		if (null == constraint || null == leaf1 || null == range) {
			throw new ArgumentNullException();
		}
		_constraint = constraint;
		_leaf1 = leaf1;
		_range = range;
	}
	
	public BTreeRange getRange() {
		return _range;
	}

	public Iterator4 iterator() {
		return _range.keys();
	}

	public TreeInt toTreeInt() {
		return IndexedNodeBase.addToTree(null, this);
	}

	public BTree getIndex() {
		return _leaf1.getIndex();
	}

	public boolean isResolved() {
		return _leaf1.isResolved();
	}

	public IndexedNode resolve() {
		return IndexedPath.newParentPath(this, _constraint);
	}

	public int resultSize() {
		return _range.size();
	}
}