/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;
import java.net.*;

class YapSocket {

    private Socket i_socket;
    private OutputStream i_out;
    private InputStream i_in;
    protected int i_timeout = ((Config4Impl)Db4o.configure()).i_timeoutClientSocket;
    protected String i_hostName;
    protected int i_port;

    protected YapSocket() {
        // don't use directly
        // only needed for subclasses
    }

    public YapSocket(Socket socket) throws IOException{
        i_socket = socket;
        i_out = i_socket.getOutputStream();
        i_in = i_socket.getInputStream();
       }

    public YapSocket(String hostName, int port) throws UnknownHostException, IOException {
        this(new Socket(hostName, port));
        i_hostName = hostName;
		i_port = port;
    }

    public void close() throws IOException {
        //    	i_socket.getInputStream().close();
        //    	i_socket.getOutputStream().close();
        i_socket.close();
    }

    public void flush() throws IOException {
        i_out.flush();
    }

    public String getHostName() {
        return i_hostName;
    }

    public int getPort() {
        return i_port;
    }

    public int read() throws IOException {
        return i_in.read();
    }

    public int read(byte[] a_bytes, int a_offset, int a_length) throws IOException {
        return i_in.read(a_bytes, a_offset, a_length);
    }

    public void setSoTimeout(int timeout) {
        i_timeout = timeout;
        try {
            i_socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

	public void write(byte[] bytes) throws IOException {
	    i_out.write(bytes);
	}

    public void write(int i) throws IOException {
        i_out.write(i);
    }

}
