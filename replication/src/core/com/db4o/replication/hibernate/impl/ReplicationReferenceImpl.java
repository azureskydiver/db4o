package com.db4o.replication.hibernate.impl;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ReplicationReferenceImpl implements ReplicationReference {
	private Object obj;
	private final Db4oUUID uuid;
	private long version;
	private Object counterPart;
	private boolean markedForReplicating;

	public ReplicationReferenceImpl(Object obj, Db4oUUID uuid, long version) {
		this.obj = obj;
		this.uuid = uuid;
		this.version = version;
	}

	public void setObject(Object obj) {
		this.obj = obj;
	}

	public Object object() {
		return obj;
	}

	public long version() {
		return version;
	}

	public Db4oUUID uuid() {
		return uuid;
	}

	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || o.getClass().getSuperclass() != o.getClass().getSuperclass()) return false;

		final ReplicationReference that = (ReplicationReferenceImpl) o;

		if (version != that.version()) return false;
		if (!uuid.equals(that.uuid())) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = uuid.hashCode();
		result = 29 * result + (int) (version ^ (version >>> 32));
		return result;
	}

	public Object counterpart() {
		return counterPart;
	}

	public void setCounterpart(Object obj) {
		counterPart = obj;
	}

	public void markForReplicating() {
		markedForReplicating = true;
	}

	public boolean isMarkedForReplicating() {
		return markedForReplicating;
	}

	public String toString() {
		return new ToStringBuilder(this).
				append("className", obj).
				append("objectId", uuid).
				append("version", version).
				append("counterPart", counterPart).
				append("markedForReplicating", markedForReplicating).
				toString();
	}
}