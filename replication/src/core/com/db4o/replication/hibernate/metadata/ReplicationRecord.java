package com.db4o.replication.hibernate.metadata;

public class ReplicationRecord {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "ReplicationRecord";

	public static final String VERSION = "version";

	private long version;

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	private PeerSignature peerSignature;

	public PeerSignature getPeerSignature() {
		return peerSignature;
	}

	public void setPeerSignature(PeerSignature peerSignature) {
		this.peerSignature = peerSignature;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	public ReplicationRecord() {
		version = 0;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return "peerSignature = " + peerSignature + ", version = " + version;
	}
}