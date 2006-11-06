/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs;

import com.db4o.*;
import com.db4o.foundation.*;


/**
 * @exclude
 */
public class LazyClientIdIterator implements IntIterator4{
	
	private final LazyClientQueryResult _queryResult;
	
	private int _current;
	
	private int[] _ids;
	
	private final int _batchSize;
	
	private int _available;
	
	public LazyClientIdIterator(LazyClientQueryResult queryResult){
		_queryResult = queryResult;
		_batchSize = queryResult.config().prefetchObjectCount();
		_ids = new int[_batchSize];
		_current = -1;
	}

	public int currentInt() {
		if(_current < 0){
			throw new IllegalStateException();
		}
		return _ids[_current];
	}

	public Object current() {
		return new Integer(currentInt());
	}

	public boolean moveNext() {
		if(_available < 0){
			return false;
		}
		if(_available == 0){
			_queryResult.fetchIDs(_batchSize);
			_available --;
			_current = 0;
			return (_available > 0);
		}
		_current++;
		_available --;
		return true;
	}

	public void reset() {
		_queryResult.reset();
		_available = 0;
		_current = -1;
	}

	public void loadFromIdReader(YapReader reader, int count) {
		for (int i = 0; i < count; i++) {
			_ids[i] = reader.readInt();
		}
		if(count > 0){
			_available = count;
			return;
		}
		_available = -1;
	}

}
