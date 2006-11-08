/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.foundation.*;


/**
 * @exclude
 */
public class SnapShotQueryResult extends AbstractLateQueryResult {
	
	public SnapShotQueryResult(Transaction transaction) {
		super(transaction);
	}
	
	public void loadFromClassIndex(final YapClass clazz) {
		createSnapshot(classIndexIterable(clazz)); 
	}

	public void loadFromClassIndexes(final YapClassCollectionIterator classCollectionIterator) {
		createSnapshot(classIndexesIterable(classCollectionIterator));
	}
	
	public void loadFromQuery(final QQuery query) {
		_iterable = new Iterable4() {
			final Iterator4 _iterator = query.executeSnapshot();
			public Iterator4 iterator() {
				_iterator.reset();
				return _iterator;
			}
		}; 
	}
	
	private void createSnapshot(Iterable4 iterable) {
		final Tree ids = TreeInt.addAll(null, new IntIterator4Adaptor(iterable));
		_iterable = new Iterable4() {
			public Iterator4 iterator() {
				return new TreeKeyIterator(ids);
			}
		
		};
	}

}
