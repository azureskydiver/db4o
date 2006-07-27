/**
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

package EDU.purdue.cs.bloat.editor;

/**
 * TryCatch holds the labels for the start and end of a protected block and the
 * beginning of a catch block and the type of the exception to catch.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class TryCatch {
	private Label start;

	private Label end;

	private Label handler;

	private Type type;

	/**
	 * Constructor.
	 * 
	 * @param start
	 *            The start label of the protected block.
	 * @param end
	 *            The label of the instruction after the end of the protected
	 *            block.
	 * @param handler
	 *            The start label of the exception handler.
	 * @param type
	 *            The type of exception to catch.
	 */
	public TryCatch(final Label start, final Label end, final Label handler,
			final Type type) {
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	/**
	 * Get the start label of the protected block.
	 * 
	 * @return The start label.
	 */
	public Label start() {
		return start;
	}

	/**
	 * Get the end label of the protected block.
	 * 
	 * @return The end label.
	 */
	public Label end() {
		return end;
	}

	/**
	 * Get the start label of the catch block.
	 * 
	 * @return The handler label.
	 */
	public Label handler() {
		return handler;
	}

	/**
	 * Set the start label of the catch block.
	 */
	public void setHandler(final Label handler) {
		this.handler = handler;
	}

	/**
	 * Get the type of the exception to catch.
	 * 
	 * @return The type of the exception to catch.
	 */
	public Type type() {
		return type;
	}

	public String toString() {
		return "try " + start + ".." + end + " catch (" + type + ") " + handler;
	}
}
