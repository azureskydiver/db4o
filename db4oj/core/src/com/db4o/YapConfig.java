/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;

/**
 * configuration and agent to write the configuration block
 * The configuration block also contains the timer lock and
 * a pointer to the running transaction.
 */
final class YapConfig implements Runnable
{
	private final Object		i_timeWriterLock = new Object();
	private final YapFile		i_stream;
	private int					i_address;
	private Transaction			i_transactionToCommit;
	int                 		i_bootRecordID;
	int							i_blockSize = 1;
	
	private static final int	POINTER_ADDRESS = 2;
	private static final int	MINIMUM_LENGTH = 
		YapConst.YAPINT_LENGTH    			// own length
		+ (YapConst.YAPLONG_LENGTH * 2)	 	// candidate ID and last access time
		+ 1;  						// Unicode byte
		
	private static final int	TRANSACTION_OFFSET = MINIMUM_LENGTH;
	private static final int	BOOTRECORD_OFFSET = TRANSACTION_OFFSET + YapConst.YAPINT_LENGTH * 2;  
	private static final int	BLOCKLENGTH_OFFSET = BOOTRECORD_OFFSET + YapConst.YAPINT_LENGTH;  
	
	// complete possible data in config block
	private static final int	LENGTH = 
		MINIMUM_LENGTH 
		+ (YapConst.YAPINT_LENGTH * 4);		// (two transaction pointers, PDB ID, block size 
	
		
		
	private final long			i_opentime; // written as pure long 8 bytes
	byte						i_encoding;
	
	YapConfig(YapFile a_stream){
		i_stream = a_stream;
		i_opentime = processID();
		if(lockFile()){
			writeHeaderLock();
		}
	}
	
	private int candidateAddress(){
		return i_address + YapConst.YAPINT_LENGTH;
	}
	
	private YapWriter candidateLockIO(){
		return i_stream.getWriter(i_stream.getTransaction(), candidateAddress(), YapConst.YAPLONG_LENGTH);
	}
	
	private void candidateLockOverwritten(){
		if(lockFile()){
			YapWriter bytes = candidateLockIO();
			bytes.read();
			if(YLong.readLong(bytes) != i_opentime){
				Db4o.throwRuntimeException(22);
			}
			writeCandidateLock();
		}
	}

	
	int getAddress(){
		return i_address;
	}
	
	Transaction getTransactionToCommit(){
		return i_transactionToCommit;
	}
	
	void go(){
		i_stream.createStringIO(i_encoding);
		i_stream.setTransactionPointerAddress(transactionAddress());
		if(lockFile()){
			i_stream.setTimerAddress(timerAddress());
			try{
				writeAccessTime();
			}catch(Exception e){
			}
			// One last check before we start off.
			syncFiles();
			candidateLockOverwritten();
			new Thread(this).start();
		}
	}
	
	private YapWriter headerLockIO(){
		return i_stream.getWriter(i_stream.getTransaction(), 2 + YapConst.YAPINT_LENGTH, YapConst.YAPINT_LENGTH);
	}
	
	private void headerLockOverwritten() {
		if(lockFile()){
			YapWriter bytes = headerLockIO();
			bytes.read();
		
			// we need to cast here, since
			// the original file format only leaves us room
			// for an int.
			// TODO: Fix in file format rewrite
			if(YInt.readInt(bytes) != ((int)i_opentime) ){
				throw new DatabaseFileLockedException();
			}
			writeHeaderLock();
		}
	}
	
	private boolean lockFile(){
		if(! Debug.lockFile){
			return false;
		}
		return i_stream.hasLockFileThread();
	}
	
	static long processID(){
		long id = System.currentTimeMillis();
		/*
		if(i_stream.LOCK_FILE){
			final int RETRIES = 10;
			try{
				for(int i = 0; i < RETRIES; i ++){
					File lockFile = new File("" + id);
					// if(Platform.createNewFile(lockFile)){
					if(Platform.createNewFile(lockFile)){
						try{
							Thread.currentThread().sleep(100);
						}catch(InterruptedException ie){
						}
						lockFile.delete();
						break;
					}
					id = System.currentTimeMillis();
				}
			}catch(Exception ioe){
			}
		}
		*/
		return id;
	}


	/**
	 * returns true if Unicode check is necessary
	 */
	boolean read(YapWriter reader) {
		i_address = reader.readInt(); 
		if(i_address == 2){
			return true;
		}
		read();
		return false;
	}
	
	void read() {
		writeCandidateLock();
		YapWriter reader = i_stream.getWriter(i_stream.getSystemTransaction(), i_address, LENGTH);
		try{
			i_stream.readBytes(reader.i_bytes, i_address, LENGTH);
		}catch(Exception e){
			// TODO: Exception handling
		}
		int oldLength = reader.readInt();
		if(oldLength > LENGTH  || oldLength < MINIMUM_LENGTH){
			Db4o.throwRuntimeException(17);
		}
		long candidateID = YLong.readLong(reader);
		long lastAccessTime = YLong.readLong(reader);
		i_encoding = reader.readByte();
		
		if(oldLength > TRANSACTION_OFFSET){
			int transactionID1 = YInt.readInt(reader);
			int transactionID2 = YInt.readInt(reader);
			if( (transactionID1 > 0)  &&  (transactionID1 == transactionID2)){
				i_transactionToCommit = new Transaction(i_stream, null);
				i_transactionToCommit.setAddress(transactionID1);
			}
		}
		
		if(oldLength > BOOTRECORD_OFFSET) {
		    i_bootRecordID = YInt.readInt(reader);
		}
		
		if(oldLength > BLOCKLENGTH_OFFSET) {
		    i_blockSize = YInt.readInt(reader);
		}
		
		if(lockFile() && ( lastAccessTime != 0)){
			i_stream.logMsg(28, null);
			long waitTime = YapConst.LOCK_TIME_INTERVAL * 10;
			long currentTime = System.currentTimeMillis();

			// If someone changes the system clock here,
			// he is out of luck.
			while(System.currentTimeMillis() < currentTime + waitTime){
				try{
					Thread.sleep(waitTime);
					}catch(Exception ie){
				}
			}
			reader = i_stream.getWriter(i_stream.getSystemTransaction(), timerAddress(), YapConst.LONG_BYTES);
			reader.read();
			long currentAccessTime = YLong.readLong(reader.i_bytes);
			if((currentAccessTime > lastAccessTime) ){
				throw new DatabaseFileLockedException();
			}
		}
		if(lockFile()){
			try{
				Thread.sleep(100);
				// We give the other process a chance to 
				// write its lock.
			}catch(Exception ie){
			}
			syncFiles();
			candidateLockOverwritten();
		}
		if(oldLength < LENGTH){
			write();
		}
		go();
	}
	
	public void run(){
		if(! Deploy.csharp){
			Thread t = Thread.currentThread();
			t.setName("db4o file lock");
			try{
				while(writeAccessTime()){
					Thread.sleep(YapConst.LOCK_TIME_INTERVAL);
				}
			}catch(Exception e){
			}
		}
	}
	
	void setEncoding(byte encoding){
		i_encoding = encoding;
	}
	
	void syncFiles(){
		i_stream.syncFiles();
	}
	
	private int timerAddress(){
		return i_address + YapConst.YAPINT_LENGTH + YapConst.YAPLONG_LENGTH;
	}
	
	private int transactionAddress(){
		return i_address + TRANSACTION_OFFSET;
	}
	
	void write() {
		headerLockOverwritten();
		i_address = i_stream.getSlot(LENGTH);
		YapWriter writer = i_stream.getWriter(i_stream.i_trans, i_address,LENGTH);
		YInt.writeInt(LENGTH, writer);
		YLong.writeLong(i_opentime, writer);
		YLong.writeLong(i_opentime, writer);
		writer.append(i_encoding);
		YInt.writeInt(0, writer);
		YInt.writeInt(0, writer);
		YInt.writeInt(i_bootRecordID, writer);
		YInt.writeInt(i_blockSize, writer);
		writer.write();
		writePointer();
	}
	
	boolean writeAccessTime() throws IOException{
		return i_stream.writeAccessTime();
	}
	
	private void writeCandidateLock(){
		if(lockFile()){
			YapWriter bytes = candidateLockIO();
			YLong.writeLong(i_opentime, bytes);
			bytes.write();
		}
	}
	
	private void writeHeaderLock(){
		if(lockFile()){
			YapWriter bytes = headerLockIO();
			YInt.writeInt(((int)i_opentime), bytes);
			bytes.write();
		}
	}
	
	private void writePointer() {
		headerLockOverwritten();
		YapWriter writer = i_stream.getWriter(i_stream.i_trans, 2, YapConst.YAPID_LENGTH);
		YInt.writeInt(i_address, writer);
		if(Deploy.debug && Deploy.overwrite){
			writer.setID(YapConst.IGNORE_ID);
		}
		writer.write();
		writeHeaderLock();
	}
	
}

