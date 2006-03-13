/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.ObjectContainer;
import com.db4o.inside.replication.DefaultConflictResolver;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableReplicationProvider;
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
	 * begins a replication session between db4o and Hibernate. Use dRS columns to keep the uuids.
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg, ConflictResolver resolver) {
		return begin(wrap(oc), wrap(cfg, false), resolver);
	}

	/**
	 * begins a replication session between db4o and Hibernate, no conflict
	 * resolver.Use dRS columns to keep the uuids.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ConflictResolver
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg) {
		return begin(oc, cfg, false, null);
	}

	/**
	 * begins a replication session between db4o and Hibernate.
	 *
	 * @param refAsTables if true then dRS will keep the uuid in a "ReplicationReference" table
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg, boolean refAsTables, ConflictResolver resolver) {
		return begin(wrap(oc), wrap(cfg, refAsTables), resolver);
	}

	/**
	 * begins a replication session between db4o and Hibernate, no conflict
	 * resolver.
	 *
	 * @param refAsTables if true then dRS will keep the uuid in a "ReplicationReference" table
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ConflictResolver
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg, boolean refAsTables) {
		return begin(oc, cfg, refAsTables, null);
	}

	/**
	 * begins a replication session between Hibernate and Hibernate
	 *
	 * @param cfg1RefAsTables if true then dRS will keep the uuid in a "ReplicationReference" table
	 * @param cfg2RefAsTables if true then dRS will keep the uuid in a "ReplicationReference" table
	 */
	public static ReplicationSession begin(Configuration cfg1, boolean cfg1RefAsTables, Configuration cfg2, boolean cfg2RefAsTables, ConflictResolver resolver) {
		return begin(wrap(cfg1, cfg1RefAsTables), wrap(cfg2, cfg2RefAsTables), resolver);
	}

	/**
	 * begins a replication session between Hibernate and Hibernate, no conflict
	 * resolver
	 *
	 * @param cfg1RefAsTables if true then dRS will keep the uuid in a "ReplicationReference" table
	 * @param cfg2RefAsTables if true then dRS will keep the uuid in a "ReplicationReference" table
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ConflictResolver
	 */
	public static ReplicationSession begin(Configuration cfg1, boolean cfg1RefAsTables, Configuration cfg2, boolean cfg2RefAsTables) {
		return begin(wrap(cfg1, cfg1RefAsTables), wrap(cfg2, cfg2RefAsTables), null);
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

	private static ReplicationProvider wrap(ObjectContainer obj) {
		return new Db4oReplicationProvider(obj);
	}

	private static ReplicationProvider wrap(Configuration cfg, boolean refAsTables) {
		if (refAsTables)
			return new RefAsTableReplicationProvider(cfg);
		else
			return new RefAsColumnsReplicationProvider(cfg);
	}
}
