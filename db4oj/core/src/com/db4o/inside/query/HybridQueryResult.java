/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public class HybridQueryResult extends AbstractQueryResult {
	
	private AbstractQueryResult _delegate;
	
	private final QueryEvaluationMode _mode;
	
	public HybridQueryResult(Transaction transaction, AbstractQueryResult delegate_, QueryEvaluationMode mode) {
		super(transaction);
		_delegate = delegate_;
		_mode = mode;
	}
	
	public HybridQueryResult(Transaction transaction, QueryEvaluationMode mode) {
		this(transaction, new LazyQueryResult(transaction), mode);
	}

	public Object get(int index) {
		_delegate = _delegate.supportElementAccess();
		return _delegate.get(index);
	}
	
	public int getId(int index) {
		_delegate = _delegate.supportElementAccess();
		return _delegate.getId(index);
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
		createIndexSnapshotIfModeRequires();
	}

	public void loadFromClassIndexes(YapClassCollectionIterator iterator) {
		_delegate.loadFromClassIndexes(iterator);
		createIndexSnapshotIfModeRequires();
	}

	public void loadFromIdReader(YapReader reader) {
		_delegate.loadFromIdReader(reader);
		createIndexSnapshotIfModeRequires();
	}

	public void loadFromQuery(QQuery query) {
		if(query.requiresSort()){
			_delegate = new IdListQueryResult(transaction());
		}
		_delegate.loadFromQuery(query);
		createIndexSnapshotIfModeRequires();
	}

	public int size() {
		_delegate = _delegate.supportSize();
		return _delegate.size();
	}

	public void sort(QueryComparator cmp) {
		_delegate = _delegate.supportSort();
		_delegate.sort(cmp);
	}
	
	public AbstractQueryResult createIndexSnapshot(){
		createIndexSnapshotIfModeRequires();
		return this;
	}
	
	private void createIndexSnapshotIfModeRequires(){
		if(_mode == QueryEvaluationMode.SNAPSHOT){
			_delegate = _delegate.createIndexSnapshot();
		}
	}

}
