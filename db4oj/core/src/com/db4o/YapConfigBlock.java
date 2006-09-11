/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.freespace.*;

/**
 * configuration and agent to write the configuration block
 * The configuration block also contains the timer lock and
 * a pointer to the running transaction.
 * @exclude
 */
public final class YapConfigBlock implements Runnable
{
    
    // ConfigBlock Format
    
    // int    length of the config block
    // long   last access time for timer lock
    // long   last access time for timer lock (duplicate for atomicity)
    // byte   unicode or not
    // int    transaction-in-process address
    // int    transaction-in-process address (duplicate for atomicity)
    // int    id of PBootRecord
    // int    unused (and lost)
	// 5 bytes of the encryption password
    // byte   freespace system used
    // int    freespace address
    // int    converter version
    
	private final YapFile		_stream;
	public int							_address;
	private Transaction			_transactionToCommit;
	public int                 		_bootRecordID;
	
	private static final int	MINIMUM_LENGTH = 
		YapConst.INT_LENGTH    			// own length
		+ (YapConst.LONG_LENGTH * 2)	 	// candidate ID and last access time
		+ 1;  						// Unicode byte
	
	static final int			OPEN_TIME_OFFSET		= YapConst.INT_LENGTH;
	public static final int            ACCESS_TIME_OFFSET      = OPEN_TIME_OFFSET + YapConst.LONG_LENGTH;
		
	public static final int			TRANSACTION_OFFSET = MINIMUM_LENGTH;
	private static final int	BOOTRECORD_OFFSET = TRANSACTION_OFFSET + YapConst.INT_LENGTH * 2;  
	private static final int	INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET = BOOTRECORD_OFFSET + YapConst.INT_LENGTH;
	private static final int	ENCRYPTION_PASSWORD_LENGTH = 5;
    private static final int    PASSWORD_OFFSET = INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET+ENCRYPTION_PASSWORD_LENGTH;
    private static final int    FREESPACE_SYSTEM_OFFSET = PASSWORD_OFFSET + 1; 
    private static final int    FREESPACE_ADDRESS_OFFSET = FREESPACE_SYSTEM_OFFSET + YapConst.INT_LENGTH; 
    private static final int    CONVERTER_VERSION_OFFSET = FREESPACE_ADDRESS_OFFSET + YapConst.INT_LENGTH;
    private static final int    UUID_INDEX_ID_OFFSET = CONVERTER_VERSION_OFFSET + YapConst.INT_LENGTH;
	
	
	// complete possible data in config block
	private static final int	LENGTH = 
		MINIMUM_LENGTH 
		+ (YapConst.INT_LENGTH * 7)	// (two transaction pointers, PDB ID, lost int, freespace address, converter_version, index id)
	    + ENCRYPTION_PASSWORD_LENGTH
        + 1;
		
	public final long			_opentime; // written as pure long 8 bytes
	byte						_encoding;
    public byte                        _freespaceSystem;
    public int                         _freespaceAddress;
    private int                 _converterVersion;
    
    
	public int _uuidIndexId;
	
	public YapConfigBlock(YapFile stream){
		_stream = stream;
        _encoding = stream.configImpl().encoding();
        _freespaceSystem = FreespaceManager.checkType(stream.configImpl().freespaceSystem());
		_opentime = processID();
		if(lockFile()){
			writeHeaderLock();
		}
	}
	
	public Transaction getTransactionToCommit(){
		return _transactionToCommit;
	}
    
    private void ensureFreespaceSlot(){
        if(_freespaceAddress == 0){
            newFreespaceSlot(_freespaceSystem);
        }
    }
    
    public int newFreespaceSlot(byte freespaceSystem){
        _freespaceAddress = FreespaceManager.initSlot(_stream);
        _freespaceSystem = freespaceSystem;
        return _freespaceAddress;
    }
	
	public void go(){
		_stream.createStringIO(_encoding);
		if(lockFile()){
			try{
				writeAccessTime();
			}catch(Exception e){
			}
			// One last check before we start off.
			syncFiles();
			openTimeOverWritten();
			new Thread(this).start(); //Carl, shouldn't this be a daemon?
		}
	}
	
	private YapWriter headerLockIO(){
	    YapWriter writer = _stream.getWriter(_stream.getTransaction(), 0, YapConst.INT_LENGTH);
	    writer.moveForward(2 + YapConst.INT_LENGTH);
	    if (Debug.xbytes) {
	        writer.setID(YapConst.IGNORE_ID);
	    }
		return writer;
	}
	
	private void headerLockOverwritten() {
		if(lockFile()){
			YapWriter bytes = headerLockIO();
			bytes.read();
		
			// we need to cast here, since
			// the original file format only leaves us room
			// for an int.
			// TODO: Fix in file format rewrite
            
            int newOpenTime = YInt.readInt(bytes);
            
            // System.out.println("Read by " + System.identityHashCode(this) + " " + newOpenTime);
            
			if(newOpenTime != ((int)_opentime) ){
				throw new DatabaseFileLockedException();
			}
			writeHeaderLock();
		}
	}
	
	private boolean lockFile(){
		if(! Debug.lockFile){
			return false;
		}
		return _stream.needsLockFileThread();
	}
    
    private YapWriter openTimeIO(){
        YapWriter writer = _stream.getWriter(_stream.getTransaction(), _address, YapConst.LONG_LENGTH);
        writer.moveForward(OPEN_TIME_OFFSET);
        if (Debug.xbytes) {
            writer.setID(YapConst.IGNORE_ID);
        }
        return writer;
    }
    
    private void openTimeOverWritten(){
        if(lockFile()){
            YapWriter bytes = openTimeIO();
            bytes.read();
            if(YLong.readLong(bytes) != _opentime){
                Exceptions4.throwRuntimeException(22);
            }
            writeOpenTime();
        }
    }
    
    private byte[] passwordToken() {
        byte[] pwdtoken=new byte[ENCRYPTION_PASSWORD_LENGTH];
        String fullpwd=_stream.configImpl().password();
        if(_stream.configImpl().encrypt() && fullpwd!=null) {
            try {
                byte[] pwdbytes=new YapStringIO().write(fullpwd);
                YapWriter encwriter=new YapWriter(_stream.i_trans,pwdbytes.length+ENCRYPTION_PASSWORD_LENGTH);
                encwriter.append(pwdbytes);
                encwriter.append(new byte[ENCRYPTION_PASSWORD_LENGTH]);
                _stream.i_handlers.decrypt(encwriter);
                System.arraycopy(encwriter._buffer, 0, pwdtoken, 0, ENCRYPTION_PASSWORD_LENGTH);                
            }
            catch(Exception exc) {
                // should never happen
                //if(Debug.atHome) {
                    exc.printStackTrace();
                //}
            }
        }
        return pwdtoken;
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

	public void read(int address) {
        _address = address;
		writeOpenTime();
		YapWriter reader = _stream.getWriter(_stream.getSystemTransaction(), _address, LENGTH);
		try{
			_stream.readBytes(reader._buffer, _address, LENGTH);
		}catch(Exception e){
			// TODO: Exception handling
		}
		int oldLength = reader.readInt();
		if(oldLength > LENGTH  || oldLength < MINIMUM_LENGTH){
			Exceptions4.throwRuntimeException(17);
		}
        if(oldLength != LENGTH){
        	// TODO: instead of bailing out, somehow trigger wrapping the stream's io adapter in
        	// a readonly decorator, issue a  notification and continue?
            if(! _stream.configImpl().isReadOnly()  && ! _stream.configImpl().allowVersionUpdates()){
            	if(_stream.configImpl().automaticShutDown()) {
            		Platform4.removeShutDownHook(_stream, _stream.i_lock);
            	}
                Exceptions4.throwRuntimeException(65);
            }
        }
		YLong.readLong(reader);  // open time
		long lastAccessTime = YLong.readLong(reader);
		_encoding = reader.readByte();
		
		if(oldLength > TRANSACTION_OFFSET){
			int transactionID1 = YInt.readInt(reader);
			int transactionID2 = YInt.readInt(reader);
			if( (transactionID1 > 0)  &&  (transactionID1 == transactionID2)){
				_transactionToCommit = _stream.newTransaction(null);
				_transactionToCommit.setAddress(transactionID1);
			}
		}
		
		if(oldLength > BOOTRECORD_OFFSET) {
		    _bootRecordID = YInt.readInt(reader);
		}
		
		if(oldLength > INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET) {
		    // this one is dead.
		    // Blocksize is in the very first bytes
		    YInt.readInt(reader);
		}
		
		if(oldLength > PASSWORD_OFFSET) {
			byte[] encpassword=reader.readBytes(ENCRYPTION_PASSWORD_LENGTH);
            boolean nonZeroByte = false;
            for (int i = 0; i < encpassword.length; i++) {
                if(encpassword[i] != 0){
                    nonZeroByte = true;
                    break;
                }
            }
            if(! nonZeroByte){
                // no password in the databasefile, work without encryption
                _stream.i_handlers.oldEncryptionOff();
            }else{
    			byte[] storedpwd=passwordToken();
    			for (int idx = 0; idx < storedpwd.length; idx++) {
    				if(storedpwd[idx]!=encpassword[idx]) {
    					_stream.fatalException(54);
    				}
    			}
            }
		}
        
        _freespaceSystem = FreespaceManager.FM_LEGACY_RAM;
        
        if(oldLength > FREESPACE_SYSTEM_OFFSET){
            _freespaceSystem = reader.readByte();
        }
        
        if(oldLength > FREESPACE_ADDRESS_OFFSET){
            _freespaceAddress = reader.readInt();
        }
        
        if(oldLength > CONVERTER_VERSION_OFFSET){
            _converterVersion = reader.readInt();
        }
        if(oldLength > UUID_INDEX_ID_OFFSET){
            _uuidIndexId = reader.readInt();
        }
        
        ensureFreespaceSlot();
		
		if(lockFile() && ( lastAccessTime != 0)){
			_stream.logMsg(28, null);
			long waitTime = YapConst.LOCK_TIME_INTERVAL * 10;
			long currentTime = System.currentTimeMillis();

			// If someone changes the system clock here,
			// he is out of luck.
			while(System.currentTimeMillis() < currentTime + waitTime){
				Cool.sleepIgnoringInterruption(waitTime);
			}
			reader = _stream.getWriter(_stream.getSystemTransaction(), _address, YapConst.LONG_LENGTH * 2);
			reader.moveForward(OPEN_TIME_OFFSET);
			reader.read();
			YLong.readLong(reader);  // open time
			long currentAccessTime = YLong.readLong(reader);
			if((currentAccessTime > lastAccessTime) ){
				throw new DatabaseFileLockedException();
			}
		}
		if(lockFile()){
			// We give the other process a chance to 
			// write its lock.
			Cool.sleepIgnoringInterruption(100);
			syncFiles();
			openTimeOverWritten();
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
				    Cool.sleepIgnoringInterruption(YapConst.LOCK_TIME_INTERVAL);
				}
			}catch(IOException e){
			}
		}
	}
    
    public void converterVersion(int ver){
        _converterVersion = ver;
    }
    
    public int converterVersion(){
        return _converterVersion;
    }
	
	void syncFiles(){
		_stream.syncFiles();
	}
	
	public void write() {
        
		headerLockOverwritten();
		_address = _stream.getSlot(LENGTH);
        
        // FIXME: Config block is written twice, not necessary, looses space 
        // System.err.println("Config block at " + _address);
        
		YapWriter writer = _stream.getWriter(_stream.i_trans, _address,LENGTH);
		YInt.writeInt(LENGTH, writer);
		YLong.writeLong(_opentime, writer);
		YLong.writeLong(_opentime, writer);
		writer.append(_encoding);
		YInt.writeInt(0, writer);
		YInt.writeInt(0, writer);
		YInt.writeInt(_bootRecordID, writer);
		YInt.writeInt(0, writer);  // dead byte from wrong attempt for blocksize
		writer.append(passwordToken());
        writer.append(_freespaceSystem);
        ensureFreespaceSlot();
        YInt.writeInt(_freespaceAddress, writer);
        YInt.writeInt(_converterVersion, writer);
        YInt.writeInt(_uuidIndexId, writer);
		writer.write();
		writePointer();
	}
	
	boolean writeAccessTime() throws IOException{
		return _stream.writeAccessTime();
	}
	
	private void writeOpenTime(){
		if(lockFile()){
			YapWriter writer = openTimeIO();
			YLong.writeLong(_opentime, writer);
			writer.write();
		}
	}
	
	private void writeHeaderLock(){
		if(lockFile()){
			YapWriter writer = headerLockIO();
			YInt.writeInt(((int)_opentime), writer);
            
            // System.out.println("Written by " + System.identityHashCode(this) + " " + intOpenTime);
            
			writer.write();
		}
	}
	
	private void writePointer() {
		headerLockOverwritten();
		YapWriter writer = _stream.getWriter(_stream.i_trans, 0, YapConst.ID_LENGTH);
		writer.moveForward(2);
		YInt.writeInt(_address, writer);
		if(Debug.xbytes && Deploy.overwrite){
			writer.setID(YapConst.IGNORE_ID);
		}
		writer.write();
		writeHeaderLock();
	}
	
}

