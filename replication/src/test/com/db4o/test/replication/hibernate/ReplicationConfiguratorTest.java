package com.db4o.test.replication.hibernate;

import com.db4o.replication.ReplicationConfigurator;
import com.db4o.replication.hibernate.Constants;
import com.db4o.replication.hibernate.Util;
import com.db4o.test.Test;
import com.db4o.test.replication.CollectionHolder;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ReplicationConfiguratorTest {
	SessionFactory sessionFactory;
	Configuration cfg;

	final static long INIT_VER = Constants.MIN_VERSION_NO;

	final static long RAISED_VER = INIT_VER + 1;

	public ReplicationConfiguratorTest() {
		cfg = new Configuration().configure("com/db4o/test/replication/hibernate/hibernate.cfg.xml");
		cfg.addClass(CollectionHolder.class);

		ReplicationConfigurator.configure(cfg);
		sessionFactory = cfg.buildSessionFactory();

	}

	public void testFirstClass() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();
		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();
		checkVersion(cfg, session, ch, INIT_VER);

		ch._name = "changed";
		tx.commit();

		checkVersion(cfg, session, ch, RAISED_VER);
		session.close();
	}

	public void testCollectionUpdate() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();
		checkVersion(cfg, session, ch, INIT_VER);

		ch._set.add("8");
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		ch._list.add("88");
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		ch._map.put("88", "88");
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);
		tx.commit();

		session.close();
	}

	public void testCollectionRemove() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();
		checkVersion(cfg, session, ch, INIT_VER);

		ch._set.add("8");
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		ch._list.add("88");
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		ch._map.put("88", "88");
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		ch._set = null;
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		ch._list = null;
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		ch._map = null;
		session.flush();
		checkVersion(cfg, session, ch, RAISED_VER);

		tx.commit();

		session.close();
	}

	protected Session openSession() {
		Session session = sessionFactory.openSession();
		ReplicationConfigurator.install(session, cfg);
		session.setFlushMode(FlushMode.ALWAYS);
		return session;
	}

	protected void checkVersion(Configuration cfg, Session session, Object obj, long expected) {
		long actual = Util.getVersion(cfg, session, obj);
		boolean condition = actual == expected;
		if (!condition)
			System.out.println("actual = " + actual + ", expected = " + expected);
		Test.ensure(condition);
	}
}
