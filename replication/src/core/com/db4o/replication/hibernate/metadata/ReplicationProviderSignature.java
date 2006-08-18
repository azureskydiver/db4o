package com.db4o.replication.hibernate.metadata;

import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;

import java.util.Arrays;

public abstract class ReplicationProviderSignature 
	implements ReadonlyReplicationProviderSignature {
	
	public static class Table {
		public static final String NAME = "drs_replication_provider_signatures";
		
	}
	
	public static class Fields {
		public static final String ID = "id";
		public static final String BYTES = "bytes";
	}

	private byte[] bytes;

	private long id;

	private long creationTime;
	
	public ReplicationProviderSignature() {
	}

	public ReplicationProviderSignature(byte[] signature) {
		this.bytes = signature;
		this.creationTime = System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReplicationProviderSignature that = (ReplicationProviderSignature) o;

		if (creationTime != that.creationTime) return false;
		if (id != that.id) return false;
		if (!Arrays.equals(bytes, that.bytes)) return false;

		return true;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] signature) {
		this.bytes = signature;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int hashCode() {
		int result;
		result = (int) (id ^ (id >>> 32));
		result = 29 * result + (int) (creationTime ^ (creationTime >>> 32));
		return result;
	}

	public String toString() {
		return "ReplicationProviderSignature{" +
				"bytes=" + bytes +
				", id=" + id +
				", creationTime=" + creationTime +
				'}';
	}
}
