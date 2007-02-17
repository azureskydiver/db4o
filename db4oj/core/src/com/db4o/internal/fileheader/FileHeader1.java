/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import java.io.*;

import com.db4o.internal.*;


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
    private static final int OPEN_TIME_OFFSET = HEADER_LOCK_OFFSET + Const4.INT_LENGTH;
    private static final int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + Const4.LONG_LENGTH;
    private static final int TRANSACTION_POINTER_OFFSET = ACCESS_TIME_OFFSET + Const4.LONG_LENGTH; 
    
    static final int LENGTH = TRANSACTION_POINTER_OFFSET + (Const4.INT_LENGTH * 6);
    
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
        _timerFileLock.setAddresses(0, OPEN_TIME_OFFSET, ACCESS_TIME_OFFSET);
    }

    public Transaction interruptedTransaction() {
        return _interruptedTransaction;
    }

    public int length() {
        return LENGTH;
    }

    protected void readFixedPart(LocalObjectContainer file, Buffer reader) throws IOException {
        commonTasksForNewAndRead(file);
        checkThreadFileLock(file, reader);
        reader.seek(TRANSACTION_POINTER_OFFSET);
        _interruptedTransaction = Transaction.readInterruptedTransaction(file, reader);
        file.blockSizeReadFromFile(reader.readInt());
        readClassCollectionAndFreeSpace(file, reader);
        _variablePart = new FileHeaderVariablePart1(reader.readInt(), file.systemData());
    }
    
    private void checkThreadFileLock(LocalObjectContainer container, Buffer reader) {
    	reader.seek(OPEN_TIME_OFFSET);
    	long lastOpenTime = reader.readLong();
    	long lastAccessTime = reader.readLong();
		if(FileHeader.lockedByOtherSession(container, lastAccessTime)){
			FileHeader.checkIfOtherSessionAlive(container, 0, OPEN_TIME_OFFSET, lastAccessTime);
		}
	}

	private void commonTasksForNewAndRead(LocalObjectContainer file){
        newTimerFileLock(file);
        file.i_handlers.oldEncryptionOff();
    }
    
    public void readVariablePart(LocalObjectContainer file) {
        _variablePart.read(file.getSystemTransaction());
    }
    
    public void writeFixedPart(
        LocalObjectContainer file, boolean startFileLockingThread, boolean shuttingDown, StatefulBuffer writer, int blockSize, int freespaceID) {
        writer.append(SIGNATURE);
        writer.append(VERSION);
        writer.writeInt((int)timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(System.currentTimeMillis(), shuttingDown));
        writer.writeInt(0);  // transaction pointer 1 for "in-commit-mode"
        writer.writeInt(0);  // transaction pointer 2
        writer.writeInt(blockSize);
        writer.writeInt(file.systemData().classCollectionID());
        writer.writeInt(freespaceID);
        writer.writeInt(_variablePart.getID());
        writer.noXByteCheck();
        writer.write();
        if(startFileLockingThread){
        	try {
				_timerFileLock.start();
			} catch (IOException e) {
				// TODO: throw
			}
        }
    }

    public void writeTransactionPointer(Transaction systemTransaction, int transactionAddress) {
        writeTransactionPointer(systemTransaction, transactionAddress, 0, TRANSACTION_POINTER_OFFSET);
    }

    public void writeVariablePart(LocalObjectContainer file, int part) {
    	_variablePart.setStateDirty();
        _variablePart.write(file.getSystemTransaction());
    }

}
