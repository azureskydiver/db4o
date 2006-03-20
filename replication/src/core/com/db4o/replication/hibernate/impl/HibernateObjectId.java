package com.db4o.replication.hibernate.impl;

import java.io.Serializable;

public class HibernateObjectId {
// ------------------------------ FIELDS ------------------------------

	public final Serializable hibernateId;
	public final String className;

// --------------------------- CONSTRUCTORS ---------------------------

	public HibernateObjectId(Serializable hibernateId, String className) {
		this.hibernateId = hibernateId;
		this.className = className;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return "HibernateObjectId, className = " + className + ", hibernateId = " + hibernateId;
	}
}
