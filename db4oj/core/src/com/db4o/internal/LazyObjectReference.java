/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;


/**
 * @exclude
 */
public class LazyObjectReference implements ObjectInfo{
	
	private final ObjectContainerBase _container;
	
	private final int _id;
	
	public LazyObjectReference(ObjectContainerBase container, int id){
		_container = container;
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
	
	private ObjectReference reference(){
		HardObjectReference hardReference = _container.getHardObjectReferenceById(_id);
		if(hardReference == null){
			return null;
		}
		return hardReference._reference;
	}

}
