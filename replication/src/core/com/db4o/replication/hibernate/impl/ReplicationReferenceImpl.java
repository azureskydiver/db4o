package com.db4o.replication.hibernate.impl;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import org.apache.commons.lang.builder.ToStringBuilder;

public final class ReplicationReferenceImpl implements ReplicationReference {
// ------------------------------ FIELDS ------------------------------

	private boolean objectIsNew;
	private final Object obj;
	private final Db4oUUID uuid;
	private final long version;
	private Object counterPart;
	private boolean markedForReplicating;
	private boolean markedForDeleting;

// --------------------------- CONSTRUCTORS ---------------------------

	public ReplicationReferenceImpl(Object obj, Db4oUUID uuid, long version) {
		this.obj = obj;
		this.uuid = uuid;
		this.version = version;
	}

// --------------------- GETTER / SETTER METHODS ---------------------

	public final boolean isMarkedForDeleting() {
		return markedForDeleting;
	}

	public final boolean isMarkedForReplicating() {
		return markedForReplicating;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public final boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || o.getClass().getSuperclass() != o.getClass().getSuperclass()) return false;

		final ReplicationReference that = (ReplicationReferenceImpl) o;

		if (version != that.version()) return false;
		return uuid.equals(that.uuid());
	}

	public final int hashCode() {
		int result;
		result = uuid.hashCode();
		result = 29 * result + (int) (version ^ (version >>> 32));
		return result;
	}

	public final String toString() {
		return new ToStringBuilder(this).
				append("className", obj).
				append("objectId", uuid).
				append("version", version).
				append("counterPart", counterPart).
				append("markedForReplicating", markedForReplicating).
				toString();
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ReplicationReference ---------------------

	public final Object counterpart() {
		return counterPart;
	}

	public boolean isCounterpartNew() {
		return objectIsNew;
	}

	public final void markForDeleting() {
		markedForDeleting = true;
	}

	public final void markForReplicating() {
		markedForReplicating = true;
	}

	public void markCounterpartAsNew() {
		objectIsNew = true;
	}

	public final Object object() {
		return obj;
	}

	public final void setCounterpart(Object obj) {
		counterPart = obj;
	}

	public final Db4oUUID uuid() {
		return uuid;
	}

	public final long version() {
		return version;
	}
}