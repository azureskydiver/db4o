package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.*;

public class IndexedPath extends IndexedNodeBase {

	public IndexedPath(Transaction transaction, QConObject parent, BTreeRange range) {
		super(transaction, parent, range);
	}

	public IndexedNode resolve() {
		return null;
	}

	public TreeInt toTreeInt() {		
		TreeInt tree = null;
		final KeyValueIterator i = _range.iterator();
		while (i.moveNext()) {
			final FieldIndexKey key = (FieldIndexKey) i.key();			
			tree = addRangeToTree(tree, search(new Integer(key.parentID())));
		}
		return tree;
	}
}
