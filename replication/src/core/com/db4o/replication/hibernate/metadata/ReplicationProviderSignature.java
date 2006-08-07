package com.db4o.replication.hibernate.metadata;

import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;

import java.util.Arrays;

//TODO make abstract
public class ReplicationProviderSignature implements ReadonlyReplicationProviderSignature {
	/**
	 * Table for storing ReplicationProviderSignature byte[].
	 */
	public static final String TABLE_NAME = "db4o_replication_provider_signatures";

	/**
	 * Column name of the ReplicationProviderSignature byte_array.
	 */
	public static final String BYTES = "bytes";

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
