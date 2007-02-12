/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.db4o;

import com.db4o.ObjectContainer;

class ClientServerReplicationProvider extends FileReplicationProvider {
	public ClientServerReplicationProvider(ObjectContainer objectContainer) {		 
		super(objectContainer, "null");
	}

	public ClientServerReplicationProvider(ObjectContainer objectContainer, String name) {
		super(objectContainer, name);
	}
	
	protected void refresh(Object obj) {
		_stream.refresh(obj, 1);
	}
}
