package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.ReplicationProviderSignature;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ReplicationReference {
	public static final String TABLE_NAME = "ReplicationReference";
	public static final String CLASS_NAME = "className";
	public static final String OBJECT_ID = "objectId";
	public static final String UUID_LONG_PART = "uuidLongPart";
	public static final String PROVIDER = "provider";
	public static final String VERSION = "version";

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

	public String toString() {
		return new ToStringBuilder(this).
				append("className", className).
				append("objectId", objectId).
				append("uuidLongPart", uuidLongPart).
				append("version", version).
				append("provider", provider).
				toString();
	}
}
