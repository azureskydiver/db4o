/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.hibernate.impl;

import com.db4o.drs.inside.ReplicationReference;
import com.db4o.drs.inside.ReplicationReferenceImpl;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Visitor4;

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


