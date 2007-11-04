/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation.network;

import com.db4o.ext.*;

public interface Socket4 {

	public abstract void close() throws Db4oIOException;

	public abstract void flush() throws Db4oIOException;
    
    public abstract boolean isConnected();

	public abstract int read() throws Db4oIOException;

	public abstract int read(byte[] a_bytes, int a_offset, int a_length) throws Db4oIOException;

	public abstract void setSoTimeout(int timeout);

	public abstract void write(byte[] bytes) throws Db4oIOException;

	public abstract void write(byte[] bytes, int off, int len) throws Db4oIOException;

	public abstract void write(int i) throws Db4oIOException;

	public abstract Socket4 openParalellSocket() throws Db4oIOException;

}