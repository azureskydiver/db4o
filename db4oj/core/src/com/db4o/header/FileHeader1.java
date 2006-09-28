/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;


/**
 * @exclude
 */
public class FileHeader1 extends FileHeader {
    
    private static final byte[] SIGNATURE = {(byte)'d', (byte)'b', (byte)'4', (byte)'o'};
    
    private static byte VERSION = 1;
    
    private static final int HEADER_LOCK_OFFSET = SIGNATURE.length + 1;
    private static final int OPEN_TIME_OFFSET = HEADER_LOCK_OFFSET + YapConst.INT_LENGTH;
    private static final int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + YapConst.LONG_LENGTH;
    private static final int TRANSACTION_POINTER_OFFSET = ACCESS_TIME_OFFSET + YapConst.LONG_LENGTH; 
    
    static final int LENGTH = TRANSACTION_POINTER_OFFSET + (YapConst.INT_LENGTH * 6);
    
    // The header format is:

    // New format
    // -------------------------
    // (byte) 'd'
    // (byte) 'b'
    // (byte) '4'
    // (byte) 'o'
    // (byte) headerVersion
    // (int) headerLock
    // (long) openTime
    // (long) accessTime
    // (int) Transaction pointer 1
    // (int) Transaction pointer 2
    // (int) blockSize
    // (int) classCollectionID
    // (int) freespaceID
    // (int) variablePartID
    
    private TimerFileLock _timerFileLock;
    
    
    private TimerFileLock timerFileLock(){
        return _timerFileLock;
    }

    private int variablePartID() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

    public void initNew(YapFile file) throws IOException {
        newTimerFileLock(file);
        
    }
    
    protected FileHeader newOnSignatureMatch(YapFile file, YapReader reader) {
        if(signatureMatches(reader, SIGNATURE, VERSION)){
            return new FileHeader1();
        }
        return null;
    }

    private void newTimerFileLock(YapFile file) {
        _timerFileLock = TimerFileLock.forFile(file);
    }

    public Transaction interruptedTransaction() {
        
        // TODO Auto-generated method stub
        return null;
    }

    public int length() {
        return LENGTH;
    }

    protected void readFixedPart(YapFile file, YapReader reader) throws IOException {
        newTimerFileLock(file);
        
    }

    public void readVariablePart(YapFile file) {
        // TODO Auto-generated method stub
        
    }
    
    public void writeFixedPart(
        boolean shuttingDown, YapWriter writer, int blockSize, int classCollectionId,int freespaceID) {
        writer.append(SIGNATURE);
        writer.append(VERSION);
        writer.writeInt((int)openTimeToWrite(timerFileLock().openTime(), shuttingDown));
        
        writer.writeLong(openTimeToWrite(timerFileLock().openTime(), shuttingDown));
        
        writer.writeInt(0);
        writer.writeInt(0);
        writer.writeInt(blockSize);
        writer.writeInt(classCollectionId);
        writer.writeInt(freespaceID);
        writer.writeInt(variablePartID());
    }

    public void writeTransactionPointer(Transaction systemTransaction, int transactionAddress) {
        writeTransactionPointer(systemTransaction, transactionAddress, 0, TRANSACTION_POINTER_OFFSET);
    }

    public void writeVariablePart(YapFile file, int part) {
        
    }



}
