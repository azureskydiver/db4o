/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
final class ObjectInfoCollectionImpl implements ObjectInfoCollection {
	
	public static final ObjectInfoCollection EMPTY = new ObjectInfoCollectionImpl(Iterators.EMPTY_ITERABLE);
	
	private final Iterable4 _collection;

	public ObjectInfoCollectionImpl(Iterable4 collection) {
		_collection = collection;
	}

	public Iterator4 iterator() {
		return _collection.iterator();
	}
}