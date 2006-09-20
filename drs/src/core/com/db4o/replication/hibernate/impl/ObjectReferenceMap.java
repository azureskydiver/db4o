package com.db4o.replication.hibernate.impl;

import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.replication.ReplicationReference;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

final class ObjectReferenceMap {
	private final Map<Object, ReplicationReference> _delegate;

	public ObjectReferenceMap() {
		_delegate = new IdentityHashMap();
	}

	public final void clear() {
		_delegate.clear();
	}

	public final ReplicationReference get(Object obj) {
		return _delegate.get(obj);
	}

	public ReplicationReference getByUUID(Db4oUUID uuid) {
		for (ReplicationReference ref : _delegate.values())
			if (ref.uuid().equals(uuid))
				return ref;
		return null;
	}

	public final ReplicationReference put(Object obj, Db4oUUID uuid, long version) {
		if (_delegate.containsKey(obj)) throw new RuntimeException("key already existed");
		ReplicationReference result = new ReplicationReferenceImpl(obj, uuid, version);
		_delegate.put(obj, result);
		return result;
	}

	public String toString() {
		return _delegate.toString();
	}

	public final void visitEntries(Visitor4 visitor) {
		Iterator i = _delegate.values().iterator();

		while (i.hasNext())
			visitor.visit(i.next());
	}
}


