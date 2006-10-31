/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public class IdTreeQueryResult extends AbstractQueryResult{
	
	private TreeInt _ids;

	public IdTreeQueryResult(Transaction transaction, QueryResult queryResult) {
		super(transaction);
		IntIterator4 i = queryResult.iterateIDs();
		if(! i.moveNext()){
			return;
		}
		_ids = new TreeInt(i.currentInt());
		while(i.moveNext()){
			_ids = (TreeInt) _ids.add(new TreeInt(i.currentInt()));
		}
	}
	
	public Object get(int index) {
		throw new NotImplementedException();
	}

	public int indexOf(int id) {
		throw new NotImplementedException();
	}

	public IntIterator4 iterateIDs() {
		return new IntIterator4Adaptor(new TreeKeyIterator(_ids));
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

	public int size() {
		if(_ids == null){
			return 0;
		}
		return _ids.size();
	}

	public void sort(QueryComparator cmp) {
		throw new NotImplementedException();
	}
	
    public AbstractQueryResult supportSort(){
    	return toIdList();
    }
    
    public AbstractQueryResult supportElementAccess(){
    	return toIdList();
    }

}
