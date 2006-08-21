package com.db4o.test.replication.hibernate;

import java.util.Iterator;

import com.db4o.ObjectSet;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationSession;
import com.db4o.replication.hibernate.impl.HibernateReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplication;
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
import org.hibernate.cfg.Environment;

public class ReplicationConfiguratorTest extends ReplicationTestCase {
	protected SessionFactory sessionFactory;
	protected Configuration cfg;
	String reuseUrl;
	

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
			doDummyReplication();
			oneRound();
		}
	}

	/**
	 * Simulate real life application. Do a round of replication.
	 *
	 */
	private void doDummyReplication() {
		final ReplicationSession r = HibernateReplication.begin(cfg, HibernateUtil.createNewDbConfig());
		
		final ObjectSet changed = r.providerA().objectsChangedSinceLastReplication();
		while (changed.hasNext())
			r.replicate(changed.next());
		
		r.commit();
		r.close();
	}

	private void oneRound() {
		if (reuseUrl==null){
			cfg = HibernateUtil.createNewDbConfig();
			reuseUrl = cfg.getProperty(Environment.URL);
		} else {
			cfg = HibernateUtil.reuse(reuseUrl);
		}
		
		Util.addClass(cfg, CollectionHolder.class);
		ReplicationConfigurator.configure(cfg);

		sessionFactory = cfg.buildSessionFactory();

		Session session = sessionFactory.openSession();
		session.close();

		tstFirstClass();
		tstCollectionUpdate();
		tstCollectionRemove();
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

	protected Session openSession() {
		Session session = sessionFactory.openSession();
		//session.setFlushMode(FlushMode.COMMIT);
		ReplicationConfigurator.install(session, cfg);
		return session;
	}	
}
