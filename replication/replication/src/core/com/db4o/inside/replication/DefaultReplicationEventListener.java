/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.replication;

import com.db4o.replication.*;

/**
 A default implementation of ConflictResolver. In case of a conflict
 a {@link com.db4o.replication.ReplicationConflictException}
 is thrown.
 
 @author Albert Kwan
 @author Carl Rosenberger
 @author Klaus Wuestefeld
 @version 1.0
 @since dRS 1.0 */
public class DefaultReplicationEventListener implements ReplicationEventListener {

	public void onReplicate(ReplicationEvent e) {}
	
}
