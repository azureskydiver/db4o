/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query.result;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.query.processor.*;


/**
 * @exclude
 */
public class LazyQueryResult extends AbstractLateQueryResult {
	
	public LazyQueryResult(Transaction trans) {
		super(trans);
	}

	public void loadFromClassIndex(final ClassMetadata clazz) {
		_iterable = classIndexIterable(clazz);
	}
	
	public void loadFromClassIndexes(final ClassMetadataIterator classCollectionIterator) {
		_iterable = classIndexesIterable(classCollectionIterator);
	}
	
	public void loadFromQuery(final QQuery query) {
		_iterable = new Iterable4(){
			public Iterator4 iterator() {
				return query.executeLazy();
			}
		};
	}

}
