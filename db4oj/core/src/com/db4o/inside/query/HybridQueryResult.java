/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public class HybridQueryResult extends AbstractQueryResult {
	
	private AbstractQueryResult _delegate;
	
	public HybridQueryResult(Transaction transaction, AbstractQueryResult delegate_) {
		super(transaction);
		_delegate = delegate_;
	}
	
	public HybridQueryResult(Transaction transaction) {
		this(transaction, new LazyQueryResult(transaction));
	}

	public Object get(int index) {
		_delegate = _delegate.supportElementAccess();
		return _delegate.get(index);
	}

	public int indexOf(int id) {
		_delegate = _delegate.supportElementAccess();
		return _delegate.indexOf(id);
	}

	public IntIterator4 iterateIDs() {
		return _delegate.iterateIDs();
	}
	
	public Iterator4 iterator() {
		return _delegate.iterator();
	}

	public void loadFromClassIndex(YapClass clazz) {
		_delegate.loadFromClassIndex(clazz);
	}

	public void loadFromClassIndexes(YapClassCollectionIterator iterator) {
		_delegate.loadFromClassIndexes(iterator);
	}

	public void loadFromIdReader(YapReader reader) {
		_delegate.loadFromIdReader(reader);
	}

	public void loadFromQuery(QQuery query) {
		if(query.requiresSort()){
			_delegate = new IdListQueryResult(transaction());
		}
		_delegate.loadFromQuery(query);
	}

	public int size() {
		_delegate = _delegate.supportSize();
		return _delegate.size();
	}

	public void sort(QueryComparator cmp) {
		_delegate = _delegate.supportSort();
		_delegate.sort(cmp);
	}

}
