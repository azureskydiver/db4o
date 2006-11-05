/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs;

import com.db4o.foundation.*;
import com.db4o.inside.query.*;


/**
 * @exclude
 */
public class LazyClientObjectSetStub {
	
	private final AbstractQueryResult _queryResult;
	
	private IntIterator4 _idIterator;
	
	public LazyClientObjectSetStub(AbstractQueryResult queryResult, IntIterator4 idIterator) {
		_queryResult = queryResult;
		_idIterator = idIterator;
	}
	
	public IntIterator4 idIterator(){
		return _idIterator;
	}
	
	public AbstractQueryResult queryResult(){
		return _queryResult;
	}
	
	public void reset(){
		_idIterator = _queryResult.iterateIDs();
	}

}
