/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;

/**
 * @exclude
 */
public class CallbackObjectInfoCollections {
	
	public ObjectInfoCollection added;
	
	public ObjectInfoCollection updated;
	
	public ObjectInfoCollection deleted;

	public static final CallbackObjectInfoCollections EMTPY = empty(); 
		
	public CallbackObjectInfoCollections(ObjectInfoCollection added_, ObjectInfoCollection updated_,
		ObjectInfoCollection deleted_) {
		added = added_;
		updated = updated_;
		deleted = deleted_;
	}
	
	private static final CallbackObjectInfoCollections empty(){
		return new CallbackObjectInfoCollections(ObjectInfoCollectionImpl.EMPTY, ObjectInfoCollectionImpl.EMPTY, ObjectInfoCollectionImpl.EMPTY); 
	}

}
