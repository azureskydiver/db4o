/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com

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
package com.db4o.drs.db4o;

import com.db4o.drs.inside.ReplicationReference;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ObjectInfo;
import com.db4o.foundation.Visitor4;
import com.db4o.internal.*;
import com.db4o.internal.replication.*;

/**
 * @exclude
 */
/*
 * using ObjectReference's hc_tree functionality, only exposing the methods
 * that are to be used in this class. Do not use superclass methods.
 * <p/>
 * Implementation details that are difficult to read:
 * The hc_xxx variables are used for the sorted tree.
 * The virtualAttributes is used to
 */
public class Db4oReplicationReferenceImpl extends ObjectReference implements ReplicationReference, Db4oReplicationReference {

	private Object _counterPart;

	private boolean _markedForReplicating;
	private boolean _markedForDeleting;

	Db4oReplicationReferenceImpl(ObjectInfo objectInfo) {
		ObjectReference yo = (ObjectReference) objectInfo;
		Transaction trans = yo.transaction();
		VirtualAttributes va = yo.virtualAttributes(trans);
		if (va != null) {
			setVirtualAttributes((VirtualAttributes) va.shallowClone());
		} else {
			// No virtu
			setVirtualAttributes(new VirtualAttributes());
		}
		Object obj = yo.getObject();
		setObject(obj);
		ref_init();
	}

	public Db4oReplicationReferenceImpl(Object myObject, Db4oDatabase db, long longPart, long version) {
		setObject(myObject);
		ref_init();
		VirtualAttributes va = new VirtualAttributes();
		va.i_database = db;
		va.i_uuid = longPart;
		va.i_version = version;
		setVirtualAttributes(va);
	}

	public Db4oReplicationReferenceImpl add(Db4oReplicationReferenceImpl newNode) {
		return (Db4oReplicationReferenceImpl) hc_add(newNode);
	}

	public Db4oReplicationReferenceImpl find(Object obj) {
		return (Db4oReplicationReferenceImpl) hc_find(obj);
	}

	public void traverse(Visitor4 visitor) {
		hc_traverse(visitor);
	}

	public Db4oUUID uuid() {
		Db4oDatabase db = signaturePart();
		if (db == null) {
			return null;
		}
		return new Db4oUUID(longPart(), db.getSignature());
	}

	public long version() {
		return virtualAttributes().i_version;
	}

	public Object object() {
		return getObject();
	}

	public Object counterpart() {
		return _counterPart;
	}

	public void setCounterpart(Object obj) {
		_counterPart = obj;
	}

	public void markForReplicating() {
		_markedForReplicating = true;
	}

	public boolean isMarkedForReplicating() {
		return _markedForReplicating;
	}

	public void markForDeleting() {
		_markedForDeleting = true;
	}

	public boolean isMarkedForDeleting() {
		return _markedForDeleting;
	}

	public void markCounterpartAsNew() {
		throw new UnsupportedOperationException("TODO");
	}

	public boolean isCounterpartNew() {
		throw new UnsupportedOperationException("TODO");
	}

	public Db4oDatabase signaturePart() {
		return virtualAttributes().i_database;
	}

	public long longPart() {
		return virtualAttributes().i_uuid;
	}

	public VirtualAttributes virtualAttributes() {
		return virtualAttributes(null);
	}

	public final boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || o.getClass().getSuperclass() != o.getClass().getSuperclass()) return false;

		final ReplicationReference that = (ReplicationReference) o;

		if (version() != that.version()) return false;
		return uuid().equals(that.uuid());
	}

	public final int hashCode() {
		int result;
		result = uuid().hashCode();
		result = 29 * result + (int) (version() ^ (version() >>> 32));
		return result;
	}
}
