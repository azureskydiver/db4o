/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Holds metadata of a persisted object.
 * 
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.1
 */
public class ObjectReference {
	public static class Table {
		public static final String NAME = "drs_object_references";
		public static final String CLASS_NAME = "class_name";
		public static final String HIBERNATE_ID = "hibernate_id";
		public static final String VERSION = "version";
	}
	
	public static class Fields {
		public static final String UUID = "uuid";
		public static final String VERSION = Table.VERSION;
		public static final String HIBERNATE_ID = "hibernateId";
		public static final String CLASS_NAME = "className";
		
	}
	
	/**
	 * Fully qualified class name of the referenced object.
	 */
	private String className;

	/**
	 * The identifier of the referenced object in Hibernate. 
	 * 
	 * @see org.hibernate.Session#getIdentifier(Object refObj)
	 */
	private long hibernateId;

	/**
	 * The UUID of the referenced object.
	 * 
	 * @see Uuid
	 */
	private Uuid uuid;

	/**
	 * The version number of the referenced object.
	 */
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
