/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Identifies the peer ReplicationProvider during a ReplicationSession.
 * 
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.1
 */
public class PeerSignature extends ReplicationProviderSignature {
	public PeerSignature() {
		super();
	}

	public PeerSignature(byte[] bytes) {
		super(bytes);
	}
}
