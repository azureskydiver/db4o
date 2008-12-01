/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

import com.db4o.ext.*;

/**
 * base class for IoAdapters that delegate to other IoAdapters (decorator pattern)
 * @deprecated use {@link StorageDecorator} / {@link BinDecorator} instead.
 */
public abstract class VanillaIoAdapter extends IoAdapter {
    
    protected IoAdapter _delegate;
    
    public VanillaIoAdapter(IoAdapter delegateAdapter){
        _delegate = delegateAdapter;
    }
    
    protected VanillaIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
    	this(delegateAdapter.open(path, lockFile, initialLength, readOnly));
    }

    public void close() throws Db4oIOException {
        _delegate.close();
    }

    public void delete(String path) {
    	_delegate.delete(path);
    }
    
    public boolean exists(String path) {
    	return _delegate.exists(path);
    }
    
    public long getLength() throws Db4oIOException {
        return _delegate.getLength();
    }

    public int read(byte[] bytes, int length) throws Db4oIOException {
        return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws Db4oIOException {
        _delegate.seek(pos);
    }

    public void sync() throws Db4oIOException {
        _delegate.sync();
    }

    public void write(byte[] buffer, int length) throws Db4oIOException {
        _delegate.write(buffer, length);
    }

}
