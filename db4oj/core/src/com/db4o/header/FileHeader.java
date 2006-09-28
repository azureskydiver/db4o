/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public abstract class FileHeader {
    
    private static final FileHeader[] AVAILABLE_FILE_HEADERS = new FileHeader[]{
        new FileHeader0(),
        new FileHeader1()
    };
    
    private static int readerLength(){
        int length = AVAILABLE_FILE_HEADERS[0].length();
        for (int i = 1; i < AVAILABLE_FILE_HEADERS.length; i++) {
            length = YInt.max(length, AVAILABLE_FILE_HEADERS[i].length());
        }
        return length;
    }

    public static FileHeader readFixedPart(YapFile file) throws IOException{
        YapReader reader = new YapReader(readerLength()); 
        reader.read(file, 0, 0);
        FileHeader result = null;
        for (int i = 0; i < AVAILABLE_FILE_HEADERS.length; i++) {
            reader._offset = 0;
            result =  AVAILABLE_FILE_HEADERS[i].newOnSignatureMatch(file, reader);
            if(result != null){
                break;
            }
        }
        if(result == null){
            Exceptions4.throwRuntimeException(17);
        }else{
            result.readFixedPart(file, reader);
        }
        return result;
    }

    public abstract void close() throws IOException;

    public abstract void initNew(YapFile file) throws IOException;

    public abstract Transaction interruptedTransaction();

    public abstract int length();
    
    protected abstract FileHeader newOnSignatureMatch(YapFile file, YapReader reader);
    
    protected int openTimeToWrite(long openTime, boolean shuttingDown) {
        return shuttingDown ? 0 : (int)openTime;
    }

    protected abstract void readFixedPart(YapFile file, YapReader reader) throws IOException;

    public abstract void readVariablePart(YapFile file);
    
    protected boolean signatureMatches(YapReader reader, byte[] signature, byte version){
        for (int i = 0; i < signature.length; i++) {
            if(reader.readByte() != signature[i]){
                return false;
            }
        }
        return reader.readByte() == version; 
    }
    
    public abstract void writeFixedPart(
        boolean shuttingDown, YapWriter writer, int blockSize, int classCollectionID, int freespaceID);
    
    public abstract void writeTransactionPointer(Transaction systemTransaction, int transactionAddress);

    protected void writeTransactionPointer(Transaction systemTransaction, int transactionAddress, final int address, final int offset) {
        YapWriter bytes = new YapWriter(systemTransaction, address, YapConst.INT_LENGTH * 2);
        bytes.moveForward(offset);
        bytes.writeInt(transactionAddress);
        bytes.writeInt(transactionAddress);
        if (Debug.xbytes && Deploy.overwrite) {
            bytes.setID(YapConst.IGNORE_ID);
        }
        bytes.write();
    }
    
    public abstract void writeVariablePart(YapFile file, int part);

}
