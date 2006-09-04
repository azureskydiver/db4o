/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

/**
 * Holds metadata of a persisted object.
 * 
 * @author Albert Kwan
 *
 * @version 1.2
 * @since dRS 1.1
 */
public class Record {
	
	public static class Fields {
		public static final String TIME = "time";
		public static final String PEER_SIGNATURE = "peerSignature";
	}

	private long time;

	private PeerSignature peerSignature;

	public Record() {
		time = 0;
	}

	public PeerSignature getPeerSignature() {
		return peerSignature;
	}

	public void setPeerSignature(PeerSignature peerSignature) {
		this.peerSignature = peerSignature;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long version) {
		this.time = version;
	}
}