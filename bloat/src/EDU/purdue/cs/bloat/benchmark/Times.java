/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 *
 * <p>
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and other
 * materials related to such distribution and use acknowledge that the
 * software was developed at Purdue University, West Lafayette, IN by
 * Antony Hosking, David Whitlock, and Nathaniel Nystrom.  No charge
 * may be made for copies, derivations, or distributions of this
 * material without the express written consent of the copyright
 * holder.  Neither the name of the University nor the name of the
 * author may be used to endorse or promote products derived from this
 * material without specific prior written permission.  THIS SOFTWARE
 * IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 *
 * <p>
 * Java is a trademark of Sun Microsystems, Inc.
 */

package EDU.purdue.cs.bloat.benchmark;

/**
 * This class allows Java to access the information obtained by the UNIX system
 * call <tt>times</tt>.
 */
public class Times {
	static {
		// Load native code from libbenchmark.so
		System.loadLibrary("times");
	}

	static float userTime;

	static float systemTime;

	/**
	 * Takes a "snapshot" of the system. Reads various items from the result of
	 * <tt>times</tt>.
	 * 
	 * @return <tt>true</tt> if everything is successful
	 */
	public static native boolean snapshot();

	/**
	 * Returns the user time used by this process in seconds.
	 */
	public static float userTime() {
		return (Times.userTime);
	}

	/**
	 * Returns the system time used by this process in seconds.
	 */
	public static float systemTime() {
		return (Times.systemTime);
	}

	/**
	 * Test program.
	 */
	public static void main(final String[] args) throws Exception {
		System.out.println("Starting Test");

		if (Times.snapshot() == false) {
			System.err.println("Error during snapshot");
			System.exit(1);
		}

		System.out.println("System time: " + Times.systemTime());
		System.out.println("User time: " + Times.userTime());

		System.out.println("Ending Test");
	}

}
