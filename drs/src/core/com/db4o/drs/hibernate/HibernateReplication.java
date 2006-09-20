package com.db4o.drs.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.ObjectContainer;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationConflictException;
import com.db4o.drs.ReplicationEventListener;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.db4o.Db4oReplicationProvider;
import com.db4o.drs.hibernate.impl.HibernateReplicationProviderImpl;

/**
 * Factory to create ReplicationSessions.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @see com.db4o.drs.Replication
 * @see com.db4o.drs.ReplicationProvider
 * @see ReplicationEventListener
 * @since dRS 1.2
 */
public class HibernateReplication {
	/**
	 * Begins a replication session between db4o and Hibernate without ReplicationEventListener.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg) {
		return begin(oc, cfg, null);
	}

	/**
	 * Begins a replication session between Hibernate and Hibernate without ReplicationEventListener.
	 *
	 * @throws ReplicationConflictException when conflicts occur
	 * @see ReplicationEventListener
	 */
	public static ReplicationSession begin(Configuration cfg1, Configuration cfg2) {
		return begin(cfg1, cfg2, null);
	}

	/**
	 * Begins a replication session between db4o and Hibernate.
	 */
	public static ReplicationSession begin(ObjectContainer oc, Configuration cfg,
			ReplicationEventListener listener) {
		return Replication.begin(new Db4oReplicationProvider(oc), new HibernateReplicationProviderImpl(cfg), listener);
	}

	/**
	 * Begins a replication session between Hibernate and Hibernate
	 */
	public static ReplicationSession begin(Configuration cfg1, Configuration cfg2,
			ReplicationEventListener listener) {
		return Replication.begin(new HibernateReplicationProviderImpl(cfg1), new HibernateReplicationProviderImpl(cfg2),
				listener);
	}
}
