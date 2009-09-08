/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

/**
 * @sharpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.optional.monitoring.cs;

import java.io.IOException;

import com.db4o.foundation.network.*;

public class CountingSocket4 extends Socket4Decorator {
	
	public CountingSocket4(Socket4 socket) {	
		super(socket);
	}

	public void write(byte[] bytes, int offset, int count) throws IOException {
		super.write(bytes, offset, count);
		_bytesSent += count;
	}

	public void write(byte b) throws IOException {
		super.write(b);		
		_bytesSent += 1;
	}
	
	public double getBytesSent() {
		return _bytesSent;
	}

	public void resetCount() {
		_bytesSent = 0.0;
	}
	
	private double _bytesSent = 0.0;
}
