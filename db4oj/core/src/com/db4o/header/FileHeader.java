/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;


/**
 * @exclude
 */
public abstract class FileHeader {
    
    public abstract void close() throws IOException;
    
    public abstract int length();

    public abstract void initNew(YapFile file) throws IOException;

    public abstract void readFixedPart(YapFile file) throws IOException;

    public abstract void readVariablePart(YapFile file);

    public abstract Transaction interruptedTransaction();

    public abstract void writeFixedPart(
        boolean shuttingDown, YapWriter writer, byte b, int id, int freespaceID);
    
    public abstract void writeTransactionPointer(Transaction systemTransaction, int address);
    
    public abstract void writeVariablePart(YapFile file, int part);

}
