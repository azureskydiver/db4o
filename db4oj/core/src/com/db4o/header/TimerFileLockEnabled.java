/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public class TimerFileLockEnabled extends TimerFileLock{
    
    private final YapFile _file;
    
    private int _headerLockOffset = 2 + YapConst.INT_LENGTH; 
    
    private final long _opentime;
    
    private int _baseAddress;
    
    private int _openTimeOffset;

    private int _accessTimeOffset;
    
    private boolean _closed = false;
    
    
    public TimerFileLockEnabled(YapFile file) {
        _file = file;
        _opentime = uniqueOpenTime();
    }
    
    public void checkHeaderLock() {
        YapWriter reader = headerLockIO();
        reader.read();
        if(reader.readInt() != (int)_opentime ){
            throw new DatabaseFileLockedException();
        }
        writeHeaderLock();
    }
    
    public void checkOpenTime() {
        YapWriter reader = openTimeIO();
        if(reader == null){
            return;
        }
        reader.read();
        if(YLong.readLong(reader) != _opentime){
            Exceptions4.throwRuntimeException(22);
        }
        writeOpenTime();
    }
    
    public void close() throws IOException {
        writeAccessTime(true);
        _closed = true;
    }
    
    private YapWriter getWriter(int address, int offset, int length) {
        YapWriter writer = _file.getWriter(_file.getTransaction(), address, length);
        writer.moveForward(offset);
        return writer; 
    }
    
    private YapWriter headerLockIO(){
        YapWriter writer = getWriter(0, _headerLockOffset, YapConst.INT_LENGTH);
        if (Debug.xbytes) {
            writer.setID(YapConst.IGNORE_ID);
        }
        return writer;
    }

    public boolean lockFile() {
        return true;
    }
    
    public long openTime() {
        return _opentime;
    }

    private YapWriter openTimeIO(){
        if(_baseAddress == 0){
            return null;
        }
        YapWriter writer = getWriter(_baseAddress,  _openTimeOffset, YapConst.LONG_LENGTH);
        if (Debug.xbytes) {
            writer.setID(YapConst.IGNORE_ID);
        }
        return writer;
    }

    public void run(){
        Thread t = Thread.currentThread();
        t.setName("db4o file lock");
        try{
            while(writeAccessTime(false)){
                Cool.sleepIgnoringInterruption(YapConst.LOCK_TIME_INTERVAL);
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
        _file.syncFiles();
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
        if(_baseAddress < 1){
            return true;
        }
        long time = closing ? 0 : System.currentTimeMillis();
        return _file.writeAccessTime(_baseAddress, _accessTimeOffset, time);
    }


    public void writeHeaderLock(){
        YapWriter writer = headerLockIO();
        writer.writeInt((int)_opentime);
        writer.write();
    }

    public void writeOpenTime() {
        YapWriter writer = openTimeIO();
        if(writer== null){
            return;
        }
        YLong.writeLong(_opentime, writer);
        writer.write();
    }
    
}


