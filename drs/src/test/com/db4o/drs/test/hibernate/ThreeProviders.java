package com.db4o.drs.test.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.drs.test.DrsTestCase;
import com.db4o.drs.test.Pilot;

public class ThreeProviders extends DrsTestCase {
	public void test() {
		replicateAll(a().provider(), b().provider());

		Configuration hibernate = HibernateUtil.createNewDbConfig();
		hibernate.addClass(Pilot.class);
		
		ReplicationSession session = Replication.begin(b().provider(), 
				new HibernateReplicationProviderImpl(hibernate));
		
		ObjectSet changed = session.providerA().objectsChangedSinceLastReplication();
		
		while (changed.hasNext()) {
			Object obj = changed.next();
			System.out.println(obj);
			session.replicate(obj);
		}

		session.commit();
		session.close();
	}

	protected void store() {
		Pilot pilot1 = new Pilot("Scott Felton", 200);
		a().provider().storeNew(pilot1);
	}
}
