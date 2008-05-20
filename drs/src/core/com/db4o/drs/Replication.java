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
package com.db4o.drs;

import com.db4o.ObjectContainer;
import com.db4o.drs.db4o.Db4oProviderFactory;
import com.db4o.drs.inside.*;

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
		ReplicationReflector reflector = new ReplicationReflector(providerA, providerB);
		providerA.replicationReflector(reflector);
		providerB.replicationReflector(reflector);
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
