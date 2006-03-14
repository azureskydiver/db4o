package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.impl.Constants;
import com.db4o.replication.hibernate.impl.UuidLongPartGenerator;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsConfiguration;
import com.db4o.test.Test;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

public class UuidLongPartGeneratorTest {
	public void testUuidLongPartGenerator() {
		Configuration cfg = HibernateUtil.createNewDbConfig();
		RefAsColumnsConfiguration rc = RefAsColumnsConfiguration.produce(cfg);

		final SessionFactory sessionFactory = rc.getConfiguration().buildSessionFactory();

		final Session session1 = sessionFactory.openSession();
		UuidLongPartGenerator generator1 = new UuidLongPartGenerator(session1);

		Test.ensureEquals(Constants.MIN_SEQ_NO + 1, generator1.next());
		session1.close();

		final Session session2 = sessionFactory.openSession();
		UuidLongPartGenerator generator2 = new UuidLongPartGenerator(session2);
		Test.ensureEquals(Constants.MIN_SEQ_NO + 2, generator2.next());

		UuidLongPartGenerator generator3 = new UuidLongPartGenerator(session2);
		Test.ensureEquals(Constants.MIN_SEQ_NO + 3, generator3.next());
		session2.close();

		final Session session4 = sessionFactory.openSession();
		UuidLongPartGenerator generator4 = new UuidLongPartGenerator(session4);
		Test.ensureEquals(Constants.MIN_SEQ_NO + 4, generator4.next());
		session4.close();

		sessionFactory.close();
	}
}
