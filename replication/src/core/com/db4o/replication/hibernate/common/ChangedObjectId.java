package com.db4o.replication.hibernate.common;

import java.io.Serializable;

public class ChangedObjectId {
	public final Serializable hibernateId;
	public final String className;

	public ChangedObjectId(Serializable hibernateId, String className) {
		this.hibernateId = hibernateId;
		this.className = className;
	}
}
