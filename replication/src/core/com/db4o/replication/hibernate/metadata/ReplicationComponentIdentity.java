package com.db4o.replication.hibernate.metadata;

public class ReplicationComponentIdentity {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "ReplicationComponentIdentity";

	private long uuidLongPart;

	private long referencingObjectUuidLongPart;

	private ReplicationComponentField referencingObjectField;

	private ReplicationProviderSignature provider;

// --------------------------- CONSTRUCTORS ---------------------------

	public ReplicationComponentIdentity() {
	}

// --------------------- GETTER / SETTER METHODS ---------------------

	public ReplicationProviderSignature getProvider() {
		return provider;
	}

	public void setProvider(ReplicationProviderSignature provider) {
		if (provider == null)
			throw new RuntimeException("provider cannot be null");

		this.provider = provider;
	}

	public ReplicationComponentField getReferencingObjectField() {
		return referencingObjectField;
	}

	public void setReferencingObjectField(ReplicationComponentField referencingObjectField) {
		if (referencingObjectField == null)
			throw new RuntimeException("referencingObjectField cannot be null");

		this.referencingObjectField = referencingObjectField;
	}

	public long getReferencingObjectUuidLongPart() {
		return referencingObjectUuidLongPart;
	}

	public void setReferencingObjectUuidLongPart(long referencingObjectUuidLongPart) {
		this.referencingObjectUuidLongPart = referencingObjectUuidLongPart;
	}

	public long getUuidLongPart() {
		return uuidLongPart;
	}

	public void setUuidLongPart(long uuidLongPart) {
		this.uuidLongPart = uuidLongPart;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReplicationComponentIdentity that = (ReplicationComponentIdentity) o;

		if (referencingObjectUuidLongPart != that.referencingObjectUuidLongPart) return false;
		if (uuidLongPart != that.uuidLongPart) return false;
		if (!referencingObjectField.equals(that.referencingObjectField)) return false;
		if (!provider.equals(that.provider)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (int) (uuidLongPart ^ (uuidLongPart >>> 32));
		result = 29 * result + (int) (referencingObjectUuidLongPart ^ (referencingObjectUuidLongPart >>> 32));
		result = 29 * result + referencingObjectField.hashCode();
		result = 29 * result + provider.hashCode();
		return result;
	}

	public String toString() {
		return "ReplicationComponentIdentity{" +
				"uuidLongPart=" + uuidLongPart +
				", referencingObjectUuidLongPart=" + referencingObjectUuidLongPart +
				", referencingObjectField=" + referencingObjectField +
				", provider=" + provider +
				'}';
	}
}
