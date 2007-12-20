/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.timing;


public interface NanoTiming {

	public long nanoTime();
	
	public void waitNano(long nanos);
	
}
