/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs;

/**
 * Defines the contract for handling of replication events generated from a replication session.
 * Users can implement this interface to resolve replication conflicts according to their own business rules.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @since dRS 1.2
 */
public interface ReplicationEventListener {
	/**
	 * invoked when a replication of an object occurs.
	 *
	 * @param e
	 */
	void onReplicate(ReplicationEvent e);
}
