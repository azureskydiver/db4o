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
	
	private final int _proxyID;
	
	private int _size = SIZE_NOT_SET;

	public LazyClientQueryResult(Transaction trans, YapClient client, int id) {
		super(trans);
		_client = client;
		_proxyID = id;
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
		// TODO Auto-generated method stub
		return null;
	}


	public int size() {
		if(_size == SIZE_NOT_SET){
			_client.writeMsg(Msg.OBJECTSET_SIZE.getWriterForInt(_transaction, _proxyID));
			_size = ((MsgD)_client.expectedResponse(Msg.OBJECTSET_SIZE)).readInt();
		}
		return _size;
	}

	public void sort(QueryComparator cmp) {
		throw new NotImplementedException();		
	}
	
	protected void finalize() throws Throwable {
		_client.writeMsg(Msg.OBJECTSET_FINALIZED.getWriterForInt(_transaction, _proxyID));
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

}
