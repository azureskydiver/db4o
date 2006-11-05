/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs;

import com.db4o.*;
import com.db4o.cs.messages.*;
import com.db4o.foundation.*;
import com.db4o.inside.query.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public class LazyClientQueryResult extends AbstractQueryResult{
	
	private static final int SIZE_NOT_SET = -1;
	
	private final YapClient _client;
	
	private final int _queryResultID;
	
	private int _size = SIZE_NOT_SET;
	
	private LazyClientIdIterator _iterator;

	public LazyClientQueryResult(Transaction trans, YapClient client, int queryResultID) {
		super(trans);
		_client = client;
		_queryResultID = queryResultID;
	}

	public Object get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public int indexOf(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public IntIterator4 iterateIDs() {
		return _iterator;
	}
	
	public Iterator4 iterator() {
		return new ClientQueryResultIterator(this);
	}

	public int size() {
		if(_size == SIZE_NOT_SET){
			_client.writeMsg(Msg.OBJECTSET_SIZE.getWriterForInt(_transaction, _queryResultID));
			_size = ((MsgD)_client.expectedResponse(Msg.OBJECTSET_SIZE)).readInt();
		}
		return _size;
	}

	public void sort(QueryComparator cmp) {
		throw new NotImplementedException();		
	}
	
	protected void finalize() throws Throwable {
		_client.writeMsg(Msg.OBJECTSET_FINALIZED.getWriterForInt(_transaction, _queryResultID));
	}
	
	public void loadFromClassIndex(YapClass clazz) {
		throw new NotImplementedException();
	}

	public void loadFromClassIndexes(YapClassCollectionIterator iterator) {
		throw new NotImplementedException();
	}

	public void loadFromIdReader(YapReader reader) {
		int batchSize = reader.readInt(); 
		_iterator = new LazyClientIdIterator(this, batchSize);
		_iterator.loadFromIdReader(reader, batchSize);
	}

	public void loadFromQuery(QQuery query) {
		throw new NotImplementedException();
	}

	public void reset() {
		_client.writeMsg(Msg.OBJECTSET_RESET.getWriterForInt(_transaction, _queryResultID));
	}

	public void fetchIDs(LazyClientIdIterator iterator, int batchSize) {
		int[] ints = new int[]{_queryResultID, batchSize };
		_client.writeMsg(Msg.OBJECTSET_FETCH.getWriterForInts(_transaction, ints));
		YapReader reader = _client.expectedByteResponse(Msg.ID_LIST);
		_iterator.loadFromIdReader(reader, reader.readInt());
	}

}
