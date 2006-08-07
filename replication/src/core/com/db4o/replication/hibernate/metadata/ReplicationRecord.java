package com.db4o.replication.hibernate.metadata;

public class ReplicationRecord {
	public static final String TABLE_NAME = "db4o_replication_records";

	public static final String VERSION = "version";

	private long version;

	private PeerSignature peerSignature;

	public ReplicationRecord() {
		version = 0;
	}

	public PeerSignature getPeerSignature() {
		return peerSignature;
	}

	public void setPeerSignature(PeerSignature peerSignature) {
		this.peerSignature = peerSignature;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String toString() {
		return "peerSignature = " + peerSignature + ", version = " + version;
	}
}