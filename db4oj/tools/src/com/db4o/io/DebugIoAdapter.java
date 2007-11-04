/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

import com.db4o.ext.*;

/**
 * @exclude
 */  
public class DebugIoAdapter extends VanillaIoAdapter{
    
    static int counter;
    
    private static final int[] RANGE_OF_INTEREST = new int[] {0, 20};
    

    public DebugIoAdapter(IoAdapter delegateAdapter){
        super(delegateAdapter);
    }
    
    protected DebugIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
        super(delegateAdapter.open(path, lockFile, initialLength, readOnly));
    }

    public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
        return new DebugIoAdapter(new RandomAccessFileAdapter(),  path, lockFile, initialLength, readOnly);
    }
    
    public void seek(long pos) throws Db4oIOException {
        if(pos >= RANGE_OF_INTEREST[0] && pos <= RANGE_OF_INTEREST[1]){
            counter ++;
            System.out.println("seek: " + pos + "  counter: " + counter);
        }
        super.seek(pos);
    }

}
