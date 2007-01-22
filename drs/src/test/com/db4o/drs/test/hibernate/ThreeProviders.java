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
		Pilot pilot1 = new Pilot("Scott Felton", 200);
		a().provider().storeNew(pilot1);
		a().provider().commit();
		
		replicateAll(a().provider(), b().provider());

		Configuration hibernate = HibernateUtil.createNewDbConfig();
		hibernate.addClass(Pilot.class);
		
		ReplicationSession session = Replication.begin(b().provider(), 
				new HibernateReplicationProviderImpl(hibernate));
		
		ObjectSet changed = session.providerA().objectsChangedSinceLastReplication();
		
		while (changed.hasNext()) {
			Object obj = changed.next();
			session.replicate(obj);
		}

		session.commit();
	}
}
