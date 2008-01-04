/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench.logging.replay;

public class ReadWriteCommand {
	
	protected final int _length;
	
	public ReadWriteCommand(int length) {
		_length = length;
	}

	protected byte[] prepareBuffer() {
		return new byte[_length];
	}

}
