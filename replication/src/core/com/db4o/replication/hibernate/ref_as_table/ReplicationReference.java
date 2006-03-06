package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.ReplicationProviderSignature;

public class ReplicationReference {
	public static final String TABLE_NAME = "ReplicationReference";

	private String className;
	private long objectId;

	private long uuidLongPart;
	private ReplicationProviderSignature provider;

	private long version;

	public ReplicationReference() {
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
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

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
