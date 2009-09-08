/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import java.io.IOException;
import java.nio.channels.IllegalSelectorException;

import com.db4o.foundation.network.*;

@decaf.Ignore
class MonitoredSocket4 extends Socket4Decorator {
	public MonitoredSocket4(Socket4 socket) {
		this(socket, null);
	}
	
	public MonitoredSocket4(Socket4 socket, Networking bean) {
		super(socket);
		_bean = bean;
	}

	public void write(byte[] bytes, int offset, int count) throws IOException {
		super.write(bytes, offset, count);
		bean().notifyWrite(count);
	}


	public void write(byte b) throws IOException {
		super.write(b);
		bean().notifyWrite(1);
	}

	@Override
	public void close() throws IOException {
		super.close();
	}
	
	private Networking bean() {
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