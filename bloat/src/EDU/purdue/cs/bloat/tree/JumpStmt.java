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

package EDU.purdue.cs.bloat.tree;

import java.util.*;

/**
 * <tt>JumpStmt</tt> is the super class for several classes that represent
 * statements that chang the flow of control in a program.
 * 
 * @see GotoStmt
 * @see IfStmt
 * @see JsrStmt
 */
public abstract class JumpStmt extends Stmt {
	Set catchTargets;

	public JumpStmt() {
		catchTargets = new HashSet();
	}

	/**
	 * The <tt>Block</tt> containing this <tt>JumpStmt</tt> may lie within a
	 * try block (i.e. it is a <i>protected</i> block). If so, then
	 * <tt>catchTargets()</tt> returns a set of <tt>Block</tt>s that begin
	 * the exception handler for the exception that may be thrown in the
	 * protected block.
	 */
	public Collection catchTargets() {
		return catchTargets;
	}

	protected Node copyInto(final Node node) {
		((JumpStmt) node).catchTargets.addAll(catchTargets);
		return super.copyInto(node);
	}
}
