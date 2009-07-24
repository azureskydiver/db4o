/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */
package com.db4o.internal.threading;

import com.db4o.events.*;

public interface ThreadPool4 {
	
	void start(Runnable task);

	void startLowPriority(Runnable task);
	
	/**
	 * @sharpen.event UncaughtExceptionEventArgs
	 */
	Event4<UncaughtExceptionEventArgs> uncaughtException();

	void join(int timeoutMilliseconds) throws InterruptedException;


}
