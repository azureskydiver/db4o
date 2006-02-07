package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.ReplicationFeaturesMain;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

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
		clean(cfgA);
		return pA;
	}

	protected TestableReplicationProvider prepareProviderB() {
		clean(cfgB);
		return pB;
	}

	public void test() {
		super.test();
	}

	protected void clean() {
		pA.closeIfOpened();
		pB.closeIfOpened();
	}

	private void clean(Configuration cfg) {
		SessionFactory sessionFactory = cfg.buildSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.createQuery("delete from Replicated").executeUpdate();
		tx.commit();
		session.close();
		sessionFactory.close();
	}
}
