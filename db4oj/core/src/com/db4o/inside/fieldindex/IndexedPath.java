package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.Iterator4;
import com.db4o.inside.Exceptions4;

public class IndexedPath extends IndexedNodeBase {
	
	public static IndexedNode newParentPath(IndexedNode next, QCon constraint) {
		QCon parent = constraint.parent();
		if (parent.canLoadByIndex()) {
			return new IndexedPath((QConObject) parent, next);
		}
		return null;
	}

	private IndexedNode _next;

	public IndexedPath(QConObject parent, IndexedNode next) {
		super(parent);
		_next = next;
	}
	
	public Iterator4 iterator() {		
		return new IndexedPathIterator(this, _next.iterator());
	}

	public int resultSize() {
		Exceptions4.notSupported();
		return 0;
	}
}
