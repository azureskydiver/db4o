/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

import java.util.Arrays;

import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;

/**
 * Uniquely identifies a RDBMS.
 * @author Albert Kwan
 *
 * @see PeerSignature
 * @see MySignature
 * 
 * @version 1.1
 * @since dRS 1.1
 */
public abstract class ProviderSignature 
	implements ReadonlyReplicationProviderSignature {
	
	public static class Table {
		public static final String NAME = "drs_provider_signatures";
	}
	
	public static class Fields {
		public static final String ID = "id";
		public static final String BYTES = "bytes";
	}

	/**
	 * unique identifier for this ProviderSignature.
	 */
	private byte[] bytes;

	/**
	 * 1 to 1 identifier of "bytes", for space usage optimization. 
	 * Serves as the primary key in relational table.
	 */
	private long id;

	/**
	 * legacy field used by pre-dRS db4o replication code.
	 * @deprecated
	 */
	private long creationTime;
	
	public ProviderSignature() {}

	public ProviderSignature(byte[] signature) {
		this.bytes = signature;
		this.creationTime = System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ProviderSignature that = (ProviderSignature) o;

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
		return "ProviderSignature{" +
				"bytes=" + bytes +
				", id=" + id +
				", creationTime=" + creationTime +
				'}';
	}
}
