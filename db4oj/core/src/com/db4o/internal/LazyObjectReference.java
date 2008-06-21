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
		synchronized(containerLock()){
			return reference().getObject();
		}
	}

	public Db4oUUID getUUID() {
		synchronized(containerLock()){
			return reference().getUUID();
		}
	}

	public long getVersion() {
		synchronized(containerLock()){
			return reference().getVersion();
		}
	}
	
	public ObjectReference reference() {
		final HardObjectReference hardRef = _transaction.container().getHardObjectReferenceById(_transaction, _id);
		return hardRef._reference;
	}
	
	private Object containerLock(){
		_transaction.container().checkClosed();
		return _transaction.container().lock();
	}

}
