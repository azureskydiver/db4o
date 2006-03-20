package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.Uuid;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.test.replication.CollectionHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public abstract class AbstractReplicationConfiguratorTest {
	protected SessionFactory sessionFactory;
	protected Configuration cfg;

	protected ObjectConfig objectConfig;

	protected long initVer;

	protected long raisedVer;

	public AbstractReplicationConfiguratorTest() {

	}

	public void test() {
		oneRound();
		oneRound();
	}

	private void oneRound() {
		init();

		sessionFactory = cfg.buildSessionFactory();

		Session session = sessionFactory.openSession();
		initVer = Util.getMaxVersion(session.connection());
		raisedVer = initVer + 1;
		session.close();

		tstFirstClass();
		tstCollectionUpdate();
		tstCollectionRemove();

		clean();
	}

	protected abstract void init();

	protected void clean() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.createQuery("delete from CollectionHolder").executeUpdate();
		tx.commit();
		session.close();
		sessionFactory.close();
	}

	public void tstFirstClass() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();
		Transaction tx;
		tx = session.beginTransaction();

		session.save(ch);
		session.flush();

		Uuid uuid = getUuid(session, ch);

		ch._name = "changed";
		tx.commit();

		checkVersion(cfg, session, ch, raisedVer);

		tx = session.beginTransaction();
		session.delete(ch);
		session.flush();

		ensureDeleted(uuid);

		tx.commit();

		session.close();
	}

	abstract protected void ensureDeleted(Uuid uuid);

	abstract protected Uuid getUuid(Session session, Object aClass);

	public void tstCollectionUpdate() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx;
		tx = session.beginTransaction();

		session.save(ch);
		session.flush();

		ch._set.add("8");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch._list.add("88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch._map.put("88", "88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);
		tx.commit();

		tx = session.beginTransaction();
		session.delete(ch);
		tx.commit();

		session.close();
	}

	public void tstCollectionRemove() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();

		ch._set.add("8");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch._list.add("88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch._map.put("88", "88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch._set = null;
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch._list = null;
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch._map = null;
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		session.delete(ch);
		tx.commit();

		session.close();
	}

	protected abstract Session openSession();

	protected abstract void checkVersion(Configuration cfg, Session session, Object obj, long expected);
}
