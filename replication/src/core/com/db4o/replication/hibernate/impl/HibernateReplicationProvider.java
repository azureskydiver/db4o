package com.db4o.replication.hibernate.impl;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

/**
 * Represents a RDBMS.
 * @author Albert Kwan
 * @since 1.0
 * @version 1.2
 */
public interface HibernateReplicationProvider extends TestableReplicationProviderInside {
	Configuration getConfiguration();

	Session getSession();
}
