/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Holds metadata of a persisted object.
 * 
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.1
 */
public class ReplicationRecord {
	public static class Table {
		public static final String NAME = "drs_replication_records";
	}
	
	public static class Fields {
		public static final String PEER_SIGNATURE = "peerSignature";
		public static final String VERSION = "version";
	}

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