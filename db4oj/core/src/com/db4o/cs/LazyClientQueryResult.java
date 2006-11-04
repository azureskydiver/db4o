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
	
	private final YapClient _client;
	
	private final int _id;

	public LazyClientQueryResult(Transaction trans, YapClient client, int id) {
		super(trans);
		_client = client;
		_id = id;
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

	public void loadFromClassIndex(YapClass clazz) {
		// TODO Auto-generated method stub
		
	}

	public void loadFromClassIndexes(YapClassCollectionIterator iterator) {
		// TODO Auto-generated method stub
		
	}

	public void loadFromIdReader(YapReader reader) {
		// TODO Auto-generated method stub
		
	}

	public void loadFromQuery(QQuery query) {
		// TODO Auto-generated method stub
		
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void sort(QueryComparator cmp) {
		// TODO Auto-generated method stub
		
	}
	
	protected void finalize() throws Throwable {
		_client.writeMsg(Msg.OBJECTSET_FINALIZED.getWriterForInt(_transaction, _id));
	}

}
