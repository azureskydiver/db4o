/*
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 * <p>
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
package EDU.purdue.cs.bloat.util;

/**
 * Mechanism for making assertions about things in BLOAT. If an assertion fails,
 * an <tt>IllegalArgumentException</tt> is thrown.
 */
public abstract class Assert {
	public static void isTrue(boolean test, final String msg) {
		if (!test) {
			throw new IllegalArgumentException("Assert.isTrue: " + msg);
		}
	}

	public static void isFalse(final boolean test, final String msg) {
		if (test) {
			throw new IllegalArgumentException("Assert.isFalse: " + msg);
		}
	}

	public static void isNotNull(final Object test, final String msg) {
		if (test == null) {
			throw new IllegalArgumentException("Assert.isNotNull: " + msg);
		}
	}

	public static void isNull(final Object test, final String msg) {
		if (test != null) {
			throw new IllegalArgumentException("Assert.isNull: " + msg);
		}
	}

	public static void isTrue(boolean test) {
		if (!test) {
			throw new IllegalArgumentException("Assert.isTrue failed");
		}
	}

	public static void isFalse(final boolean test) {
		if (test) {
			throw new IllegalArgumentException("Assert.isFalse failed");
		}
	}

	public static void isNotNull(final Object test) {
		if (test == null) {
			throw new IllegalArgumentException("Assert.isNotNull failed");
		}
	}

	public static void isNull(final Object test) {
		if (test != null) {
			throw new IllegalArgumentException("Assert.isNull failed");
		}
	}
}
