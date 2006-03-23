package com.db4o.test.replication.jdk14;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.template.r0tor4.R0;
import com.db4o.test.replication.template.r0tor4.R0to4Runner;
import com.db4o.test.replication.transients.TransientReplicationProvider;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import java.sql.SQLException;
import java.sql.Statement;

public class R0to4RunnerCombinations extends R0to4Runner {
	public R0to4RunnerCombinations() {
		super();
	}

	public void test() {
		super.test();
	}

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = new HibernateReplicationProviderImpl(addClasses(HibernateUtil.createNewDbConfig()), "HSQL RefAsTable");
		b = Db4oReplicationTestUtil.newProviderA();
		addProviderPairs(a, b);

		a = HibernateUtil.refAsTableProviderA();
		b = HibernateUtil.refAsTableProviderB();
		addProviderPairs(a, b);
	}

	protected Configuration addClasses(Configuration cfg) {
		cfg = HibernateUtil.createNewDbConfig();
		cfg.addClass(R0.class);

		return cfg;
	}

	protected void clean() {
		deleteOrDropTables(_providerA);
		deleteOrDropTables(_providerB);
	}

	private void deleteOrDropTables(TestableReplicationProviderInside provider) {
		if ((provider instanceof TransientReplicationProvider)
				|| (provider instanceof Db4oReplicationProvider)) {
			delete(provider);
		} else if (provider instanceof HibernateReplicationProvider) {
			final Configuration cfg = ((HibernateReplicationProvider) provider).getConfiguration();
			dropTables(cfg);
		}
	}

	/**
	 * It is impossible to delet the R0 circles in RDBMS...
	 *
	 * @param cfg
	 */
	protected void dropTables(Configuration cfg) {
		final SessionFactory sf = cfg.buildSessionFactory();
		final Session session = sf.openSession();
		final Transaction tx = session.beginTransaction();
		final Statement st;

		try {
			st = session.connection().createStatement();
			st.execute("drop table R0");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		tx.commit();
		session.close();
		sf.close();
	}

	@Override
	protected TestableReplicationProviderInside prepareProviderA() {
		throw new RuntimeException("REVISE");
	}

	@Override
	protected TestableReplicationProviderInside prepareProviderB() {
		throw new RuntimeException("REVISE");
	}
}
