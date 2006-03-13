package com.db4o.replication.hibernate.metadata;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DeletedObject {
	public static final String TABLE_NAME = "DeletedObject";
	public static final String UUID_LONG_PART = "uuidLongPart";
	public static final String PROVIDER = "provider";

	private long uuidLongPart;
	private ReplicationProviderSignature provider;

	public DeletedObject() {
	}

	public long getUuidLongPart() {
		return uuidLongPart;
	}

	public void setUuidLongPart(long uuidLongPart) {
		this.uuidLongPart = uuidLongPart;
	}

	public ReplicationProviderSignature getProvider() {
		return provider;
	}

	public void setProvider(ReplicationProviderSignature provider) {
		this.provider = provider;
	}


	public String toString() {
		return new ToStringBuilder(this).
				append("uuidLongPart", uuidLongPart).
				append("provider", provider).
				toString();
	}
}
