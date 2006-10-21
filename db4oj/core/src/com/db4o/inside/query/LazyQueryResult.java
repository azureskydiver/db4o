/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public class LazyQueryResult implements QueryResult {
	
	private final Transaction _transaction;
	
	public LazyQueryResult(Transaction trans) {
		_transaction = trans;
	}

	public Object get(int index) {
		throw new NotImplementedException();
	}

	public int indexOf(int id) {
		throw new NotImplementedException();
	}

	public IntIterator4 iterateIDs() {
		throw new NotImplementedException();
	}

	public void loadFromClassIndex(YapClass clazz) {
		throw new NotImplementedException();
	}

	public void loadFromClassIndexes(YapClassCollectionIterator iterator) {
		throw new NotImplementedException();
	}

	public void loadFromIdReader(YapReader reader) {
		throw new NotImplementedException();
	}

	public void loadFromQuery(QQuery query) {
		throw new NotImplementedException();
	}

	public YapStream stream() {
		return _transaction.stream();
	}

    public ExtObjectContainer objectContainer() {
        return stream();
    }

	public int size() {
		throw new NotImplementedException();
	}

	public void sort(QueryComparator cmp) {
		throw new NotImplementedException();
	}

	public Iterator4 iterator() {
		throw new NotImplementedException();
	}

}
