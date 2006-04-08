package com.db4o.replication.hibernate.metadata;

public class ObjectReference {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "ObjectReference";
	public static final String CLASS_NAME = "className";
	public static final String OBJECT_ID = "objectId";
	public static final String UUID = "uuid";
	public static final String VERSION = "version";
	public static final String DELETED = "deleted";

	private String className;

	private boolean deleted;

	private long objectId;

	private Uuid uuid;

	private long version;

// --------------------------- CONSTRUCTORS ---------------------------

	public ObjectReference() {}

// --------------------- GETTER / SETTER METHODS ---------------------

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid(Uuid uuid) {
		this.uuid = uuid;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return "ObjectReference{" +
				"className='" + className + '\'' +
				", deleted=" + deleted +
				", objectId=" + objectId +
				", uuid=" + uuid +
				", version=" + version +
				'}';
	}
}
