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
 * @see ReplicationProvider
 * @see ReplicationEventListener
 * @since dRS 1.0
 */
public class Replication {
// -------------------------- STATIC METHODS --------------------------

	/**
	 * begins a replication session between two ReplicationProviders, no conflict
	 * resolver
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(ReplicationProvider providerA, ReplicationProvider providerB) {
		return begin(providerA, providerB, null);
	}

	/**
	 * begins a replication session between db4o and db4o, no conflict resolver.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(ObjectContainer oc1, ObjectContainer oc2) {
		return begin(oc1, oc2, null);
	}

	/**
	 * begins a replication session between two ReplicatoinProviders
	 */
	public static ReplicationSession begin(ReplicationProvider providerA, ReplicationProvider providerB, ReplicationEventListener listener) {
		if (listener == null) {
			listener = new DefaultReplicationEventListener();
		}
		return new GenericReplicationSession(providerA, providerB, listener);
	}

	/**
	 * begins a replication session between db4o and db4o.
	 */
	public static ReplicationSession begin(ObjectContainer oc1, ObjectContainer oc2, ReplicationEventListener listener) {
		return begin(wrap(oc1), wrap(oc2), listener);
	}

	public static ReplicationProvider wrap(Object obj) {
		if (obj instanceof ObjectContainer) {
			return new Db4oReplicationProvider((ObjectContainer) obj);
		}
		throw new IllegalArgumentException();
	}
}
