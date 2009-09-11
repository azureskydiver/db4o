/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

import com.db4o.internal.*;

public class NetworkSocket implements Socket4 {

    private Socket _socket;
    private OutputStream _out;
    private InputStream _in;
    private String _hostName;
    
    public NetworkSocket(String hostName, int port) throws IOException {
    	Socket socket = createSocket(hostName, port);
    	initSocket(socket);
    	
        _hostName=hostName;
    }

    protected Socket createSocket(String hostName, int port) throws IOException {
    	return new Socket(hostName, port);
	}

	public NetworkSocket(Socket socket) throws IOException {
    	initSocket(socket);
    }

	private void initSocket(Socket socket) throws IOException {
		_socket = socket;
    	_out = _socket.getOutputStream();
    	_in = _socket.getInputStream();
	}

    public void close() throws IOException {
		_socket.close();
	}

    public void flush() throws IOException {
		_out.flush();
	}
    
    public boolean isConnected() {
        return Platform4.isConnected(_socket);
    }
        
    public int read(byte[] a_bytes, int a_offset, int a_length) throws IOException {
    	int ret = _in.read(a_bytes, a_offset, a_length);
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
            _socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] bytes,int off,int len) throws IOException {
		_out.write(bytes,off,len);
    }
    
	public Socket4 openParallelSocket() throws IOException {
		if(_hostName==null) {
			throw new IllegalStateException();
		}
		return new NetworkSocket(_hostName,_socket.getPort());
	}
	
	@Override
	public String toString() {
		return _socket.toString();
	}
}
