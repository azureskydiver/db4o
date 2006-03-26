package com.db4o.replication.hibernate.metadata;

public class ReplicationRecord {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "ReplicationRecord";

	public static final String VERSION = "version";

	private long version;

	private PeerSignature peerSignature;

// --------------------------- CONSTRUCTORS ---------------------------

	public ReplicationRecord() {
		version = 0;
	}

// --------------------- GETTER / SETTER METHODS ---------------------

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

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return "peerSignature = " + peerSignature + ", version = " + version;
	}
}