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
 * @version 1.2
 * @since dRS 1.1
 */
public abstract class ProviderSignature 
	implements ReadonlyReplicationProviderSignature {
	
	public static class Fields {
		public static final String ID = "id";
		public static final String SIG = "signature";
	}

	/**
	 * unique identifier for this ProviderSignature.
	 */
	private byte[] signature;

	/**
	 * 1 to 1 identifier of "signature", for space usage optimization. 
	 * Serves as the primary key in relational table.
	 */
	private long id;

	/**
	 * legacy field used by pre-dRS db4o replication code.
	 * @deprecated
	 */
	private long created;
	
	public ProviderSignature() {
		this.created = System.currentTimeMillis();
	}

	public ProviderSignature(byte[] signature) {
		this();
		this.signature = signature;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + Arrays.hashCode(signature);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ProviderSignature other = (ProviderSignature) obj;
		if (!Arrays.equals(signature, other.signature))
			return false;
		return true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
}
