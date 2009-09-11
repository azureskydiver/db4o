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
		_messagesSent++;	
	}

	@Override
	public int read(byte[] buffer, int offset, int count) throws IOException {
		int bytesReceived = super.read(buffer, offset, count);
		_bytesReceived += bytesReceived;
		
		return bytesReceived;
	}
	
	public double bytesSent() {
		return _bytesSent;
	}

	public double bytesReceived() {
		return _bytesReceived;
	}

	public double messagesSent() {
		return _messagesSent;
	}
	
	public void resetCount() {
		_bytesSent = 0.0;
		_bytesReceived = 0.0;
		_messagesSent = 0.0;
	}
	
	private double _bytesSent;
	private double _bytesReceived;
	private double _messagesSent;
}
