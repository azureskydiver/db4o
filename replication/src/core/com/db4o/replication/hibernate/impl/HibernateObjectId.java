package com.db4o.replication.hibernate.impl;

final class HibernateObjectId {
// ------------------------------ FIELDS ------------------------------

	public final long hibernateId;
	public final String className;

// --------------------------- CONSTRUCTORS ---------------------------

	public HibernateObjectId(long hibernateId, String className) {
		this.hibernateId = hibernateId;
		this.className = className;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public final String toString() {
		return "HibernateObjectId, className = " + className + ", hibernateId = " + hibernateId;
	}
}
