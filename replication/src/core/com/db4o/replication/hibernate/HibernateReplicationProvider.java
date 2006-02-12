package com.db4o.replication.hibernate;

import org.hibernate.Session;

public interface HibernateReplicationProvider {
	/**
	 * Returns the session currently in use by this provider
	 *
	 * @return the session currently in use by this provider
	 */
	Session getSession();

	String getModifiedObjectCriterion();
}
