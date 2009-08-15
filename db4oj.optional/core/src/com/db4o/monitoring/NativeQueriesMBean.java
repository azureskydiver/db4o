/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

/**
 * Native queries statistics. 
 * 
 * In C/S mode only applicable to clients.
 */
@decaf.Ignore
public interface NativeQueriesMBean {
	
	double getUnoptimizedNativeQueriesPerSecond();
	double getNativeQueriesPerSecond();
	
}
