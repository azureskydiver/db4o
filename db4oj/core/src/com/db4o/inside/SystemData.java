/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.ext.Db4oDatabase;
import com.db4o.header.*;

/**
 * @exclude
 */
public class SystemData {
    
    private int _classCollectionID;
    
    private int _converterVersion;
    
    private int _freespaceAddress;
    
    private int _freespaceID;
    
    private byte _freespaceSystem;
    
    private Db4oDatabase _identity;
    
    private long _lastTimeStampID;
    
    private byte _stringEncoding;

    private int _uuidIndexId;
    
    private final TimerFileLock _timerFileLock;
    
    public SystemData(YapFile file){
        _timerFileLock = TimerFileLock.forFile(file);
    }
    
    public int classCollectionID() {
        return _classCollectionID;
    }
    
    public void classCollectionID(int id) {
        _classCollectionID = id;
    }
    
    public int converterVersion(){
        return _converterVersion;
    }

    public void converterVersion(int version){
        _converterVersion = version;
    }
    
    public int freespaceAddress(){
        return _freespaceAddress;
    }
    
    public void freespaceAddress(int address){
        _freespaceAddress = address;
    }

    public int freespaceID() {
        return _freespaceID;
    }

    public void freespaceID(int id) {
        _freespaceID = id;
    }
    
    public byte freespaceSystem() {
        return _freespaceSystem;
    }
    
    public void freespaceSystem(byte freespaceSystemtype){
        _freespaceSystem = freespaceSystemtype;
    }
    
    public Db4oDatabase identity(){
        return _identity;
    }
    
    public void identity(Db4oDatabase identityObject) {
        _identity = identityObject;
    }

    public long lastTimeStampID(){
        return _lastTimeStampID;
    }
    
    public void lastTimeStampID(long id) {
        _lastTimeStampID = id;
    }
    
    public byte stringEncoding(){
        return _stringEncoding;
    }
    
    public void stringEncoding(byte encodingByte){
        _stringEncoding = encodingByte; 
    }
    
    public TimerFileLock timerFileLock(){
        return _timerFileLock;
    }

    public int uuidIndexId(){
        return _uuidIndexId;
    }
    
    public void uuidIndexId(int id){
        _uuidIndexId = id;
    }
    
}
