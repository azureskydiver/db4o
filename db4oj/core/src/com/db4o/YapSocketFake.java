/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

/**
 * Fakes a socket connection for an embedded client.
 */
class YapSocketFake extends YapSocket {
	
	final YapServer i_server;

    private YapSocketFake i_affiliate;
    private ByteBuffer4 i_uploadBuffer;
    private ByteBuffer4 i_downloadBuffer;

    public YapSocketFake(YapServer a_server) {
    	i_server = a_server;
        i_uploadBuffer = new ByteBuffer4(((Config4Impl)a_server.configure()).i_timeoutClientSocket);
        i_downloadBuffer = new ByteBuffer4(((Config4Impl)a_server.configure()).i_timeoutClientSocket);
    }

    public YapSocketFake(YapServer a_server, YapSocketFake affiliate) {
        this(a_server);
        i_affiliate = affiliate;
        affiliate.i_affiliate = this;
        i_downloadBuffer = affiliate.i_uploadBuffer;
        i_uploadBuffer = affiliate.i_downloadBuffer;
    }

    public void close() throws IOException {
        if (i_affiliate != null) {
            YapSocketFake temp = i_affiliate;
            i_affiliate = null;
            temp.close();
        }
        i_affiliate = null;

    }

    public void flush() {
        // do nothing
    }

    public String getHostName() {
        return null;
    }

    public boolean isClosed() {
        return i_affiliate == null;
    }

    public int read() throws IOException {
        return i_downloadBuffer.read();
    }

    public int read(byte[] a_bytes, int a_offset, int a_length) throws IOException {
        return i_downloadBuffer.read(a_bytes, a_offset, a_length);
    }

    public void setSoTimeout(int a_timeout) {
        i_uploadBuffer.setTimeout(a_timeout);
        i_downloadBuffer.setTimeout(a_timeout);
    }

    public void write(byte[] bytes) throws IOException {
        i_uploadBuffer.write(bytes);
    }

    public void write(int i) throws IOException {
        i_uploadBuffer.write(i);
    }
}
