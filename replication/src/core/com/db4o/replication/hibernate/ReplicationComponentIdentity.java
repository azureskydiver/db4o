package com.db4o.replication.hibernate;

public class ReplicationComponentIdentity {
	private long uuidLongPart;
	private long providerId;
	private long ownerUuidLongPart;
	private ReplicationComponentField ownerField;

	public ReplicationComponentIdentity() {
	}

	public long getUuidLongPart() {
		return uuidLongPart;
	}

	public void setUuidLongPart(long uuidLongPart) {
		this.uuidLongPart = uuidLongPart;
	}

	public long getProviderId() {
		return providerId;
	}

	public void setProviderId(long providerId) {
		this.providerId = providerId;
	}

	public long getOwnerUuidLongPart() {
		return ownerUuidLongPart;
	}

	public void setOwnerUuidLongPart(long ownerUuidLongPart) {
		this.ownerUuidLongPart = ownerUuidLongPart;
	}

	public ReplicationComponentField getOwnerField() {
		return ownerField;
	}

	public void setOwnerField(ReplicationComponentField ownerField) {
		this.ownerField = ownerField;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReplicationComponentIdentity that = (ReplicationComponentIdentity) o;

		if (ownerUuidLongPart != that.ownerUuidLongPart) return false;
		if (providerId != that.providerId) return false;
		if (uuidLongPart != that.uuidLongPart) return false;
		if (!ownerField.equals(that.ownerField)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (int) (uuidLongPart ^ (uuidLongPart >>> 32));
		result = 29 * result + (int) (providerId ^ (providerId >>> 32));
		result = 29 * result + (int) (ownerUuidLongPart ^ (ownerUuidLongPart >>> 32));
		result = 29 * result + ownerField.hashCode();
		return result;
	}
}
