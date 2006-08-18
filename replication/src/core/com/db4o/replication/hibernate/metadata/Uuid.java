package com.db4o.replication.hibernate.metadata;

public class Uuid {
	public static class Table {
		public static final String LONG_PART = "long_part";
		public static final String PROVIDER = "provider";
	}
	
	public static class Fields {
		public static final String LONG_PART = "longPart";
		public static final String PROVIDER = Table.PROVIDER;	
	}
	
	private long longPart;

	private ReplicationProviderSignature provider;
	
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

	public ReplicationProviderSignature getProvider() {
		return provider;
	}

	public void setProvider(ReplicationProviderSignature provider) {
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
