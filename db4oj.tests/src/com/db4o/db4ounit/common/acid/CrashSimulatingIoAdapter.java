/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.acid;

import com.db4o.ext.*;
import com.db4o.io.*;


public class CrashSimulatingIoAdapter extends VanillaIoAdapter{
    
    CrashSimulatingBatch batch;
    
    long curPos;
    
    public CrashSimulatingIoAdapter(IoAdapter delegateAdapter) {
        super(delegateAdapter);
        batch = new CrashSimulatingBatch();
    }
    
    private CrashSimulatingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, boolean readOnly, CrashSimulatingBatch batch) throws Db4oIOException {
        super(delegateAdapter.open(path, lockFile, initialLength, readOnly));
        this.batch = batch;
    }
    
    public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
        return new CrashSimulatingIoAdapter(_delegate, path, lockFile, initialLength, readOnly, batch);
    }

    public void seek(long pos) throws Db4oIOException {
        curPos=pos;
        super.seek(pos);
    }
    
    public void write(byte[] buffer, int length) throws Db4oIOException {
        super.write(buffer, length);
        byte[] copy=new byte[buffer.length];
        System.arraycopy(buffer, 0, copy, 0, buffer.length);
        batch.add(copy, curPos, length);
    }
    
    public void sync() throws Db4oIOException {
        super.sync();
        batch.sync();
    }
}
