/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public class FileHeader1 extends FileHeader {
    
    // The header format is:

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
    
    private static final byte[] SIGNATURE = {(byte)'d', (byte)'b', (byte)'4', (byte)'o'};
    
    private static byte VERSION = 1;
    
    private static final int HEADER_LOCK_OFFSET = SIGNATURE.length + 1;
    private static final int OPEN_TIME_OFFSET = HEADER_LOCK_OFFSET + YapConst.INT_LENGTH;
    private static final int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + YapConst.LONG_LENGTH;
    private static final int TRANSACTION_POINTER_OFFSET = ACCESS_TIME_OFFSET + YapConst.LONG_LENGTH; 
    
    static final int LENGTH = TRANSACTION_POINTER_OFFSET + (YapConst.INT_LENGTH * 6);
    
    private TimerFileLock _timerFileLock;

    private Transaction _interruptedTransaction;

    private FileHeaderVariablePart1 _variablePart;
    
    public void close() throws IOException {
        _timerFileLock.close();
    }

    public void initNew(LocalObjectContainer file) throws IOException {
        commonTasksForNewAndRead(file);
        _variablePart = new FileHeaderVariablePart1(0, file.systemData());
        writeVariablePart(file, 0);
    }
    
    protected FileHeader newOnSignatureMatch(LocalObjectContainer file, Buffer reader) {
        if(signatureMatches(reader, SIGNATURE, VERSION)){
            return new FileHeader1();
        }
        return null;
    }

    private void newTimerFileLock(LocalObjectContainer file) {
        _timerFileLock = TimerFileLock.forFile(file);
    }

    public Transaction interruptedTransaction() {
        return _interruptedTransaction;
    }

    public int length() {
        return LENGTH;
    }

    protected void readFixedPart(LocalObjectContainer file, Buffer reader) throws IOException {
        commonTasksForNewAndRead(file);
        reader.seek(TRANSACTION_POINTER_OFFSET);
        _interruptedTransaction = Transaction.readInterruptedTransaction(file, reader);
        file.blockSizeReadFromFile(reader.readInt());
        readClassCollectionAndFreeSpace(file, reader);
        _variablePart = new FileHeaderVariablePart1(reader.readInt(), file.systemData());
    }
    
    private void commonTasksForNewAndRead(LocalObjectContainer file){
        newTimerFileLock(file);
        file.i_handlers.oldEncryptionOff();
    }
    
    public void readVariablePart(LocalObjectContainer file) {
        _variablePart.read(file.getSystemTransaction());
    }
    
    public void writeFixedPart(
        LocalObjectContainer file, boolean shuttingDown, StatefulBuffer writer, int blockSize, int freespaceID) {
        writer.append(SIGNATURE);
        writer.append(VERSION);
        writer.writeInt((int)timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(System.currentTimeMillis(), shuttingDown));
        writer.writeInt(0);
        writer.writeInt(0);
        writer.writeInt(blockSize);
        writer.writeInt(file.systemData().classCollectionID());
        writer.writeInt(freespaceID);
        writer.writeInt(_variablePart.getID());
        writer.noXByteCheck();
        writer.write();
    }

    public void writeTransactionPointer(Transaction systemTransaction, int transactionAddress) {
        writeTransactionPointer(systemTransaction, transactionAddress, 0, TRANSACTION_POINTER_OFFSET);
    }

    public void writeVariablePart(LocalObjectContainer file, int part) {
    	_variablePart.setStateDirty();
        _variablePart.write(file.getSystemTransaction());
    }

}
