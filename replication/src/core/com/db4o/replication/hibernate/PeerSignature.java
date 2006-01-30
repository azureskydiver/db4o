package com.db4o.replication.hibernate;

import com.db4o.Unobfuscated;

public class PeerSignature extends ReplicationProviderSignature {
	public PeerSignature() {
		super();
	}

	public PeerSignature(byte[] bytes) {
		super(bytes);
	}

	public static PeerSignature generateSignature() {
		return new PeerSignature(Unobfuscated.generateSignature());
	}
}
