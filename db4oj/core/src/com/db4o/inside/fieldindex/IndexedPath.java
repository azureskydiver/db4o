package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.Iterator4;
import com.db4o.inside.Exceptions4;
import com.db4o.inside.btree.FieldIndexKey;

public class IndexedPath extends IndexedNodeBase {
	
	public static IndexedNode newParentPath(IndexedNode next, QConObject constraint) {
		QCon parent = constraint.parent();
		if (parent instanceof QConObject) {
			return new IndexedPath((QConObject) parent, next);
		}
		return null;
	}

	private IndexedNode _next;

	public IndexedPath(QConObject parent, IndexedNode next) {
		super(parent);
		_next = next;
	}
	
	public TreeInt toTreeInt() {
		TreeInt tree = null;
		Iterator4 iterator = iterator();
		while (iterator.moveNext()) {
			final FieldIndexKey key = (FieldIndexKey) iterator.current();
			tree = (TreeInt) Tree.add(tree, new TreeInt(key.parentID()));
		}
		return tree;
	}

	public Iterator4 iterator() {		
		return new IndexedPathIterator(this, _next.iterator());
	}

	public int resultSize() {
		Exceptions4.notSupported();
		return 0;
	}
}
