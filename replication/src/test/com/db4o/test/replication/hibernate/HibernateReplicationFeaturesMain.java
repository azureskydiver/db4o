package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.ReplicationFeaturesMain;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class HibernateReplicationFeaturesMain extends ReplicationFeaturesMain {
	static protected Configuration cfgA;
	static protected Configuration cfgB;
	static protected HibernateReplicationProviderImpl pA;
	static protected HibernateReplicationProviderImpl pB;

	static {
		cfgA = HibernateConfigurationFactory.createNewDbConfig();
		cfgA.addClass(Replicated.class);
		pA = new HibernateReplicationProviderImpl(cfgA, "A", new byte[]{1});

		cfgB = HibernateConfigurationFactory.createNewDbConfig();
		cfgB.addClass(Replicated.class);
		pB = new HibernateReplicationProviderImpl(cfgB, "B", new byte[]{2});
	}

	public HibernateReplicationFeaturesMain() {

	}

	protected TestableReplicationProvider prepareProviderA() {
		delete(pA.getSession());
		return pA;
	}

	protected TestableReplicationProvider prepareProviderB() {
		delete(pA.getSession());
		return pB;
	}

	public void test() {
		super.test();
	}

	protected void clean() {
		pA.closeIfOpened();
		pB.closeIfOpened();
	}

	private void delete(Session session) {
		session.createQuery("delete from Replicated").executeUpdate();
	}
}
