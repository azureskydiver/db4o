package com.db4o.test.replication.jdk14;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.replication.hibernate.ref_as_table.RefAsTableReplicationProvider;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
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

		a = new RefAsColumnsReplicationProvider(newCfg(), "HSQL RefAsColumns");
		b = new RefAsColumnsReplicationProvider(newCfg(), "HSQL RefAsColumns");
		addProviderPairs(a, b);

		a = new RefAsColumnsReplicationProvider(newCfg(), "HSQL RefAsColumns");
		b = Db4oReplicationTestUtil.newProviderA();
		addProviderPairs(a, b);

		a = Db4oReplicationTestUtil.newProviderA();
		b = new RefAsColumnsReplicationProvider(newCfg(), "HSQL RefAsColumns");
		addProviderPairs(a, b);

		a = new RefAsTableReplicationProvider(newCfg(), "HSQL RefAsTable A");
		b = new RefAsTableReplicationProvider(newCfg(), "HSQL RefAsTable B");
		addProviderPairs(a, b);

		a = new RefAsTableReplicationProvider(newCfg(), "HSQL RefAsTable A");
		b = Db4oReplicationTestUtil.newProviderA();
		addProviderPairs(a, b);
	}

	protected Configuration newCfg() {
		Configuration cfg;
		cfg = HibernateConfigurationFactory.createNewDbConfig();
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
}
