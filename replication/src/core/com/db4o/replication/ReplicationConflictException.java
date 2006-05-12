/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.ext.Db4oException;

/**
 * Thrown when a conflict occurs and no ReplicationEventListener is specified.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @see ReplicationEventListener
 * @since dRS 1.2
 */
public class ReplicationConflictException extends Db4oException {
	public ReplicationConflictException(String message) {
		super(message);
	}
}
