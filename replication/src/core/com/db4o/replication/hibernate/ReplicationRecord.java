package com.db4o.replication.hibernate;

public class ReplicationRecord {
    
	/**
	 * Table for storing ReplicationRecord.
	 */
	public static String TABLE_NAME = "ReplicationRecord";

	public static String VERSION = "version";

	public long version;

	public long peerId;

	public ReplicationRecord() {
		version = 0;
	}

	public String toString() {
		return "peerId = " + peerId + ", version = " + version;
	}
}