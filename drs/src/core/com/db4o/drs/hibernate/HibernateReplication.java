/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.ObjectContainer;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationConflictException;
import com.db4o.drs.ReplicationEventListener;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.db4o.Db4oProviderFactory;
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
		return Replication.begin(Db4oProviderFactory.newInstance(oc), new HibernateReplicationProviderImpl(cfg), listener);
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
