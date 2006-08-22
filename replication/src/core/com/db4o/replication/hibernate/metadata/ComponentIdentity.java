/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Uniquely identifies a Collection using Uuid.
 * 
 * <p/>
 * ComponentField and ComponentIdentity 
 * together allow HibernateReplicationProvider to assign UUIDs 
 * to Collections and retrieve Collections using UUIDs.
 * 
 * @see ComponentField
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.1
 */
public class ComponentIdentity {
	public static class Table {
		public static final String NAME = "drs_component_identities";
	}
	
	public static class Fields {
		public static final String UUID_LONG = "uuidLongPart";
		public static final String REF_OBJ_UUID_LONG = "referencingObjectUuidLongPart";
		public static final String REF_OBJ_FIELD = "referencingObjectField";
		public static final String PROVIDER = "provider";
	}

	/**
	 * The long part of the UUID of a Collection.
	 */
	private long uuidLongPart;
	
	/**
	 * The long part of the UUID of the owner of a Collection.
	 */
	private long referencingObjectUuidLongPart;

	/**
	 * The relationship between a Collection and its owner.
	 */
	private ComponentField referencingObjectField;

	/**
	 * The RDBMS which orginates this Collection.
	 */
	private ProviderSignature provider;
	
	public ComponentIdentity() {}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ComponentIdentity that = (ComponentIdentity) o;

		if (referencingObjectUuidLongPart != that.referencingObjectUuidLongPart) return false;
		if (uuidLongPart != that.uuidLongPart) return false;
		if (!referencingObjectField.equals(that.referencingObjectField)) return false;
		if (!provider.equals(that.provider)) return false;

		return true;
	}

	public ProviderSignature getProvider() {
		return provider;
	}

	public void setProvider(ProviderSignature provider) {
		if (provider == null)
			throw new RuntimeException("provider cannot be null");

		this.provider = provider;
	}

	public ComponentField getReferencingObjectField() {
		return referencingObjectField;
	}

	public void setReferencingObjectField(ComponentField referencingObjectField) {
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
		return "ComponentIdentity{" +
				"uuidLongPart=" + uuidLongPart +
				", referencingObjectUuidLongPart=" + referencingObjectUuidLongPart +
				", referencingObjectField=" + referencingObjectField +
				", provider=" + provider +
				'}';
	}
}
