package com.db4o.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import org.hibernate.Session;

public interface HibernateReplicationProvider extends TestableReplicationProviderInside {

	Session getObjectSession();
}
