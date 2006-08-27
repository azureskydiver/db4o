package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.*;

public class IndexedPath extends IndexedNodeBase {

	private IndexedNode _next;

	public IndexedPath(Transaction transaction, QConObject parent, IndexedNode next) {
		super(transaction, parent);
		_next = next;
	}

	public TreeInt toTreeInt() {
		TreeInt tree = null;
		KeyValueIterator iterator = iterator();
		while (iterator.moveNext()) {
			final FieldIndexKey key = (FieldIndexKey) iterator.key();
			tree = (TreeInt) Tree.add(tree, new TreeInt(key.parentID()));
		}
		return tree;
	}

	public KeyValueIterator iterator() {		
		return new IndexedPathIterator(this, _next.iterator());
	}
}
