/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public class LazyQueryResult implements QueryResult {
	
	private final Transaction _transaction;
	
	private Iterable4 _iterable;
	
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
		if(_iterable == null){
			throw new IllegalStateException();
		}
		return new IntIterator4Adaptor(_iterable.iterator());
	}
	
	public void loadFromClassIndex(YapClass clazz) {
		ClassIndexStrategy index = clazz.index();
		if(! (index instanceof BTreeClassIndexStrategy)){
			throw new IllegalStateException();
		}
		final BTree btree = ((BTreeClassIndexStrategy)index).btree();
		_iterable = new Iterable4() {
			public Iterator4 iterator() {
				return  btree.asRange(transaction()).keys();
			}
		};
	}
	
	public Transaction transaction(){
		return _transaction;
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
