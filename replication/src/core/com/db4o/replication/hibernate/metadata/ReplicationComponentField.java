package com.db4o.replication.hibernate.metadata;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ReplicationComponentField {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "ReplicationComponentField";

	private String referencingObjectClassName;

	public String getReferencingObjectClassName() {
		return referencingObjectClassName;
	}

	public void setReferencingObjectClassName(String referencingObjectClassName) {
		this.referencingObjectClassName = referencingObjectClassName;
	}

	private String referencingObjectFieldName;

	public String getReferencingObjectFieldName() {
		return referencingObjectFieldName;
	}

	public void setReferencingObjectFieldName(String referencingObjectFieldName) {
		this.referencingObjectFieldName = referencingObjectFieldName;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	public ReplicationComponentField() {
	}

// ------------------------ CANONICAL METHODS ------------------------

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReplicationComponentField that = (ReplicationComponentField) o;

		if (!referencingObjectClassName.equals(that.referencingObjectClassName)) return false;
		if (!referencingObjectFieldName.equals(that.referencingObjectFieldName)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = referencingObjectClassName.hashCode();
		result = 29 * result + referencingObjectFieldName.hashCode();
		return result;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
