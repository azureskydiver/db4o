/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.metadata;

public class SignatureToDatabaseId {
	
	private int databaseId;
	
	private byte[] signature;
	
	public SignatureToDatabaseId(int databaseId, byte[] signature){
		this.databaseId = databaseId;
		this.signature = signature;
	}
	
	public int databaseId(){
		return databaseId;
	}
	
	public byte[] signature(){
		return signature;
	}

}
