/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs;

import com.db4o.ObjectContainer;
import com.db4o.drs.db4o.Db4oProviderFactory;
import com.db4o.drs.inside.DefaultReplicationEventListener;
import com.db4o.drs.inside.GenericReplicationSession;

/**
 * Factory to create ReplicationSessions.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @see com.db4o.drs.hibernate.HibernateReplication
 * @see ReplicationProvider
 * @see ReplicationEventListener
 * @since dRS 1.0
 */
public class Replication {
	/**
	 * Begins a replication session between two ReplicationProviders without ReplicationEventListener.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(ReplicationProvider providerA, ReplicationProvider providerB) {
		return begin(providerA, providerB, null);
	}

	/**
	 * Begins a replication session between db4o and db4o without ReplicationEventListener.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(ObjectContainer oc1, ObjectContainer oc2) {
		return begin(oc1, oc2, null);
	}

	/**
	 * Begins a replication session between two ReplicatoinProviders.
	 */
	public static ReplicationSession begin(ReplicationProvider providerA, ReplicationProvider providerB,
			ReplicationEventListener listener) {
		if (listener == null) {
			listener = new DefaultReplicationEventListener();
		}
		return new GenericReplicationSession(providerA, providerB, listener);
	}

	/**
	 * Begins a replication session between db4o and db4o.
	 */
	public static ReplicationSession begin(ObjectContainer oc1, ObjectContainer oc2,
			ReplicationEventListener listener) {
		return begin(Db4oProviderFactory.newInstance(oc1), Db4oProviderFactory.newInstance(oc2), listener);
	}
}
