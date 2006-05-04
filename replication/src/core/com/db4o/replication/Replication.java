/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.ObjectContainer;
import com.db4o.inside.replication.DefaultReplicationEventListener;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.db4o.Db4oReplicationProvider;

/**
 * Factory to create ReplicationSessions.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @see com.db4o.replication.hibernate.HibernateReplication
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
		return begin(new Db4oReplicationProvider(oc1), new Db4oReplicationProvider(oc2), listener);
	}
}
