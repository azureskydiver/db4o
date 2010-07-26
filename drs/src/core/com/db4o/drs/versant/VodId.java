/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

public class VodId {
	
	public final int databaseId;
	
	public final int objectId1;
	
	public final long objectId2;
	
	public final long timestamp;

	public VodId(int databaseId, int objectId1, long objectId2, long timestamp) {
		this.databaseId = databaseId;
		this.objectId1 = objectId1;
		this.objectId2 = objectId2;
		this.timestamp = timestamp;
	}
	


}
