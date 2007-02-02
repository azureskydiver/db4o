/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.header.*;
import com.db4o.inside.handlers.*;


/**
 * configuration and agent to write the configuration block
 * The configuration block also contains the timer lock and
 * a pointer to the running transaction.
 * @exclude
 */
public final class ConfigBlock {
    
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
    // int    converter versions
    
	private final LocalObjectContainer		_stream;
    private final TimerFileLock _timerFileLock;
    
	private int					_address;
	private Transaction			_transactionToCommit;
	public int                 	_bootRecordID;
	
	private static final int	MINIMUM_LENGTH = 
		Const4.INT_LENGTH    			// own length
		+ (Const4.LONG_LENGTH * 2)	 	// candidate ID and last access time
		+ 1;  						// Unicode byte
	
	static final int			OPEN_TIME_OFFSET		= Const4.INT_LENGTH;
	public static final int     ACCESS_TIME_OFFSET      = OPEN_TIME_OFFSET + Const4.LONG_LENGTH;
		
	public static final int		TRANSACTION_OFFSET = MINIMUM_LENGTH;
	private static final int	BOOTRECORD_OFFSET = TRANSACTION_OFFSET + Const4.INT_LENGTH * 2;  
	private static final int	INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET = BOOTRECORD_OFFSET + Const4.INT_LENGTH;
	private static final int	ENCRYPTION_PASSWORD_LENGTH = 5;
    private static final int    PASSWORD_OFFSET = INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET+ENCRYPTION_PASSWORD_LENGTH;
    private static final int    FREESPACE_SYSTEM_OFFSET = PASSWORD_OFFSET + 1; 
    private static final int    FREESPACE_ADDRESS_OFFSET = FREESPACE_SYSTEM_OFFSET + Const4.INT_LENGTH; 
    private static final int    CONVERTER_VERSION_OFFSET = FREESPACE_ADDRESS_OFFSET + Const4.INT_LENGTH;
    private static final int    UUID_INDEX_ID_OFFSET = CONVERTER_VERSION_OFFSET + Const4.INT_LENGTH;
	
	
	// complete possible data in config block
	private static final int	LENGTH = 
		MINIMUM_LENGTH 
		+ (Const4.INT_LENGTH * 7)	// (two transaction pointers, PDB ID, lost int, freespace address, converter_version, index id)
	    + ENCRYPTION_PASSWORD_LENGTH
        + 1;
    
    
    public static ConfigBlock forNewFile(LocalObjectContainer file) throws IOException{
        return new ConfigBlock(file, true, 0);
    }
    
    public static ConfigBlock forExistingFile(LocalObjectContainer file, int address) throws IOException{
        return new ConfigBlock(file, false, address);
    }
    
	private ConfigBlock(LocalObjectContainer stream, boolean isNew, int address) throws IOException{
		_stream = stream;
        _timerFileLock = TimerFileLock.forFile(stream);
        timerFileLock().writeHeaderLock();
        if(! isNew){
            read(address);
        }
        timerFileLock().start();
	}
    
    private TimerFileLock timerFileLock(){
        return _timerFileLock;
    }
    
    public long openTime(){
        return timerFileLock().openTime();
    }
	
	public Transaction getTransactionToCommit(){
		return _transactionToCommit;
	}
    
	private boolean lockFile(){
		if(! Debug.lockFile){
			return false;
		}
		return _stream.needsLockFileThread();
	}
    
    private byte[] passwordToken() {
        byte[] pwdtoken=new byte[ENCRYPTION_PASSWORD_LENGTH];
        String fullpwd=_stream.configImpl().password();
        if(_stream.configImpl().encrypt() && fullpwd!=null) {
            try {
                byte[] pwdbytes=new LatinStringIO().write(fullpwd);
                Buffer encwriter=new StatefulBuffer(_stream.getTransaction(),pwdbytes.length+ENCRYPTION_PASSWORD_LENGTH);
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
    
    private SystemData systemData(){
        return _stream.systemData();
    }
	
	private void read(int address) {
        addressChanged(address);
		timerFileLock().writeOpenTime();
		StatefulBuffer reader = _stream.getWriter(_stream.getSystemTransaction(), _address, LENGTH);
		try{
			_stream.readBytes(reader._buffer, _address, LENGTH);
		}catch(Exception e){
			// TODO: Exception handling
		}
		int oldLength = reader.readInt();
		if(oldLength > LENGTH  || oldLength < MINIMUM_LENGTH){
			Exceptions4.throwRuntimeException(Messages.INCOMPATIBLE_FORMAT);
		}
        if(oldLength != LENGTH){
        	// TODO: instead of bailing out, somehow trigger wrapping the stream's io adapter in
        	// a readonly decorator, issue a  notification and continue?
            if(! _stream.configImpl().isReadOnly()  && ! _stream.configImpl().allowVersionUpdates()){
            	if(_stream.configImpl().automaticShutDown()) {
            		Platform4.removeShutDownHook(_stream, _stream.i_lock);
            	}
                throw new OldFormatException();
            }
        }
        
        reader.readLong(); // open time 
		long lastAccessTime = reader.readLong();
        
        systemData().stringEncoding(reader.readByte());
		
		if(oldLength > TRANSACTION_OFFSET){
            _transactionToCommit = Transaction.readInterruptedTransaction(_stream, reader);
		}
		
		if(oldLength > BOOTRECORD_OFFSET) {
		    _bootRecordID = reader.readInt();
		}
		
		if(oldLength > INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET) {
		    // this one is dead.
		    // Blocksize is in the very first bytes
		    reader.readInt();
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
        
        if(oldLength > FREESPACE_SYSTEM_OFFSET){
            systemData().freespaceSystem(reader.readByte());
        }
        
        if(oldLength > FREESPACE_ADDRESS_OFFSET){
            systemData().freespaceAddress(reader.readInt());
        }
        
        if(oldLength > CONVERTER_VERSION_OFFSET){
            systemData().converterVersion(reader.readInt());
        }
        if(oldLength > UUID_INDEX_ID_OFFSET){
            final int uuidIndexId = reader.readInt();
            if (0 != uuidIndexId) {
            	systemData().uuidIndexId(uuidIndexId);
            }
        }
        
        _stream.ensureFreespaceSlot();
        
		if(lockFile() && ( lastAccessTime != 0)){
			_stream.logMsg(28, null);
			long waitTime = Const4.LOCK_TIME_INTERVAL * 5;
			long currentTime = System.currentTimeMillis();

			// If someone changes the system clock here,
			// he is out of luck.
			while(System.currentTimeMillis() < currentTime + waitTime){
				Cool.sleepIgnoringInterruption(waitTime);
			}
			reader = _stream.getWriter(_stream.getSystemTransaction(), _address, Const4.LONG_LENGTH * 2);
			reader.moveForward(OPEN_TIME_OFFSET);
			reader.read();
            
			reader.readLong();  // open time
            
			long currentAccessTime = reader.readLong();
			if((currentAccessTime > lastAccessTime) ){
				throw new DatabaseFileLockedException();
			}
		}
		if(lockFile()){
			// We give the other process a chance to 
			// write its lock.
			Cool.sleepIgnoringInterruption(100);
            _stream.syncFiles();
            timerFileLock().checkOpenTime();
		}
		if(oldLength < LENGTH){
			write();
		}
	}
	
	public void write() {
        
        timerFileLock().checkHeaderLock();
        addressChanged(_stream.getSlot(LENGTH));
        
		StatefulBuffer writer = _stream.getWriter(_stream.getTransaction(), _address,LENGTH);
		IntHandler.writeInt(LENGTH, writer);
        for (int i = 0; i < 2; i++) {
            writer.writeLong(timerFileLock().openTime());
        }
		writer.append(systemData().stringEncoding());
		IntHandler.writeInt(0, writer);
		IntHandler.writeInt(0, writer);
		IntHandler.writeInt(_bootRecordID, writer);
		IntHandler.writeInt(0, writer);  // dead byte from wrong attempt for blocksize
		writer.append(passwordToken());
        writer.append(systemData().freespaceSystem());
        _stream.ensureFreespaceSlot();
        IntHandler.writeInt(systemData().freespaceAddress(), writer);
        IntHandler.writeInt(systemData().converterVersion(), writer);
        IntHandler.writeInt(systemData().uuidIndexId(), writer);
		writer.write();
		writePointer();
	}
    
    private void addressChanged(int address){
        _address = address;
        timerFileLock().setAddresses(_address, OPEN_TIME_OFFSET, ACCESS_TIME_OFFSET);
    }
	
	private void writePointer() {
        timerFileLock().checkHeaderLock();
		StatefulBuffer writer = _stream.getWriter(_stream.getTransaction(), 0, Const4.ID_LENGTH);
		writer.moveForward(2);
		IntHandler.writeInt(_address, writer);
        writer.noXByteCheck();
		writer.write();
		timerFileLock().writeHeaderLock();
	}
    
    public int address(){
        return _address;
    }

    public void close() throws IOException {
        timerFileLock().close();
    }
	
}

