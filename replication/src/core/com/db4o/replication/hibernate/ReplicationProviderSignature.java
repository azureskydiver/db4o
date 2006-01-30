package com.db4o.replication.hibernate;

import com.db4o.inside.replication.*;

import java.util.Arrays;

public class ReplicationProviderSignature implements ReadonlyReplicationProviderSignature {

    /**
     * Table for storing ReplicationProviderSignature byte[]. Each signature
     * is identified by a {@link #SIGNATURE_ID_COLUMN_NAME}
     */
    static final String TABLE_NAME = "ReplicationProviderSignature";

    /**
     * Column name of the ReplicationProviderSignature byte_array.
     */
    static final String SIGNATURE_BYTE_ARRAY_COLUMN_NAME = "bytes";

    /**
     * Unique ID for {@link #SIGNATURE_BYTE_ARRAY_COLUMN_NAME}.
     */
    static final String SIGNATURE_ID_COLUMN_NAME = "drs_provider_id";


    private byte[] bytes;

	private long id;

    private long creationTime;

	public ReplicationProviderSignature() {
	}

	public ReplicationProviderSignature(byte[] signature) {
		this.bytes = signature;
        this.creationTime = System.currentTimeMillis();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReplicationProviderSignature that = (ReplicationProviderSignature) o;

		return Arrays.equals(bytes, that.bytes);
	}

	public int hashCode() {
		return 0;
	}

	public String toString() {
		return getClass() + ", id = " + getId() + ", bytes = " + flattenBytes(bytes);
	}

	protected static String flattenBytes(byte[] b) {
		String out = "";
		for (int i = 0; i < b.length; i++) {
			out += ", " + b[i];
		}
		return out;
	}
}
