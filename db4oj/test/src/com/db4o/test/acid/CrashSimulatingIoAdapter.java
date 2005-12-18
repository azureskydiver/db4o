/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.acid;

import java.io.*;

import com.db4o.io.*;


public class CrashSimulatingIoAdapter extends VanillaIoAdapter{
    
    CrashSimulatingBatch batch;
    
    long curPos;
    
    public CrashSimulatingIoAdapter(IoAdapter delegateAdapter) {
        super(delegateAdapter);
        batch = new CrashSimulatingBatch();
    }
    
    private CrashSimulatingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, CrashSimulatingBatch batch) throws IOException {
        super(delegateAdapter.open(path, lockFile, initialLength));
        this.batch = batch;
    }
    
    public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
        return new CrashSimulatingIoAdapter(_delegate, path, lockFile, initialLength, batch);
    }

    public void seek(long pos) throws IOException {
        curPos=pos;
        super.seek(pos);
    }
    
    public void write(byte[] buffer, int length) throws IOException {
        super.write(buffer, length);
        byte[] copy=new byte[buffer.length];
        System.arraycopy(buffer, 0, copy, 0, buffer.length);
        batch.add(copy, curPos, length);
    }
    
    public void sync() throws IOException {
        super.sync();
        batch.sync();
    }
}
