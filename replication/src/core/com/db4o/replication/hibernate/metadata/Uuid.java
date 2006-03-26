package com.db4o.replication.hibernate.metadata;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Uuid {
// ------------------------------ FIELDS ------------------------------

	public static final String LONG_PART = "longPart";
	public static final String PROVIDER = "provider";

	private long longPart;

	private ReplicationProviderSignature provider;

// --------------------------- CONSTRUCTORS ---------------------------

	public Uuid() {
	}

// --------------------- GETTER / SETTER METHODS ---------------------

	public long getLongPart() {
		return longPart;
	}

	public void setLongPart(long longPart) {
		this.longPart = longPart;
	}

	public ReplicationProviderSignature getProvider() {
		return provider;
	}

	public void setProvider(ReplicationProviderSignature provider) {
		this.provider = provider;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public boolean equals(Object o) {
		System.out.println("Uuid.equals");
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Uuid uuid = (Uuid) o;

		if (longPart != uuid.longPart) return false;

		return true;
	}

	public int hashCode() {
		return (int) (longPart ^ (longPart >>> 32));
	}

	public String toString() {
		return new ToStringBuilder(this).
				append(LONG_PART, longPart).
				append(PROVIDER, provider).
				toString();
	}
}
