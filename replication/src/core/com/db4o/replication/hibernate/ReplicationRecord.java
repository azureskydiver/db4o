package com.db4o.replication.hibernate;

public class ReplicationRecord {
	static final String TABLE_NAME = "ReplicationRecord";

	static final String VERSION = "version";

	private long version;

	private PeerSignature peerSignature;

	public ReplicationRecord() {
		version = 0;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public PeerSignature getPeerSignature() {
		return peerSignature;
	}

	public void setPeerSignature(PeerSignature peerSignature) {
		this.peerSignature = peerSignature;
	}

	public String toString() {
		return "peerSignature = " + peerSignature + ", version = " + version;
	}
}