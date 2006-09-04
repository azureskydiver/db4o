package com.db4o.inside.replication;

public interface ReadonlyReplicationProviderSignature {
	long getId();

	byte[] getSignature();

	long getCreated();
}
