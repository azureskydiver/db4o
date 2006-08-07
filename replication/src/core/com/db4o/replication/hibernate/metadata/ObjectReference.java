package com.db4o.replication.hibernate.metadata;

public class ObjectReference {
	public static final String TABLE_NAME = "db4o_object_references";
	public static final String COL_CLASS_NAME = "class_name";
	public static final String COL_HIBERNATE_ID = "hibernate_id";
	public static final String UUID = "uuid";
	public static final String COL_VERSION = "version";

	private String className;

	private long hibernateId;

	private Uuid uuid;

	private long version;
	public ObjectReference() {}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getHibernateId() {
		return hibernateId;
	}

	public void setHibernateId(long objectId) {
		this.hibernateId = objectId;
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

	public String toString() {
		return "ObjectReference{" +
				"_className='" + className + '\'' +
				", hibernateId=" + hibernateId +
				", uuid=" + uuid +
				", version=" + version +
				'}';
	}
}
