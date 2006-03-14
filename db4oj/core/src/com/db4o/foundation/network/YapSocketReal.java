/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

public class YapSocketReal implements YapSocket {

    private Socket _socket;
    private OutputStream _out;
    private InputStream _in;
    private String _hostName;
    
    public YapSocketReal(String hostName, int port) throws IOException {
        this(new Socket(hostName, port));
        _hostName=hostName;
    }

    public YapSocketReal(Socket socket) throws IOException {
    	_socket = socket;
    	_out = _socket.getOutputStream();
    	_in = _socket.getInputStream();
    }

    public void close() throws IOException {
        //    	i_socket.getInputStream().close();
        //    	i_socket.getOutputStream().close();
        _socket.close();
    }

    public void flush() throws IOException {
        _out.flush();
    }
    
    public int read() throws IOException {
        return _in.read();
    }

    public int read(byte[] a_bytes, int a_offset, int a_length) throws IOException {
        return _in.read(a_bytes, a_offset, a_length);
    }

    public void setSoTimeout(int timeout) {
        try {
            _socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

	public void write(byte[] bytes) throws IOException {
	    _out.write(bytes);
	}

    public void write(byte[] bytes,int off,int len) throws IOException {
        _out.write(bytes,off,len);
    }

    public void write(int i) throws IOException {
        _out.write(i);
    }
    
	public YapSocket openParalellSocket() throws IOException {
		if(_hostName==null) {
			throw new IOException("Cannot open parallel socket - invalid state.");
		}
		return new YapSocketReal(_hostName,_socket.getPort());
	}
}
