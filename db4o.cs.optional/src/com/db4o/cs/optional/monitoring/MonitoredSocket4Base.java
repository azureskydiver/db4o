/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.cs.optional.monitoring;

import java.io.*;

import com.db4o.cs.foundation.*;

/**
 * @exclude
 */
@decaf.Ignore
abstract class MonitoredSocket4Base extends Socket4Decorator {
	public MonitoredSocket4Base(Socket4 socket) {
		super(socket);
	}

	public void write(byte[] bytes, int offset, int count) throws IOException {
		super.write(bytes, offset, count);
		bean().notifyWrite(count);
	}
	
	@Override
	public int read(byte[] buffer, int offset, int count) throws IOException {
		int bytesReceived = super.read(buffer, offset, count);
		bean().notifyRead(bytesReceived);
		
		return bytesReceived;
	}
	
	protected abstract Networking bean();
}