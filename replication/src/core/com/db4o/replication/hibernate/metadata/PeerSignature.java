package com.db4o.replication.hibernate.metadata;

public class PeerSignature extends ReplicationProviderSignature {
	public PeerSignature() {
		super();
	}

	public PeerSignature(byte[] bytes) {
		super(bytes);
	}
}
