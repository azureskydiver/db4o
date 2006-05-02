package com.db4o.replication.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.ObjectContainer;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationConflictException;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.replication.ReplicationSession;

public class HibernateReplication {

	/**
	 * begins a replication session between Hibernate and Hibernate, no conflict
	 * resolver
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(Configuration cfg1, Configuration cfg2) {
		return HibernateReplication.begin(cfg1, cfg2, null);
	}

	/**
	 * begins a replication session between db4o and Hibernate, no conflict
	 * resolver.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg) {
		return HibernateReplication.begin(oc, cfg, null);
	}

	/**
	 * begins a replication session between Hibernate and Hibernate
	 */
	public static ReplicationSession begin(Configuration cfg1, Configuration cfg2, ReplicationEventListener listener) {
		return Replication.begin(Replication.wrap(cfg1), Replication.wrap(cfg2), listener);
	}

	/**
	 * begins a replication session between db4o and Hibernate
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg, ReplicationEventListener listener) {
		return Replication.begin(Replication.wrap(oc), Replication.wrap(cfg), listener);
	}

}
