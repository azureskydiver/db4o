/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

import java.io.*;

/**
 * Debug IoAdapter syncing to drive after every write call.
 */
public class SafeSyncIoAdapter extends VanillaIoAdapter {
    
    public SafeSyncIoAdapter(IoAdapter delegateAdapter) {
        super(delegateAdapter);
    }
    
    private SafeSyncIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength) throws IOException {
        super(delegateAdapter.open(path, lockFile, initialLength));
    }
    
    public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
        return new SafeSyncIoAdapter(_delegate, path, lockFile, initialLength);
    }
    
    public void write(byte[] buffer, int length) throws IOException {
        super.write(buffer, length);
        sync();
    }
    
}
