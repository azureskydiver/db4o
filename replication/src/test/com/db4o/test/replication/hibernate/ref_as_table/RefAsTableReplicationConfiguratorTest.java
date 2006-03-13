package com.db4o.test.replication.hibernate.ref_as_table;

import com.db4o.replication.ReplicationConfigurator;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.impl.ref_as_table.Shared;
import com.db4o.test.Test;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class RefAsTableReplicationConfiguratorTest {
	SessionFactory sessionFactory;
	Configuration cfg;

	final static long INIT_VER = Util.MIN_VERSION_NO;

	final static long RAISED_VER = INIT_VER + 1;

	public RefAsTableReplicationConfiguratorTest() {

	}

	public void test() {
		cfg = HibernateConfigurationFactory.createNewDbConfig();
		cfg.addClass(CollectionHolder.class);

		ReplicationConfigurator.refAsTableConfigure(cfg);
		sessionFactory = cfg.buildSessionFactory();

		tstFirstClass();
		tstCollectionUpdate();
		tstCollectionRemove();
		sessionFactory.close();
		sessionFactory = null;
		cfg = null;
	}

	public void tstFirstClass() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();
		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();
		checkVersion(session, ch, INIT_VER);

		ch._name = "changed";
		tx.commit();

		checkVersion(session, ch, RAISED_VER);
		session.close();
	}

	public void tstCollectionUpdate() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();
		checkVersion(session, ch, INIT_VER);

		ch._set.add("8");
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		ch._list.add("88");
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		ch._map.put("88", "88");
		session.flush();
		checkVersion(session, ch, RAISED_VER);
		tx.commit();

		session.close();
	}

	public void tstCollectionRemove() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();
		checkVersion(session, ch, INIT_VER);

		ch._set.add("8");
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		ch._list.add("88");
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		ch._map.put("88", "88");
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		ch._set = null;
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		ch._list = null;
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		ch._map = null;
		session.flush();
		checkVersion(session, ch, RAISED_VER);

		tx.commit();

		session.close();
	}

	protected Session openSession() {
		Session session = sessionFactory.openSession();
		ReplicationConfigurator.refAsTableInstall(session, cfg);
		return session;
	}

	protected void checkVersion(Session session, Object obj, long expected) {
		long actual = Shared.getVersion(session.connection(), obj.getClass().getName(), Shared.castAsLong(session.getIdentifier(obj)));
		Test.ensureEquals(expected, actual);
	}
}
