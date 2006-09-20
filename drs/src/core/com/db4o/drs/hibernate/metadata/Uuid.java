/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.drs.hibernate.metadata;

/**
 * Uniquely identifies a persisted object.
 * 
 * @author Albert Kwan
 *
 * @version 1.2
 * @since dRS 1.0
 */
public class Uuid {
	public static class Table {
		public static final String CREATED = "created";
		public static final String PROVIDER = "provider_id";
	}
	
	public static class Fields {
		public static final String CREATED = "created";
		public static final String PROVIDER = "provider";	
	}
	
	/**
	 * An id that is unique across types within a provider.
	 */
	private long created;

	/**
	 * The provider that orginates this id.
	 */
	private ProviderSignature provider;
	
	public Uuid() {}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (created ^ (created >>> 32));
		result = PRIME * result + ((provider == null) ? 0 : provider.hashCode());
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
		final Uuid other = (Uuid) obj;
		if (created != other.created)
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		return true;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public ProviderSignature getProvider() {
		return provider;
	}

	public void setProvider(ProviderSignature provider) {
		this.provider = provider;
	}
}
