/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

public class VodId {
	
	public final long loid;
	
	public final long timestamp;

	public VodId(long loid, long timestamp) {
		this.loid = loid;
		this.timestamp = timestamp;
	}
	
}
