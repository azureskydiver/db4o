/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;

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
	 * Get the object from this provider.
	 *
	 * @param uuid the UUID of the object
	 * @return the object
	 */
	Object getObject(Db4oUUID uuid);

	/**
	 * Returns newly created objects and changed objects since last replication.
	 *
	 * @return newly created objects and changed objects since last replication
	 */
	ObjectSet objectsChangedSinceLastReplication();

	/**
	 * Returns newly created objects and changed objects since last replication..
	 *
	 * @param clazz the type of objects interested
	 * @return newly created objects and changed objects of the type specified in the clazz parameter since last replication
	 */
	ObjectSet objectsChangedSinceLastReplication(Class clazz);

	/**
	 * Returns the UUIDs of the objects deleted since last replication.
	 *
	 * @return newly created objects and changed objects since last replication
	 */
	ObjectSet uuidsDeletedSinceLastReplication();
}
