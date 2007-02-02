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
    
    private final LocalObjectContainer _file;
    
    private int _headerLockOffset = 2 + Const4.INT_LENGTH; 
    
    private final long _opentime;
    
    private int _baseAddress;
    
    private int _openTimeOffset;

    private int _accessTimeOffset;
    
    private boolean _closed = false;
    
    
    public TimerFileLockEnabled(LocalObjectContainer file) {
        _file = file;
        _opentime = uniqueOpenTime();
    }
    
    public void checkHeaderLock() {
        StatefulBuffer reader = headerLockIO();
        reader.read();
        if(reader.readInt() != (int)_opentime ){
            throw new DatabaseFileLockedException();
        }
        writeHeaderLock();
    }
    
    public void checkOpenTime() {
        StatefulBuffer reader = openTimeIO();
        if(reader == null){
            return;
        }
        reader.read();
        if(reader.readLong() != _opentime){
            Exceptions4.throwRuntimeException(22);
        }
        writeOpenTime();
    }
    
    public void close() throws IOException {
        writeAccessTime(true);
        _closed = true;
    }
    
    private StatefulBuffer getWriter(int address, int offset, int length) {
        StatefulBuffer writer = _file.getWriter(_file.getTransaction(), address, length);
        writer.moveForward(offset);
        return writer; 
    }
    
    private StatefulBuffer headerLockIO(){
        StatefulBuffer writer = getWriter(0, _headerLockOffset, Const4.INT_LENGTH);
        if (Debug.xbytes) {
            writer.setID(Const4.IGNORE_ID);
        }
        return writer;
    }

    public boolean lockFile() {
        return true;
    }
    
    public long openTime() {
        return _opentime;
    }

    private StatefulBuffer openTimeIO(){
        if(_baseAddress == 0){
            return null;
        }
        StatefulBuffer writer = getWriter(_baseAddress,  _openTimeOffset, Const4.LONG_LENGTH);
        if (Debug.xbytes) {
            writer.setID(Const4.IGNORE_ID);
        }
        return writer;
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
        StatefulBuffer writer = headerLockIO();
        writer.writeInt((int)_opentime);
        writer.write();
    }

    public void writeOpenTime() {
        StatefulBuffer writer = openTimeIO();
        if(writer== null){
            return;
        }
        writer.writeLong(_opentime);
        writer.write();
    }
    
}


