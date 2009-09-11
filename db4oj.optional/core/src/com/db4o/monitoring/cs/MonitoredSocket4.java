/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import java.io.*;
import java.nio.channels.*;

import com.db4o.foundation.network.*;

@decaf.Ignore
class MonitoredSocket4 extends Socket4Decorator {
	public MonitoredSocket4(Socket4 socket) {
		super(socket);
	}
	
	public MonitoredSocket4(Socket4 socket, Networking bean) {
		super(socket);
	
		_bean = bean;
	}

	public void write(byte[] bytes, int offset, int count) throws IOException {
		super.write(bytes, offset, count);
		bean().notifyWrite(count);
	}
	
	@Override
	public int read(byte[] buffer, int offset, int count) throws IOException {
		int bytesReceived = super.read(buffer, offset, count);
		_bean.notifyRead(bytesReceived);
		
		return bytesReceived;
	}

	@Override
	public void close() throws IOException {
		super.close();
	}
	
	protected Networking bean() {
		if (null == _bean) {
			_bean = produceMBean();
		}
		
		return _bean;
	}
	
	protected Networking produceMBean() {
		throw new IllegalSelectorException();
	}

	protected Networking _bean;
}