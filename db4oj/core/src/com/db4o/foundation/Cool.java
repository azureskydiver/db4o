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
	
	/**
	 * Keeps executing a block of code until it either returns false or millisecondsTimeout
	 * elapses.
	 * 
	 * @param millisecondsTimeout
	 * @param block
	 */
	public static void loopWithTimeout(long millisecondsTimeout, ConditionalBlock block) {
		final StopWatch watch = new StopWatch();
		watch.start();
		do {
			if (!block.run()) {
				break;
			}
		} while (watch.peek() < millisecondsTimeout);
	}

}
