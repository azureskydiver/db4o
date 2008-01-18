/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.timing;

import com.db4o.internal.*;


public class NanoTiming {

	public long nanoTime() {
		return Platform4.nanoTime();
	}

	public void waitNano(long nanos) {
		long target = nanoTime() + nanos;
	    while (nanoTime() <= target) {
	    }
	}
	
}
