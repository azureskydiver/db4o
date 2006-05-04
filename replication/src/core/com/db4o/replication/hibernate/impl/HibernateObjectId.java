package com.db4o.replication.hibernate.impl;

final class HibernateObjectId {
	public final long _hibernateId;
	public final String _className;
	public HibernateObjectId(long hibernateId, String className) {
		this._hibernateId = hibernateId;
		this._className = className;
	}

	public final String toString() {
		return "HibernateObjectId, _className = " + _className + ", _hibernateId = " + _hibernateId;
	}
}
