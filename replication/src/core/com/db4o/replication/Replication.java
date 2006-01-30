/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.ObjectContainer;
import com.db4o.inside.replication.DefaultConflictResolver;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import org.hibernate.cfg.Configuration;

/**
 * Factory to create ReplicationSessions.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.0
 * @see ReplicationProvider
 * @see ConflictResolver
 * @see org.hibernate.cfg.Configuration
 * @since dRS 1.0
 */
public class Replication {

	/**
	 * begins a replication session between db4o and db4o.
	 */
	public static ReplicationSession begin(ObjectContainer oc1, ObjectContainer oc2, ConflictResolver resolver) {
		return begin(wrap(oc1), wrap(oc2), resolver);
	}

	/**
	 * begins a replication session between db4o and db4o, no conflict resolver.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ConflictResolver
	 */
	public static ReplicationSession begin(ObjectContainer oc1, ObjectContainer oc2) {
		return begin(oc1, oc2, null);
	}

	/**
	 * begins a replication session between db4o and Hibernate
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg, ConflictResolver resolver) {
		return begin(wrap(oc), wrap(cfg), resolver);
	}

	/**
	 * begins a replication session between db4o and Hibernate, no conflict
	 * resolver.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ConflictResolver
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg) {
		return begin(oc, cfg, null);
	}

	/**
	 * begins a replication session between Hibernate and Hibernate
	 */
	public static ReplicationSession begin(Configuration cfg1, Configuration cfg2, ConflictResolver resolver) {
		return begin(wrap(cfg1), wrap(cfg2), resolver);
	}

	/**
	 * begins a replication session between Hibernate and Hibernate, no conflict
	 * resolver
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ConflictResolver
	 */
	public static ReplicationSession begin(Configuration cfg1, Configuration cfg2) {
		return begin(cfg1, cfg2, null);
	}

	/**
	 * begins a replication session between two ReplicatoinProviders
	 */
	public static ReplicationSession begin(ReplicationProvider providerA, ReplicationProvider providerB, ConflictResolver resolver) {
		if (resolver == null) {
			resolver = new DefaultConflictResolver();
		}
		return new GenericReplicationSession(providerA, providerB, resolver);
	}

	/**
	 * begins a replication session between two ReplicationProviders, no conflict
	 * resolver
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ConflictResolver
	 */
	public static ReplicationSession begin(ReplicationProvider providerA, ReplicationProvider providerB) {
		return begin(providerA, providerB, null);
	}

	private static ReplicationProvider wrap(Object obj) {
		if (obj instanceof ObjectContainer) {
			return new Db4oReplicationProvider((ObjectContainer) obj);
		}
		if (obj instanceof Configuration) {
			return new HibernateReplicationProviderImpl((Configuration) obj);
		}

		throw new IllegalArgumentException();
	}
}
