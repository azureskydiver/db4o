/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

public class NetworkSocket implements Socket4 {

    private Socket _socket;
    private OutputStream _out;
    private InputStream _in;
    private String _hostName;
    private NativeSocketFactory _factory;
    
    public NetworkSocket(NativeSocketFactory factory, String hostName, int port) throws Db4oIOException {
    	_factory = factory;
    	try {
			Socket socket = _factory.createSocket(hostName, port);
			initSocket(socket);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
        _hostName=hostName;
    }

    public NetworkSocket(NativeSocketFactory factory, Socket socket) throws IOException {
    	_factory = factory;
    	initSocket(socket);
    }

	private void initSocket(Socket socket) throws IOException {
		_socket = socket;
    	_out = _socket.getOutputStream();
    	_in = _socket.getInputStream();
	}

    public void close() throws Db4oIOException {
		try {
			_socket.close();
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}

    public void flush() throws Db4oIOException {
		try {
			_out.flush();
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}
    
    public boolean isConnected() {
        return Platform4.isConnected(_socket);
    }
    
    public int read() throws Db4oIOException {
        try {
        	int ret = _in.read();
        	checkEOF(ret);
        	return ret;
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
    }

    public int read(byte[] a_bytes, int a_offset, int a_length) throws Db4oIOException {
        try {
        	int ret = _in.read(a_bytes, a_offset, a_length);
        	checkEOF(ret);
			return ret;
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
    }
    
    private void checkEOF(int ret) {
    	if(ret == -1) {
    		throw new Db4oIOException();
    	}
    }

    public void setSoTimeout(int timeout) {
        try {
            _socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

	public void write(byte[] bytes) throws Db4oIOException {
	    try {
			_out.write(bytes);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}

    public void write(byte[] bytes,int off,int len) throws Db4oIOException {
        try {
			_out.write(bytes,off,len);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
    }

    public void write(int i) throws Db4oIOException {
        try {
			_out.write(i);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
    }
    
	public Socket4 openParalellSocket() throws Db4oIOException {
		if(_hostName==null) {
			throw new IllegalStateException();
		}
		return new NetworkSocket(_factory, _hostName,_socket.getPort());
	}
}
