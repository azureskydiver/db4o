package com.db4o.drs.test.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationEvent;
import com.db4o.drs.ReplicationEventListener;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.impl.HibernateReplicationProvider;
import com.db4o.drs.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.drs.test.DrsTestCase;
import com.db4o.drs.test.Pilot;

public class RoundRobinWithManyProviders extends DrsTestCase {
	HibernateReplicationProvider providerC;

	public void test() {
		initProviderC();

		replicateAB();
		replicateBC();
		replicateCA();
	}

	private void initProviderC() {
		Configuration configC = HibernateUtil.createNewDbConfig();
		configC.addClass(Pilot.class);
		providerC = new HibernateReplicationProviderImpl(configC);
	}

	private void replicateAB() {
		Pilot pilot1 = new Pilot("Scott Felton", 200);
		a().provider().storeNew(pilot1);
		a().provider().commit();

		replicateAll(a().provider(), b().provider());
	}

	private void replicateBC() {
		ReplicationSession sess = Replication.begin(b().provider(), providerC);

		ObjectSet changed = sess.providerA()
				.objectsChangedSinceLastReplication();
		while (changed.hasNext())
			sess.replicate(changed.next());

		sess.commit();
	}

	private void replicateCA() {
		ReplicationEventListener resolver = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent e) {
				if (e.isConflict()) {
					// System.out.println("Conflict because C and A are never
					// replicated.So they don't have a replication record. This
					// makes both providers think the Pilot object was modified.
					// ");
					e.overrideWith(e.stateInProviderA());
				}
			}

		};
		ReplicationSession sess = Replication
				.begin(providerC, a().provider(), resolver);

		ObjectSet changed2 = sess.providerA()
				.objectsChangedSinceLastReplication();
		while (changed2.hasNext())
			sess.replicate(changed2.next());

		sess.commit();
	}
}
