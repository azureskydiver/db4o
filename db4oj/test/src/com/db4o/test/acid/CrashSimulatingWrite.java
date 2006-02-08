/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.acid;

import java.io.*;


public class CrashSimulatingWrite {
    
    byte[] data;
    long offset;
    int length;
    
    public CrashSimulatingWrite(byte[] data, long offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public void write(RandomAccessFile raf) throws IOException {
        raf.seek(offset);
        raf.write(data, 0, length);
    }
    
    public String toString(){
        return "A " + offset + " L " + length;
    }



}
