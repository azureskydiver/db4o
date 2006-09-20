/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.ObjectSet;

/**
 * Facade for persistence systems that provide replication support.
 * Interacts with another ReplicationProvider and a  ReplicationSession
 * to allows replication of objects between two ReplicationProviders.
 * <p/>
 * <p/> To create an instance of this class, use the methods of {@link Replication}.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @see ReplicationSession
 * @see Replication
 * @since dRS 1.0
 */
public interface ReplicationProvider {
	/**
	 * Returns newly created objects and changed objects since last replication with the opposite provider.
	 *
	 * @return newly created objects and changed objects since last replication with the opposite provider.
	 */
	ObjectSet objectsChangedSinceLastReplication();

	/**
	 * Returns newly created objects and changed objects since last replication with the opposite provider.
	 *
	 * @param clazz the type of objects interested
	 * @return newly created objects and changed objects of the type specified in the clazz parameter since last replication
	 */
	ObjectSet objectsChangedSinceLastReplication(Class clazz);
}
