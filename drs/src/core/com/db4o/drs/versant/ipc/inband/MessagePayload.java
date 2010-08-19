/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.metadata.*;

public class MessagePayload extends CobraPersistentObject {
	
	private int sender;
	private byte[] buffer;
	private long consumedAt;
	
	public MessagePayload(int sender, byte[] buffer) {
		super();
		this.sender = sender;
		this.buffer = buffer;
	}

	public byte[] buffer() {
		return buffer;
	}
	
	public int sender() {
		return sender;
	}

	public void consumedAt(long timestamp) {
		this.consumedAt = timestamp;
	}

	public boolean consumed() {
		return consumedAt != 0;
	}
	
	public long consumedAt() {
		return consumedAt;
	}

}
