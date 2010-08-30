/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.metadata;


public class UuidMapping extends CobraPersistentObject {

	private int otherDb;
	private long otherLongPart;
	
	private int mineDb;
	private long mineLongPart;
	
	public UuidMapping(int otherDb, long otherLongPart, int mineDb, long mineLongPart) {
		super();
		this.otherDb = otherDb;
		this.otherLongPart = otherLongPart;
		this.mineDb = mineDb;
		this.mineLongPart = mineLongPart;
	}

	public int otherDb() {
		return otherDb;
	}
	
	public long otherLongPart() {
		return otherLongPart;
	}

	public int mineDb() {
		return mineDb;
	}
	
	public long mineLongPart() {
		return mineLongPart;
	}
	
}
