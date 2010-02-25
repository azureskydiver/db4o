/* Copyright (C) 2004 - 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
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
    
    private static final int HEADER_LOCK_OFFSET = SIGNATURE.length + 1;
    private static final int OPEN_TIME_OFFSET = HEADER_LOCK_OFFSET + Const4.INT_LENGTH;
    private static final int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + Const4.LONG_LENGTH;
    private static final int TRANSACTION_POINTER_OFFSET = ACCESS_TIME_OFFSET + Const4.LONG_LENGTH;
    private static final int BLOCKSIZE_OFFSET = TRANSACTION_POINTER_OFFSET + (Const4.INT_LENGTH * 2);
    
    public static final int HEADER_LENGTH = TRANSACTION_POINTER_OFFSET + (Const4.INT_LENGTH * 6);
    
    private TimerFileLock _timerFileLock;

    private FileHeaderVariablePart1 _variablePart;

    public void close() throws Db4oIOException {
    	if(_timerFileLock == null){
    		return;
    	}
        _timerFileLock.close();
    }

    public void initNew(LocalObjectContainer file) throws Db4oIOException {
        commonTasksForNewAndRead(file);
        _variablePart = createVariablePart(file, 0);
        writeVariablePart(file, 0);
    }

	protected FileHeaderVariablePart1 createVariablePart(LocalObjectContainer file, int id) {
		return new FileHeaderVariablePart1(file, id, file.systemData());
	}
    
    protected FileHeader newOnSignatureMatch(LocalObjectContainer file, ByteArrayBuffer reader) {
        if(signatureMatches(reader, SIGNATURE, version())){
            return createNew();
        }
        return null;
    }

	private void newTimerFileLock(LocalObjectContainer file) {
        _timerFileLock = TimerFileLock.forFile(file);
        _timerFileLock.setAddresses(0, OPEN_TIME_OFFSET, ACCESS_TIME_OFFSET);
    }

    @Override
    public void completeInterruptedTransaction(LocalObjectContainer container) {
    	SystemData systemData = container.systemData();
    	container.globalIdSystem().completeInterruptedTransaction(systemData.transactionPointer1(), systemData.transactionPointer2());
    }

    public int length() {
        return HEADER_LENGTH;
    }

    protected void read(LocalObjectContainer file, ByteArrayBuffer reader) {
        commonTasksForNewAndRead(file);
        checkThreadFileLock(file, reader);
        reader.seek(TRANSACTION_POINTER_OFFSET);
        file.systemData().transactionPointer1(reader.readInt());
        file.systemData().transactionPointer2(reader.readInt());
        reader.seek(BLOCKSIZE_OFFSET);
        file.blockSizeReadFromFile(reader.readInt());
        readClassCollectionAndFreeSpace(file, reader);
        _variablePart =  createVariablePart(file, reader.readInt());
        _variablePart.read();
    }
    
    private void checkThreadFileLock(LocalObjectContainer container, ByteArrayBuffer reader) {
    	reader.seek(ACCESS_TIME_OFFSET);
    	long lastAccessTime = reader.readLong();
		if(FileHeader.lockedByOtherSession(container, lastAccessTime)){
			_timerFileLock.checkIfOtherSessionAlive(container, 0, ACCESS_TIME_OFFSET, lastAccessTime);
		}
	}
    
	private void commonTasksForNewAndRead(LocalObjectContainer file){
        newTimerFileLock(file);
        file._handlers.oldEncryptionOff();
    }
    
    public void writeFixedPart(
        LocalObjectContainer file, boolean startFileLockingThread, boolean shuttingDown, StatefulBuffer writer, int blockSize, int freespaceID) {
    	SystemData systemData = file.systemData();
        writer.append(SIGNATURE);
        writer.writeByte(version());
        writer.writeInt((int)timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(System.currentTimeMillis(), shuttingDown));
        writer.writeInt(systemData.transactionPointer1());
        writer.writeInt(systemData.transactionPointer2());
        writer.writeInt(blockSize);
		writer.writeInt(systemData.classCollectionID());
        writer.writeInt(freespaceID);
        writer.writeInt(_variablePart.id());
        if (Debug4.xbytes) {
        	writer.checkXBytes(false);
        }
        writer.write();
        file.syncFiles();
        if(startFileLockingThread){
        	file.threadPool().start(_timerFileLock);
        }
    }

    public void writeTransactionPointer(Transaction systemTransaction, int transactionPointer1, int transactionPointer2) {
        writeTransactionPointer(systemTransaction, transactionPointer1, transactionPointer2, 0, TRANSACTION_POINTER_OFFSET);
    }

    public void writeVariablePart(LocalObjectContainer file, int part) {
    	Runnable commitHook = commit();
    	file.syncFiles();
    	commitHook.run();
    	file.syncFiles();
    }

	@Override
	public void readIdentity(LocalObjectContainer container) {
		_variablePart.readIdentity((LocalTransaction) container.systemTransaction());
	}

	@Override
	public Runnable commit() {
		return _variablePart.commit();
	}
	
	protected FileHeader1 createNew(){
		return new FileHeader1();
	}
	
    protected byte version() {
		return (byte) 1;
	}

	@Override
	public FileHeader convert(LocalObjectContainer file) {
		FileHeader2 fileHeader = new FileHeader2();
		fileHeader.initNew(file);
		return fileHeader;
	}

}
