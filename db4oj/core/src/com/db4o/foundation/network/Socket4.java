/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;

public interface Socket4 {

	public abstract void close() throws IOException;

	public abstract void flush() throws IOException;
    
    public abstract boolean isConnected();

	public abstract int read() throws IOException;

	public abstract int read(byte[] a_bytes, int a_offset, int a_length)
			throws IOException;

	public abstract void setSoTimeout(int timeout);

	public abstract void write(byte[] bytes) throws IOException;

	public abstract void write(byte[] bytes, int off, int len)
			throws IOException;

	public abstract void write(int i) throws IOException;

	public abstract Socket4 openParalellSocket() throws IOException;

}