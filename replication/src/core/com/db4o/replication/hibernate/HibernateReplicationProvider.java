package com.db4o.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public interface HibernateReplicationProvider extends TestableReplicationProviderInside {

	Session getObjectSession();

	Configuration getConfiguration();
}
