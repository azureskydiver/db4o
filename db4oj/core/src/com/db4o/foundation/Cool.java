package com.db4o.foundation;

/**
 * A collection of cool static methods that should be part of the runtime environment but are not.
 */
public class Cool {

	public static void sleepIgnoringInterruption(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
   		}
	}

}
