/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.timing;

import com.db4o.internal.*;

/**
 * @sharpen.ignore
 */
public final class NanoTiming {
    
    private final JDK jdk = Platform4.jdk();

	public final long nanoTime() {
        return jdk.nanoTime();
	}

	public final void waitNano(final long nanos) {
		final long target = jdk.nanoTime() + nanos;
	    while (jdk.nanoTime() <= target) {
	    }
	}
	
}
