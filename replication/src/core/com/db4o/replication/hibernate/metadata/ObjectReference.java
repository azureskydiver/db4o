/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Holds metadata of a persisted object.
 * 
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.2
 */
public class ObjectReference {
	public static class Table {
		public static final String NAME = "drs_objects";
	}
	
	public static class Fields {
		public static final String UUID = "uuid";
		public static final String VERSION = "modified";
		public static final String TYPED_ID = "typedId";
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
	private long typedId;

	/**
	 * The UUID of the referenced object.
	 * 
	 * @see Uuid
	 */
	private Uuid uuid;

	/**
	 * The version number of the referenced object.
	 */
	private long modified;
	
	public ObjectReference() {}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getTypedId() {
		return typedId;
	}

	public void setTypedId(long objectId) {
		this.typedId = objectId;
	}

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid(Uuid uuid) {
		this.uuid = uuid;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long version) {
		this.modified = version;
	}
}
