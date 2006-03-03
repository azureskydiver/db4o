package com.db4o.replication.hibernate;

import java.io.Serializable;

public class ChangedObjectId {
	final Serializable hibernateId;
	final String className;

	public ChangedObjectId(Serializable hibernateId, String className) {
		this.hibernateId = hibernateId;
		this.className = className;
	}
}
