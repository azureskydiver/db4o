/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;

/**
 * Specifies a socket connection via a socket factory and a port number.
 */
public class SocketSpec {
	
	private final int _port;
	private final NativeSocketFactory _factory;

	public SocketSpec(int port, NativeSocketFactory factory) {
		_port = port;
		_factory = factory;
	}

	public int port() {
		return _port;
	}

	public NativeSocketFactory socketFactory() {
		return _factory;
	}
	
	
}
