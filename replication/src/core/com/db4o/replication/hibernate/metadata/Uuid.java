/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Uniquely identifies a persisted object.
 * 
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.1
 */
public class Uuid {
	public static class Table {
		public static final String LONG_PART = "long_part";
		public static final String PROVIDER = "drs_provider_id";
	}
	
	public static class Fields {
		public static final String LONG_PART = "longPart";
		public static final String PROVIDER = "provider";	
	}
	
	/**
	 * An id that is unique across types within a provider.
	 */
	private long longPart;

	/**
	 * The provider that orginates this id.
	 */
	private ProviderSignature provider;
	
	public Uuid() {}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Uuid uuid = (Uuid) o;

		if (longPart != uuid.longPart) return false;

		return true;
	}

	public long getLongPart() {
		return longPart;
	}

	public void setLongPart(long longPart) {
		this.longPart = longPart;
	}

	public ProviderSignature getProvider() {
		return provider;
	}

	public void setProvider(ProviderSignature provider) {
		this.provider = provider;
	}

	public int hashCode() {
		return (int) (longPart ^ (longPart >>> 32));
	}

	public String toString() {
		return "Uuid{" +
				"longPart=" + longPart +
				", provider=" + provider +
				'}';
	}
}
