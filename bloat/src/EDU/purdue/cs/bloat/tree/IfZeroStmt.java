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

import EDU.purdue.cs.bloat.cfg.*;

/**
 * IfZeroStmt evaluates an expression and executes one of its two branches
 * depending on whether or not the expression evaluated to zero.
 */
public class IfZeroStmt extends IfStmt {
	Expr expr; // Expression to evaluate

	/**
	 * Constructor.
	 * 
	 * @param comparison
	 *            Comparison operator.
	 * @param expr
	 *            An expression to be evaluated.
	 * @param trueTarget
	 *            Basic Block that is executed if the expression evaluates to
	 *            zero.
	 * @param falseTarget
	 *            Basic Block that is executed if the expression evaluates to
	 *            non-zero.
	 */
	public IfZeroStmt(final int comparison, final Expr expr,
			final Block trueTarget, final Block falseTarget) {
		super(comparison, trueTarget, falseTarget);
		this.expr = expr;
		expr.setParent(this);
	}

	/**
	 * @return The expression that is evaluated.
	 */
	public Expr expr() {
		return expr;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		expr.visit(visitor);
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitIfZeroStmt(this);
	}

	public Object clone() {
		return copyInto(new IfZeroStmt(comparison, (Expr) expr.clone(),
				trueTarget, falseTarget));
	}
}
