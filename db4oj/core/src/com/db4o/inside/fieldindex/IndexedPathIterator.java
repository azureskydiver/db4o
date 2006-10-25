/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.fieldindex;

import com.db4o.foundation.*;
import com.db4o.inside.btree.FieldIndexKey;

final class IndexedPathIterator extends CompositeIterator4 {
	
	private IndexedPath _path;
	
	public IndexedPathIterator(IndexedPath path, Iterator4 iterator) {
		super(iterator);
		_path = path;
	}

	protected Iterator4 nextIterator(final Object current) {
		final FieldIndexKey key = (FieldIndexKey) current;
		return _path.search(new Integer(key.parentID())).keys();
	}

}