/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.io.*;


/**
 * @exclude
 */
public class TimerFileLockEnabled extends TimerFileLock{
    
    private final IoAdapter _timerFile;
    
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
        _timerFile = file.timerFile();
        _opentime = uniqueOpenTime();
    }
    
    public void checkHeaderLock() {
    	try {
			if( ((int)_opentime) == readInt(0, _headerLockOffset)){
				writeHeaderLock();
				return;
			}
		} catch (IOException e) {
			
		}
		throw new DatabaseFileLockedException();
    }
    
    public void checkOpenTime() {
    	try {
			if(_opentime == readLong(_baseAddress, _openTimeOffset)){
				writeOpenTime();
				return;
			}
		} catch (IOException e) {
			
		}
		throw new DatabaseFileLockedException();
    }
    
    public void close() throws IOException {
        writeAccessTime(true);
        _closed = true;
    }
    
    public boolean lockFile() {
        return true;
    }
    
    public long openTime() {
        return _opentime;
    }

    public void run(){
        Thread t = Thread.currentThread();
        t.setName("db4o file lock");
        try{
            while(writeAccessTime(false)){
                Cool.sleepIgnoringInterruption(Const4.LOCK_TIME_INTERVAL);
                if(_closed){
                    break;
                }
            }
        }catch(IOException e){
        }
    }

    public void setAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset){
        _baseAddress = baseAddress;
        _openTimeOffset = openTimeOffset;
        _accessTimeOffset = accessTimeOffset;
    }
    
    public void start() throws IOException{
        writeAccessTime(false);
        _timerFile.sync();
        checkOpenTime();
        // TODO: Thread could be a daemon.
        new Thread(this).start(); 
    }
    
    private long uniqueOpenTime(){
        return  System.currentTimeMillis();
        // TODO: More security is possible here to make this time unique
        // to other processes. 
    }
    
    private boolean writeAccessTime(boolean closing) throws IOException{
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
    	try {
			writeInt(0, _headerLockOffset, (int)_opentime);
			sync();
		} catch (IOException e) {
			
		}
    }

    public void writeOpenTime() {
    	try {
			writeLong(_baseAddress, _openTimeOffset, _opentime);
			sync();
		} catch (IOException e) {
			
		}
    }
    
    private boolean writeLong(int address, int offset, long time) throws IOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return false;
            }
            _timerFile.blockSeek(address, offset);
            if (Deploy.debug) {
                Buffer lockBytes = new Buffer(Const4.LONG_LENGTH);
                lockBytes.writeLong(time);
                _timerFile.write(lockBytes._buffer);
            } else {
            	PrimitiveCodec.writeLong(_longBytes, time);
                _timerFile.write(_longBytes);
            }
            return true;
    	}
    }
    
    private long readLong(int address, int offset) throws IOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return 0;
            }
            _timerFile.blockSeek(address, offset);
            if (Deploy.debug) {
                Buffer lockBytes = new Buffer(Const4.LONG_LENGTH);
                _timerFile.read(lockBytes._buffer, Const4.LONG_LENGTH);
                return lockBytes.readLong();
            }
            _timerFile.read(_longBytes);
            return PrimitiveCodec.readLong(_longBytes, 0);
    	}
    }
    
    private boolean writeInt(int address, int offset, int time) throws IOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return false;
            }
            _timerFile.blockSeek(address, offset);
            if (Deploy.debug) {
                Buffer lockBytes = new Buffer(Const4.INT_LENGTH);
                lockBytes.writeInt(time);
                _timerFile.write(lockBytes._buffer);
            } else {
            	PrimitiveCodec.writeInt(_intBytes, 0, time);
                _timerFile.write(_intBytes);
            }
            return true;
    	}
    }
    
    private long readInt(int address, int offset) throws IOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return 0;
            }
            _timerFile.blockSeek(address, offset);
            if (Deploy.debug) {
                Buffer lockBytes = new Buffer(Const4.INT_LENGTH);
                _timerFile.read(lockBytes._buffer, Const4.INT_LENGTH);
                return lockBytes.readInt();
            }
            _timerFile.read(_longBytes);
            return PrimitiveCodec.readInt(_longBytes, 0);
    	}
    }
    
    private void sync() throws IOException{
    	_timerFile.sync();
    }
    
}


