/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.metadata;

import com.db4o.internal.encoding.*;

public class DatabaseSignature {
	
	private int databaseId;
	
	private byte[] signature;
	
	public DatabaseSignature(int databaseId, byte[] signature){
		this.databaseId = databaseId;
		this.signature = signature;
	}
	
	public int databaseId(){
		return databaseId;
	}
	
	public byte[] signature(){
		return signature;
	}
	
	@Override
	public String toString() {
		return "DatabaseSignature databaseId:" + databaseId + " signature:" + new LatinStringIO().read(signature);
	}

}
