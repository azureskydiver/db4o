package com.db4o.replication.hibernate.metadata;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ObjectReference {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "ObjectReference";
	public static final String CLASS_NAME = "className";
	public static final String OBJECT_ID = "objectId";
	public static final String UUID = "uuid";
	public static final String VERSION = "version";

	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	private long objectId;

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	private Uuid uuid;

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid(Uuid uuid) {
		this.uuid = uuid;
	}

	private long version;

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	public ObjectReference() {}

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return new ToStringBuilder(this).
				append(CLASS_NAME, className).
				append(OBJECT_ID, objectId).
				append(UUID, uuid).
				append(VERSION, version).
				toString();
	}
}
