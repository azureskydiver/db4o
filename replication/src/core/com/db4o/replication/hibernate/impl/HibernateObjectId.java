package com.db4o.replication.hibernate.impl;

import java.io.Serializable;

public class HibernateObjectId {
	public final Serializable hibernateId;
	public final String className;

	public HibernateObjectId(Serializable hibernateId, String className) {
		this.hibernateId = hibernateId;
		this.className = className;
	}

	public String toString() {
		return "HibernateObjectId, className = " + className + ", hibernateId = " + hibernateId;
	}
}
