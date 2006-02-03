package com.db4o.replication.hibernate;

public class ReplicationComponentField {
	static final String TABLE_NAME = "ReplicationComponentField";

	private String className;
	private String fieldName;

	public ReplicationComponentField() {
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReplicationComponentField that = (ReplicationComponentField) o;

		if (!className.equals(that.className)) return false;
		if (!fieldName.equals(that.fieldName)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = className.hashCode();
		result = 29 * result + fieldName.hashCode();
		return result;
	}
}
