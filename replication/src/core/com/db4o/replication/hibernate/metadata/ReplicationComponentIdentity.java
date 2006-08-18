package com.db4o.replication.hibernate.metadata;

public class ReplicationComponentIdentity {
	public static class Table {
		public static final String NAME = "drs_replication_component_identities";
	}
	
	public static class Fields {
		public static final String UUID_LONG = "uuidLongPart";
		public static final String REF_OBJ_UUID_LONG = "referencingObjectUuidLongPart";
		public static final String REF_OBJ_FIELD = "referencingObjectField";
		public static final String PROVIDER = "provider";
	}

	private long uuidLongPart;

	private long referencingObjectUuidLongPart;

	private ReplicationComponentField referencingObjectField;

	private ReplicationProviderSignature provider;
	
	public ReplicationComponentIdentity() {}

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
