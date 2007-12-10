/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;


public class Delays {

	public long readDelay;
	public long writeDelay;
	public long seekDelay;
	public long syncDelay;
	
	public String units;
	
	public static final String UNITS_MILLISECONDS = "milliseconds";
	public static final String UNITS_NANOSECONDS = "nanoseconds";
	
	public Delays(long read, long write, long seek, long sync, String timeUnits) {
		readDelay = read;
		writeDelay = write;
		seekDelay = seek;
		syncDelay = sync;
		units = timeUnits;
	}
	
	public String toString() {
		return "[delays in " + units + "] read: " + readDelay + " | write: " + writeDelay +
				" | seek: " + seekDelay + " | sync: " + syncDelay;
	}
	
}
