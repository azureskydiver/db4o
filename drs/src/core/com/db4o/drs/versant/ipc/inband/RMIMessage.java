/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.metadata.*;

public class RMIMessage extends CobraPersistentObject {
	
	private int sender;
	private byte[] buffer;
	
	public RMIMessage(int sender, byte[] buffer) {
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

}
