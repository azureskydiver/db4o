package com.db4o.test.replication.hibernate;

import com.db4o.replication.ReplicationConfigurator;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.Uuid;
import com.db4o.test.Test;
import com.db4o.test.replication.CollectionHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ReplicationConfiguratorTest {
// ------------------------------ FIELDS ------------------------------

	protected SessionFactory sessionFactory;
	protected Configuration cfg;

	protected long initVer;

	protected long raisedVer;

// --------------------------- CONSTRUCTORS ---------------------------

	public ReplicationConfiguratorTest() {

	}

	protected void checkVersion(Configuration cfg, Session session, Object obj, long expected) {
		long actual = Util.getVersion(session.connection(), obj.getClass().getName(), Util.castAsLong(session.getIdentifier(obj)));
		Test.ensureEquals(expected, actual);
	}

	protected void clean() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		session.createQuery("delete from CollectionHolder").executeUpdate();
		tx.commit();
		session.close();
		sessionFactory.close();
	}

	protected void ensureDeleted(Session session, Uuid uuid) {
		Test.ensure(Util.getByUUID(session, uuid).isDeleted());
	}

	protected Uuid getUuid(Session session, Object obj) {
		return Util.getUuid(session, obj);
	}

	protected void init() {
		cfg = prepareCfg();
		Util.addClass(cfg, CollectionHolder.class);
		ReplicationConfigurator.configure(cfg);
	}

	protected Configuration prepareCfg() {return HibernateUtil.createNewDbConfig();}

	protected Session openSession() {
		Session session = sessionFactory.openSession();
		//session.setFlushMode(FlushMode.COMMIT);
		ReplicationConfigurator.install(session, cfg);
		return session;
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

	public void test() {
		oneRound();
		oneRound();
	}

	public void tstCollectionRemove() {
		CollectionHolder ch = new CollectionHolder();

		Session session = openSession();

		Transaction tx = session.beginTransaction();

		session.save(ch);
		session.flush();

		ch.set.add("8");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch.list.add("88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch.map.put("88", "88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch.set = null;
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch.list = null;
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch.map = null;
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		session.delete(ch);
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

		ch.set.add("8");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch.list.add("88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);

		ch.map.put("88", "88");
		session.flush();
		checkVersion(cfg, session, ch, raisedVer);
		tx.commit();

		tx = session.beginTransaction();
		session.delete(ch);
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

		checkVersion(cfg, session, ch, raisedVer);

		tx = session.beginTransaction();
		session.delete(ch);
		session.flush();

		ensureDeleted(session, uuid);

		tx.commit();

		session.close();
	}
}
