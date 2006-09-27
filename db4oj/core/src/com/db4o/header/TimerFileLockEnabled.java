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
    
    private int _openTimeAddress;
    
    private int _openTimeOffset;
    
    
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
        if(_openTimeAddress == 0){
            return null;
        }
        YapWriter writer = getWriter(_openTimeAddress,  _openTimeOffset, YapConst.LONG_LENGTH);
        if (Debug.xbytes) {
            writer.setID(YapConst.IGNORE_ID);
        }
        return writer;
    }

    public void run(){
        Thread t = Thread.currentThread();
        t.setName("db4o file lock");
        try{
            while(_file.writeAccessTime()){
                Cool.sleepIgnoringInterruption(YapConst.LOCK_TIME_INTERVAL);
            }
        }catch(IOException e){
        }
    }

    public void setOpenTimeAddress(int address, int offset){
        _openTimeAddress = address;
        _openTimeOffset = offset;
    }

    public void start() throws IOException{
        _file.writeAccessTime();
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


