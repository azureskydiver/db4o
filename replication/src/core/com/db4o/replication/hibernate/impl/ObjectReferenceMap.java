package com.db4o.replication.hibernate.impl;

import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.replication.ReplicationReference;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

final class ObjectReferenceMap {
// ------------------------------ FIELDS ------------------------------

	private final Map<Object, ReplicationReference> delegate;

// --------------------------- CONSTRUCTORS ---------------------------

	public ObjectReferenceMap() {
		delegate = new IdentityHashMap();
	}

	public final void clear() {
		delegate.clear();
	}

	public final ReplicationReference get(Object obj) {
		return delegate.get(obj);
	}

	public final ReplicationReference put(Object obj, Db4oUUID uuid, long version) {
		ReplicationReference result = new ReplicationReferenceImpl(obj, uuid, version);
		delegate.put(obj, result);
		return result;
	}

	public final void visitEntries(Visitor4 visitor) {
		Iterator i = delegate.values().iterator();

		while (i.hasNext())
			visitor.visit(i.next());
	}
}


