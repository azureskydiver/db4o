/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

/**
 * A collection of cool static methods that should be part of the runtime environment but are not.
 * 
 * @exclude
 */
public class Cool {

	public static void sleepIgnoringInterruption(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception ignored) {
   		}
	}

	public static void loopWithTimeout(long millisecondsTimeout, Runnable block) {
		final StopWatch watch = new StopWatch();
		watch.start();
		do {
			block.run();
		} while (watch.peek() < millisecondsTimeout);
	}

}
