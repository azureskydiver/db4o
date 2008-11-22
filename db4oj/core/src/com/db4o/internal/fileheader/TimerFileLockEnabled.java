/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.io.*;


/**
 * @exclude
 */
public class TimerFileLockEnabled extends TimerFileLock{
    
    private final BlockAwareBin _timerFile;
    
    private final Object _timerLock;
    
    private byte[] _longBytes = new byte[Const4.LONG_LENGTH];
    
    private byte[] _intBytes = new byte[Const4.INT_LENGTH];
    
    private int _headerLockOffset = 2 + Const4.INT_LENGTH; 
    
    private final long _opentime;
    
    private int _baseAddress = -1;
    
    private int _openTimeOffset;

    private int _accessTimeOffset;
    
    private boolean _closed = false;
    
    public TimerFileLockEnabled(IoAdaptedObjectContainer file) {
        _timerLock = file.lock();
        
        // FIXME: No reason to sync over the big master lock.
        //        A local lock should be OK.
        // _timerLock = new Object();
        
        _timerFile = file.timerFile();
        _opentime = uniqueOpenTime();
    }
    
    public void checkHeaderLock() {
    	if( ((int)_opentime) != readInt(0, _headerLockOffset)){
    		throw new DatabaseFileLockedException(_timerFile.toString());	
    	}
		writeHeaderLock();
    }
    
    public void checkOpenTime() {
		long readOpenTime = readLong(_baseAddress, _openTimeOffset);
		if (_opentime != readOpenTime) {
			throw new DatabaseFileLockedException(_timerFile.toString());
		}
		writeOpenTime();		
	}
    
    public void checkIfOtherSessionAlive(LocalObjectContainer container, int address, int offset,
		long lastAccessTime) throws Db4oIOException {
    	if(_timerFile == null) { // need to check? 
    		return;
    	}
		long waitTime = Const4.LOCK_TIME_INTERVAL * 5;
		long currentTime = System.currentTimeMillis();
		
		// If someone changes the system clock here, he is out of luck.
		while (System.currentTimeMillis() < currentTime + waitTime) {
			Cool.sleepIgnoringInterruption(waitTime);
		}
		
		long currentAccessTime = readLong(address, offset);
		if ((currentAccessTime > lastAccessTime)) {
			throw new DatabaseFileLockedException(container.toString());
		}
	}
    
    public void close() throws Db4oIOException {
        writeAccessTime(true);
        synchronized (_timerLock) {
			_closed = true;
		}
    }
    
    public boolean lockFile() {
        return true;
    }
    
    public long openTime() {
        return _opentime;
    }

    public void run() {
		while (true) {
			synchronized (_timerLock) {
				if (_closed) {
					return;
				}
				writeAccessTime(false);
			}
			Cool.sleepIgnoringInterruption(Const4.LOCK_TIME_INTERVAL);
		}
	}

    public void setAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset){
        _baseAddress = baseAddress;
        _openTimeOffset = openTimeOffset;
        _accessTimeOffset = accessTimeOffset;
    }
    
    public void start() throws Db4oIOException{
        writeAccessTime(false);
        _timerFile.sync();
        checkOpenTime();
        Thread thread = new Thread(this);
        thread.setName("db4o file lock");
        thread.setDaemon(true);
        thread.start(); 
    }
    
    private long uniqueOpenTime(){
        return  System.currentTimeMillis();
        // TODO: More security is possible here to make this time unique
        // to other processes. 
    }
    
    private boolean writeAccessTime(boolean closing) throws Db4oIOException {
        if(noAddressSet()){
            return true;
        }
        long time = closing ? 0 : System.currentTimeMillis();
        boolean ret = writeLong(_baseAddress, _accessTimeOffset, time);
        sync();
        return ret;
    }

	private boolean noAddressSet() {
		return _baseAddress < 0;
	}

    public void writeHeaderLock(){
    	writeInt(0, _headerLockOffset, (int)_opentime);
		sync();
    }

    public void writeOpenTime() {
    	writeLong(_baseAddress, _openTimeOffset, _opentime);
		sync();
    }
    
    private boolean writeLong(int address, int offset, long time) throws Db4oIOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return false;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.LONG_LENGTH);
                lockBytes.writeLong(time);
                _timerFile.blockWrite(address, offset, lockBytes._buffer);
            } else {
            	PrimitiveCodec.writeLong(_longBytes, time);
                _timerFile.blockWrite(address, offset, _longBytes);
            }
            return true;
    	}
    }
    
    private long readLong(int address, int offset) throws Db4oIOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return 0;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.LONG_LENGTH);
                _timerFile.blockRead(address, offset, lockBytes._buffer, Const4.LONG_LENGTH);
                return lockBytes.readLong();
            }
            _timerFile.blockRead(address, offset, _longBytes);
            return PrimitiveCodec.readLong(_longBytes, 0);
    	}
    }
    
    private boolean writeInt(int address, int offset, int time) {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return false;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.INT_LENGTH);
                lockBytes.writeInt(time);
                _timerFile.blockWrite(address, offset, lockBytes._buffer);
            } else {
            	PrimitiveCodec.writeInt(_intBytes, 0, time);
                _timerFile.blockWrite(address, offset, _intBytes);
            }
            return true;
    	}
    }
    
    private long readInt(int address, int offset)  {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return 0;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.INT_LENGTH);
                _timerFile.blockRead(address, offset, lockBytes._buffer, Const4.INT_LENGTH);
                return lockBytes.readInt();
            }
            _timerFile.blockRead(address, offset, _longBytes);
            return PrimitiveCodec.readInt(_longBytes, 0);
    	}
    }
    
    private void sync() throws Db4oIOException {
    	_timerFile.sync();
    }
    
}


