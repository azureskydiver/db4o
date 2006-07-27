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

import java.io.*;

/**
 * The <tt>BenchmarkSecurityManager</tt> allows us to execute a "main" method
 * multiple times without the virtual machine exiting. If exit is not allowed,
 * the <tt>checkExit</tt> method will throw a <tt>SecurityException</tt>
 * that can be caught, thus allowing execution to continue.
 * 
 * @see Shade
 * @see Stats
 */
public class BenchmarkSecurityManager extends SecurityManager {
	boolean allowExit = false;

	/**
	 * A <tt>SecurityException</tt> is thrown if we do not allow the virtual
	 * machine to exit.
	 */
	public void checkExit(final int status) {
		if (!allowExit) {
			System.err.println("exit " + status);
			throw new SecurityException("Tried to exit (status=" + status + ")");
		}
	}

	public void checkCreateClassLoader() {
	}

	public void checkAccess(final Thread t) {
	}

	public void checkAccess(final ThreadGroup g) {
	}

	public void checkExec(final String cmd) {
	}

	public void checkLink(final String lib) {
	}

	public void checkRead(final FileDescriptor fd) {
	}

	public void checkRead(final String file) {
	}

	public void checkRead(final String file, final Object context) {
	}

	public void checkWrite(final FileDescriptor fd) {
	}

	public void checkWrite(final String file) {
	}

	public void checkDelete(final String file) {
	}

	public void checkConnect(final String host, final int port) {
	}

	public void checkConnect(final String host, final int port,
			final Object context) {
	}

	public void checkListen(final int port) {
	}

	public void checkAccept(final String host, final int port) {
	}

	public void checkPropertiesAccess() {
	}

	public void checkPropertyAccess(final String key) {
	}

	public void checkPropertyAccess(final String key, final String val) {
	}

	public boolean checkTopLevelWindow(final Object window) {
		return true;
	}

	public void checkPackageAccess(final String pkg) {
	}

	public void checkPackageDefinition(final String pkg) {
	}

	public void checkSetFactory() {
	}
}
