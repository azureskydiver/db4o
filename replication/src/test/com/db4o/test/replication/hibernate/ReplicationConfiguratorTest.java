package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.ReplicationConfigurator;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.Uuid;
import com.db4o.test.Test;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.ReplicationTestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ReplicationConfiguratorTest extends ReplicationTestCase {
	protected SessionFactory sessionFactory;
	protected Configuration cfg;

	public ReplicationConfiguratorTest() {

	}

	public void test() {
		super.test();
	}

	public void tstCollectionRemove() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();

		Uuid uuid = getUuid(session, ch);
		Test.ensure(uuid != null);

		ch.set.add("8");
		session.flush();

		ch.list.add("88");
		session.flush();

		ch.map.put("88", "88");
		session.flush();

		ch.set = null;
		session.flush();

		ch.list = null;
		session.flush();

		ch.map = null;
		session.flush();

		session.delete(ch);
		session.flush();
		ensureDeleted(session, uuid);
		tx.commit();

		session.close();
	}

	public void tstCollectionUpdate() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx;
		tx = session.beginTransaction();

		session.save(ch);
		session.flush();

		Uuid uuid = getUuid(session, ch);
		Test.ensure(uuid != null);

		ch.set.add("8");
		session.flush();

		ch.list.add("88");
		session.flush();

		ch.map.put("88", "88");
		session.flush();
		tx.commit();

		tx = session.beginTransaction();
		session.delete(ch);
		session.flush();

		ensureDeleted(session, uuid);
		tx.commit();

		session.close();
	}

	public void tstFirstClass() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();
		Transaction tx;
		tx = session.beginTransaction();

		session.save(ch);
		session.flush();

		Uuid uuid = getUuid(session, ch);
		Test.ensure(uuid != null);

		ch.name = "changed";
		tx.commit();

		tx = session.beginTransaction();
		session.delete(ch);
		session.flush();

		tx.commit();

		ensureDeleted(session, uuid);

		session.close();
	}

	protected void actualTest() {
		if (_providerA instanceof HibernateReplicationProvider) {
			oneRound();
			oneRound();
		}
	}

	protected void clean() {
		if (_providerA instanceof HibernateReplicationProvider) {
			Session session = openSession();
			Transaction tx = session.beginTransaction();
			session.createQuery("delete from CollectionHolder").executeUpdate();
			tx.commit();
			session.close();
			sessionFactory.close();
		}
	}

	protected void ensureDeleted(Session session, Uuid uuid) {
		Test.ensure(Util.getByUUID(session, uuid)==null);
	}

	protected Uuid getUuid(Session session, Object obj) {
		return Util.getUuid(session, obj);
	}

	protected void init() {
		cfg = prepareCfg();
		Util.addClass(cfg, CollectionHolder.class);
		ReplicationConfigurator.configure(cfg);
	}

	protected Session openSession() {
		Session session = sessionFactory.openSession();
		//session.setFlushMode(FlushMode.COMMIT);
		ReplicationConfigurator.install(session, cfg);
		return session;
	}

	protected Configuration prepareCfg() {return HibernateUtil.createNewDbConfig();}

	private void oneRound() {
		init();

		sessionFactory = cfg.buildSessionFactory();

		Session session = sessionFactory.openSession();
		session.close();

		tstFirstClass();
		tstCollectionUpdate();
		tstCollectionRemove();
	}
}
