/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.inside.replication.DefaultConflictResolver;

/**
 * Resolves conflicts between two copies of an object during replication.
 * Conflicts occur if objects were changed in both ReplicationProviders since
 * the last time the two ReplicationProviders were replicated against eachother.
 * <p/>
 * Users should implement this interface to resolve conflicts according to their
 * own business rules.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.0
 * @see DefaultConflictResolver
 * @since dRS 1.0
 */
public interface ConflictResolver {

	/**
	 * Callback method, called by ReplicationSession when a conflict occurs.
	 *
	 * @param session ReplicationSession calling this method
	 * @param copyA   copy of the object from Provider A (null if the object was deleted)
	 * @param copyB   copy of the object from Provider B (null if the object was deleted)
	 *
	 * @return either ConflictResolver.A_PREVAILS, ConflictResolver.B_PREVAILS
     * or ConflictResolver.DO_NOTHING. If DO_NOTHING is returned then the object
     * is skipped and no state is replicated in either direction.
	 */
	public int resolveConflict(ReplicationSession session, Object copyA, Object copyB);

    static final int A_PREVAILS = 1;
    static final int B_PREVAILS = 2;
    static final int DO_NOTHING = 3;
    
}
