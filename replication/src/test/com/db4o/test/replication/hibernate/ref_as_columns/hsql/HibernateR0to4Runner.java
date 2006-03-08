package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.test.replication.R0to4Runner;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class HibernateR0to4Runner extends R0to4Runner {
	protected Configuration cfgA;
	protected Configuration cfgB;

	protected void clean() {
		dropTables(cfgA);
		dropTables(cfgB);
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
