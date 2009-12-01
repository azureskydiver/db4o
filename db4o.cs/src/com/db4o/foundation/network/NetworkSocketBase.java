/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */
package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

import com.db4o.internal.*;

public abstract class NetworkSocketBase implements Socket4 {

	private String _hostName;

	public NetworkSocketBase() {
		this(null);
    }

    public NetworkSocketBase(String hostName) {
        _hostName=hostName;
    }

	public void close() throws IOException {
		socket().close();
	}

	public void flush() throws IOException {
		socket().getOutputStream().flush();
	}

	public boolean isConnected() {
	    return Platform4.isConnected(socket());
	}

	public int read(byte[] a_bytes, int a_offset, int a_length) throws IOException {
				int ret = socket().getInputStream().read(a_bytes, a_offset, a_length);
				checkEOF(ret);
				return ret;
			}

	private void checkEOF(int ret) throws IOException {
		if(ret == -1) {
			throw new IOException();
		}
	}

	public void setSoTimeout(int timeout) {
	    try {
	        socket().setSoTimeout(timeout);
	    } 
	    catch (SocketException e) {
	        e.printStackTrace();
	    }
	}

	public void write(byte[] bytes, int off, int len) throws IOException {
		socket().getOutputStream().write(bytes,off,len);
	}

	public Socket4 openParallelSocket() throws IOException {
		if(_hostName==null) {
			throw new IllegalStateException();
		}
		return createParallelSocket(_hostName, socket().getPort());
	}

	protected abstract Socket4 createParallelSocket(String hostName, int port) throws IOException;

	@Override
	public String toString() {
		return socket().toString();
	}
	
	protected abstract Socket socket();
}