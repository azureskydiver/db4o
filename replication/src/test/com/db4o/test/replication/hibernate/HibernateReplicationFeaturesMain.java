package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.ReplicationFeaturesMain;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class HibernateReplicationFeaturesMain extends ReplicationFeaturesMain {
	protected Configuration cfgA;
	protected Configuration cfgB;
	protected HibernateReplicationProviderImpl pA;
	protected HibernateReplicationProviderImpl pB;


	public HibernateReplicationFeaturesMain() {
		cfgA = HibernateConfigurationFactory.createNewDbConfig();
		cfgA.addClass(Replicated.class);
		pA = new HibernateReplicationProviderImpl(cfgA, "A");

		cfgB = HibernateConfigurationFactory.createNewDbConfig();
		cfgB.addClass(Replicated.class);
		pB = new HibernateReplicationProviderImpl(cfgB, "B");
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

	protected void delete(Session session) {
		session.createQuery("delete from Replicated").executeUpdate();
	}
}
