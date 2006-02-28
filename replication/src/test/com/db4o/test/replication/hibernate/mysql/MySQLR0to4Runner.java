package com.db4o.test.replication.hibernate.mysql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.R0;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateR0to4Runner;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import java.sql.SQLException;
import java.sql.Statement;

public class MySQLR0to4Runner extends HibernateR0to4Runner {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.produceMySQLConfigA();
		dropTables(configuration);
		configuration.addClass(R0.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.produceMySQLConfigB();
		dropTables(configuration);
		configuration.addClass(R0.class);
		return new HibernateReplicationProviderImpl(configuration, "B");
	}

	public void test() {
		super.test();
	}

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
