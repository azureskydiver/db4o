/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.db4o.config.NativeSocketFactory;

public class SSLSocketFactory implements NativeSocketFactory {

	public ServerSocket createServerSocket(int port) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Socket createSocket(String hostName, int port) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object deepClone(Object context) {
		return this;
	}

}
