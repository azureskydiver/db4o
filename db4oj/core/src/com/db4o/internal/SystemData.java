/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;

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
    
    private int _identityId;
    
    private long _lastTimeStampID;
    
    private byte _stringEncoding;

    private int _uuidIndexId;
    
    private byte _idSystemType;
    
    private int _idSystemID;
    
    private int _transactionPointer1;
    
    private int _transactionPointer2;
    
    public SystemData(){
    	
    }
    
    public void idSystemType(byte idSystem) {
		_idSystemType = idSystem;
	}

	public byte idSystemType() {
		return _idSystemType;
	}

	public void idSystemID(int idSystemID) {
		_idSystemID = idSystemID;
	}

	public int idSystemID() {
		return _idSystemID;
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
    
    public int uuidIndexId(){
        return _uuidIndexId;
    }
    
    public void uuidIndexId(int id){
        _uuidIndexId = id;
    }

	public void identityId(int id) {
		_identityId = id;
	}
	
	public int identityId(){
		return _identityId;
	}
	
	public void transactionPointer1(int pointer){
		_transactionPointer1 = pointer;
	}
	
	public void transactionPointer2(int pointer){
		_transactionPointer2 = pointer;
	}
	
	public int transactionPointer1(){
		return _transactionPointer1;
	}
	
	public int transactionPointer2(){
		return _transactionPointer2;
	}
	
}
