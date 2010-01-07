/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.transactionlog;

import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;
import com.db4o.io.*;

/**
 * @exclude
 */
public class FileBasedTransactionLogHandler extends TransactionLogHandler {
	
	static final int LOCK_INT = Integer.MAX_VALUE - 1;
	
	private Bin _lockFile;
	
	private Bin _logFile;
	
	private final String _fileName;
	
	
	public FileBasedTransactionLogHandler(StandardIdSystem idSystem, String fileName) {
		super(idSystem);
		_fileName = fileName;
	}

	public static String logFileName(String fileName) {
		return fileName + ".log";
	}

	public static String lockFileName(String fileName) {
		return fileName + ".lock";
	}

	private Bin openBin(String fileName) {
		return new FileStorage().open(new BinConfiguration(fileName, _idSystem.config().lockFile(), 0, false));
	}
	
	public InterruptedTransactionHandler interruptedTransactionHandler(ByteArrayBuffer reader) {
		reader.incrementOffset(Const4.INT_LENGTH * 2);
		if(!File4.exists(lockFileName(_fileName))){
			return null;
		}
		if( ! lockFileSignalsInterruptedTransaction()){
			return null;
		}
		return new InterruptedTransactionHandler() {
			
			public void completeInterruptedTransaction() {
				ByteArrayBuffer buffer = new ByteArrayBuffer(Const4.INT_LENGTH);
				openLogFile();
				read(_logFile, buffer);
				int length = buffer.readInt();
				if(length > 0){
					buffer = new ByteArrayBuffer(length);
					read(_logFile, buffer);
					buffer.incrementOffset(Const4.INT_LENGTH);
					_idSystem.readWriteSlotChanges(buffer);
		            deleteLockFile();
		            _idSystem.freeAndClearSystemSlotChanges();
				}else{
					deleteLockFile();
				}
				closeLogFile();
				deleteLogFile();
			}
		};
	}

	private boolean lockFileSignalsInterruptedTransaction() {
		openLockFile();
		ByteArrayBuffer buffer = newLockFileBuffer();
		read(_lockFile, buffer);
		for (int i = 0; i < 2; i++) {
			int checkInt = buffer.readInt();
			if(checkInt != LOCK_INT){
				closeLockFile();
				return false;
			}
		}
		closeLockFile();
		return true;
	}

	public void close() {
		if(!logsOpened()){
			return;
		}
		closeLockFile();
		closeLogFile();
		deleteLockFile();
		deleteLogFile();
	}

	private void closeLockFile() {
		syncAndClose(_lockFile);
		_lockFile = null;
	}

	private void syncAndClose(Bin bin) {
		try {
			bin.sync();
		}
		finally {
			bin.close();
		}
	}

	private void closeLogFile() {
		syncAndClose(_logFile);
		_logFile = null;
	}

	private void deleteLockFile() {
		File4.delete(lockFileName(_fileName));
	}

	private void deleteLogFile() {
		File4.delete(logFileName(_fileName));
	}

	@Override
	public Slot allocateSlot(LocalTransaction transaction, boolean append) {
		// do nothing
		return null;
	}

	@Override
	public void applySlotChanges(LocalTransaction transaction, Slot reservedSlot) {
		int slotChangeCount = countSlotChanges(transaction);
		if(slotChangeCount < 1){
			return;
		}
		
		flushDatabaseFile();
		
		ensureLogAndLock();
		int length = transactionLogSlotLength(transaction);
		ByteArrayBuffer logBuffer = new ByteArrayBuffer(length);
		logBuffer.writeInt(length);
		logBuffer.writeInt(slotChangeCount);
	
	    appendSlotChanges(transaction, logBuffer);
	    write(_logFile, logBuffer);
	    _logFile.sync();
	    
	    writeToLockFile(LOCK_INT);

	    if (_idSystem.writeSlots(transaction)) {
	    	flushDatabaseFile();
	    }
	    writeToLockFile(0);
	}
	
	private void writeToLockFile(int lockSignal) {
	    ByteArrayBuffer lockBuffer = newLockFileBuffer();
		lockBuffer.writeInt(lockSignal);
	    lockBuffer.writeInt(lockSignal);
	    write(_lockFile, lockBuffer);
	    _lockFile.sync();
	}

	private ByteArrayBuffer newLockFileBuffer() {
		return new ByteArrayBuffer(lockFileBufferLength());
	}

	private int lockFileBufferLength() {
		return Const4.LONG_LENGTH * 2;
	}

	private void ensureLogAndLock() {
		if(_idSystem.isReadOnly()){
			return;
		}
		if(logsOpened()){
			return;
		}
		openLockFile();
		openLogFile();
	}

	private void openLogFile() {
		_logFile = openBin(logFileName(_fileName));
	}

	private void openLockFile() {
		_lockFile = openBin(lockFileName(_fileName));
	}

	private boolean logsOpened() {
		return _lockFile != null;
	}

	private void read(Bin storage, ByteArrayBuffer buffer) {
		storage.read(0, buffer._buffer, buffer.length());
	}
	
	private void write(Bin storage, ByteArrayBuffer buffer) {
		storage.write(0, buffer._buffer, buffer.length());
	}
	
}
