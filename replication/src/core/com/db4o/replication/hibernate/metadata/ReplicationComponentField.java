/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Creates a relationship between a Collection and its owner.
 * <p/> 
 * ReplicationComponentField and ReplicationComponentIdentity 
 * together allow HibernateReplicationProvider to assign UUIDs 
 * to Collections and retrieve Collections using UUIDs.
 * 
 * @see ReplicationComponentIdentity
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.1
 */
public class ReplicationComponentField {
	public static class Table {
		public static final String NAME = "drs_replication_component_fields";
	}
	
	public static class Fields {
		public static final String REF_OBJ_CLASS_NAME = "referencing_object_class_name";
		public static final String REF_OBJ_FIELD_NAME = "referencing_object_field_name";
	}
	
	/**
	 * The class name of the owner of the Collection.
	 */
	private String referencingObjectClassName;

	/**
	 * The field name of the Collection inside its owner.
	 */
	private String referencingObjectFieldName;
	
	public ReplicationComponentField() {}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReplicationComponentField that = (ReplicationComponentField) o;

		if (!referencingObjectClassName.equals(that.referencingObjectClassName)) return false;
		if (!referencingObjectFieldName.equals(that.referencingObjectFieldName)) return false;

		return true;
	}

	public String getReferencingObjectClassName() {
		return referencingObjectClassName;
	}

	public void setReferencingObjectClassName(String referencingObjectClassName) {
		this.referencingObjectClassName = referencingObjectClassName;
	}

	public String getReferencingObjectFieldName() {
		return referencingObjectFieldName;
	}

	public void setReferencingObjectFieldName(String referencingObjectFieldName) {
		this.referencingObjectFieldName = referencingObjectFieldName;
	}

	public int hashCode() {
		int result;
		result = referencingObjectClassName.hashCode();
		result = 29 * result + referencingObjectFieldName.hashCode();
		return result;
	}

	public String toString() {
		return "ReplicationComponentField{" +
				"referencingObjectClassName='" + referencingObjectClassName + '\'' +
				", referencingObjectFieldName='" + referencingObjectFieldName + '\'' +
				'}';
	}
}
