package com.db4o.cs.server;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:55:15 AM
 */
public class Entry {
	private long objectId;
	private Object object;

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}
}
