/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

import java.io.*;

/**
 * @exclude
 */  
public class DebugIoAdapter extends VanillaIoAdapter{
    
    static int counter;
    
    private static final int[] RANGE_OF_INTEREST = new int[] {0, 20};
    

    public DebugIoAdapter(IoAdapter delegateAdapter){
        super(delegateAdapter);
    }
    
    protected DebugIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength) throws IOException {
        super(delegateAdapter.open(path, lockFile, initialLength));
    }

    public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
        return new DebugIoAdapter(new RandomAccessFileAdapter(),  path, lockFile, initialLength);
    }
    
    public void seek(long pos) throws IOException {
        if(pos >= RANGE_OF_INTEREST[0] && pos <= RANGE_OF_INTEREST[1]){
            counter ++;
            System.out.println("seek: " + pos + "  counter: " + counter);
        }
        super.seek(pos);
    }

}
