/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLContext;

import com.db4o.config.NativeSocketFactory;

public class SecureSocketFactory implements NativeSocketFactory {

	private SSLContext _context;

	public SecureSocketFactory(SSLContext context) {
		_context = context;
	}

	public ServerSocket createServerSocket(int port) throws IOException {
		System.out.println("SERVER on " + port);
		return _context.getServerSocketFactory().createServerSocket(port);
	}

	public Socket createSocket(String hostName, int port) throws IOException {
		System.out.println("CLIENT on " + port);
		return _context.getSocketFactory().createSocket(hostName, port);
	}

	public Object deepClone(Object context) {
		return this;
	}

}
