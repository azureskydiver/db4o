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

/**
 * <tt>InitStmt</tt> groups together the initialization of local variables (<tt>LocalExpr</tt>).
 * 
 * @see LocalExpr
 * @see Tree#initLocals
 */
public class InitStmt extends Stmt implements Assign {
	LocalExpr[] targets;

	/**
	 * Constructor.
	 * 
	 * @param targets
	 *            The instances of LocalExpr that are to be initialized.
	 */
	public InitStmt(final LocalExpr[] targets) {
		this.targets = new LocalExpr[targets.length];

		for (int i = 0; i < targets.length; i++) {
			this.targets[i] = targets[i];
			this.targets[i].setParent(this);
		}
	}

	/**
	 * Returns the local variables (<tt>LocalExpr</tt>s) initialized by this
	 * <tt>InitStmt</tt>.
	 */
	public LocalExpr[] targets() {
		return targets;
	}

	/**
	 * Returns the local variables (<tt>LocalExpr</tt>s) defined by this
	 * <tt>InitStmt</tt>. These are the same local variables that are the
	 * targets of the <tt>InitStmt</tt>.
	 */
	public DefExpr[] defs() {
		return targets;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		for (int i = 0; i < targets.length; i++) {
			targets[i].visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitInitStmt(this);
	}

	public Object clone() {
		final LocalExpr[] t = new LocalExpr[targets.length];

		for (int i = 0; i < targets.length; i++) {
			t[i] = (LocalExpr) targets[i].clone();
		}

		return copyInto(new InitStmt(t));
	}
}
