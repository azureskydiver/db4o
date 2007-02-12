package com.db4o.drs.db4o;

import com.db4o.ObjectContainer;
import com.db4o.internal.cs.ClientObjectContainer;

public class Db4oProviderFactory {
	public static Db4oReplicationProvider newInstance(ObjectContainer oc, String name) {
		if (oc instanceof ClientObjectContainer)
			return new ClientServerReplicationProvider(oc, name);
		else
			return new FileReplicationProvider(oc, name);
	}
	
	public static Db4oReplicationProvider newInstance(ObjectContainer oc) {
		return newInstance(oc, null);
	}
}
