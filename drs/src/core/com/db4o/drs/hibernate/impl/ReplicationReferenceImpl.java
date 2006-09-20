package com.db4o.drs.hibernate.impl;

import com.db4o.drs.inside.ReplicationReference;
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