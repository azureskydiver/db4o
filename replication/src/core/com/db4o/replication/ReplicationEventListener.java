/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.inside.replication.DefaultReplicationEventListener;

/**
 * Users can implement this interface to resolve replication conflicts
 * according to their own business rules.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.0
 * @see DefaultReplicationEventListener
 * @since dRS 1.0
 */
public interface ReplicationEventListener {

	void onReplicate(ReplicationEvent event);
    
}
