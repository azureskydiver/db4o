/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;


/**
 * @exclude
 */
public class LazyObjectReference implements ObjectInfo{
	
	private final Transaction _transaction;
	
	private final int _id;	
	
	public LazyObjectReference(Transaction transaction, int id){
		_transaction = transaction;
		_id = id;
	}

	public long getInternalID() {
		return _id;
	}

	public Object getObject() {
		return reference().getObject();
	}

	public Db4oUUID getUUID() {
		return reference().getUUID();
	}

	public long getVersion() {
		return reference().getVersion();
	}
	
	private ObjectReference reference() {
		final HardObjectReference hardRef = _transaction.stream().getHardObjectReferenceById(_transaction, _id);
		return hardRef._reference;
	}

}
