/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.config;

import java.io.*;
import java.net.*;

/**
 * Create raw platform native sockets.
 */
public class PlainSocketFactory implements NativeSocketFactory {

	public ServerSocket createServerSocket(int port) throws IOException {
		return new ServerSocket(port);
	}

	public Socket createSocket(String hostName, int port) throws IOException {
		return new Socket(hostName, port);
	}

	public Object deepClone(Object context) {
		return this;
	}

}
