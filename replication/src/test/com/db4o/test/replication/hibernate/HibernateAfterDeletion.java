package com.db4o.test.replication.hibernate;

import com.db4o.test.replication.ReplicationAfterDeletionTest;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import org.hibernate.cfg.Configuration;

public abstract class HibernateAfterDeletion extends ReplicationAfterDeletionTest {
	protected Configuration addClasses(Configuration cfg) {
		cfg.addClass(SPCParent.class);
		cfg.addClass(SPCChild.class);
		return cfg;
	}
}
