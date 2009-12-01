/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */
package com.db4o.cs.optional.ssl;

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLSocketFactory;

import com.db4o.foundation.network.*;

@decaf.Ignore
public class SSLSocket4 extends NetworkSocketBase {

	private Socket _socket;
	private SSLSocketFactory _factory;

	public SSLSocket4(String hostName, int port, SSLSocketFactory factory) throws IOException {
		super(hostName);
		_factory = factory;
    	_socket = _factory.createSocket(hostName, port);
	}

	@Override
	protected Socket4 createParallelSocket(String hostName, int port) throws IOException {
		return new SSLSocket4(hostName, port, _factory);
	}

	@Override
	protected Socket socket() {
		return _socket;
	}

}
