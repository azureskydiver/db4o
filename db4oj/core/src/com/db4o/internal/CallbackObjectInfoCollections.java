/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.cs.*;


/**
 * @exclude
 */
public class CallbackObjectInfoCollections {
	
	public final ObjectInfoCollection added;
	
	public final ObjectInfoCollection updated;
	
	public final ObjectInfoCollection deleted;
	
	public final ServerMessageDispatcher serverMessageDispatcher;
	
	public static final CallbackObjectInfoCollections EMTPY = empty(); 
		
	public CallbackObjectInfoCollections(ServerMessageDispatcher serverMessageDispatcher_, ObjectInfoCollection added_, ObjectInfoCollection updated_,
		ObjectInfoCollection deleted_) {
		added = added_;
		updated = updated_;
		deleted = deleted_;
		serverMessageDispatcher = serverMessageDispatcher_;
	}
	
	private static final CallbackObjectInfoCollections empty(){
		return new CallbackObjectInfoCollections(null, ObjectInfoCollectionImpl.EMPTY, ObjectInfoCollectionImpl.EMPTY, ObjectInfoCollectionImpl.EMPTY); 
	}

}
