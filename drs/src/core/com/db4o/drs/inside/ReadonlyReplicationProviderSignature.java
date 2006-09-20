package com.db4o.drs.inside;

public interface ReadonlyReplicationProviderSignature {
	long getId();

	byte[] getSignature();

	long getCreated();
}
