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
package com.db4o.drs.inside;

import com.db4o.ext.Db4oUUID;

public final class ReplicationReferenceImpl implements ReplicationReference {
	private boolean _objectIsNew;

	private final Object _obj;

	private final Db4oUUID _uuid;

	private final long _version;

	private Object _counterPart;

	private boolean _markedForReplicating;

	private boolean _markedForDeleting;

	public ReplicationReferenceImpl(Object obj, Db4oUUID uuid, long version) {
		this._obj = obj;
		this._uuid = uuid;
		this._version = version;
	}

	public final Object counterpart() {
		return _counterPart;
	}

	public final boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || o.getClass().getSuperclass() != o.getClass().getSuperclass()) return false;

		final ReplicationReference that = (ReplicationReferenceImpl) o;

		if (_version != that.version()) return false;
		return _uuid.equals(that.uuid());
	}

	public final int hashCode() {
		int result;
		result = _uuid.hashCode();
		result = 29 * result + (int) (_version ^ (_version >>> 32));
		return result;
	}

	public boolean isCounterpartNew() {
		return _objectIsNew;
	}

	public final boolean isMarkedForDeleting() {
		return _markedForDeleting;
	}

	public final boolean isMarkedForReplicating() {
		return _markedForReplicating;
	}

	public void markCounterpartAsNew() {
		_objectIsNew = true;
	}

	public final void markForDeleting() {
		_markedForDeleting = true;
	}

	public final void markForReplicating() {
		_markedForReplicating = true;
	}

	public final Object object() {
		return _obj;
	}

	public final void setCounterpart(Object obj) {
		_counterPart = obj;
	}

	public String toString() {
		return "ReplicationReferenceImpl{" +
				"_objectIsNew=" + _objectIsNew +
				", _obj=" + _obj +
				", _uuid=" + _uuid +
				", _version=" + _version +
				", _counterPart=" + _counterPart +
				", _markedForReplicating=" + _markedForReplicating +
				", _markedForDeleting=" + _markedForDeleting +
				'}';
	}

	public final Db4oUUID uuid() {
		return _uuid;
	}

	public final long version() {
		return _version;
	}
}