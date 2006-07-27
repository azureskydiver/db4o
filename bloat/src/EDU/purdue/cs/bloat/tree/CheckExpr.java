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

import EDU.purdue.cs.bloat.editor.*;

/**
 * CheckExpr is a superclass for classes representing a check on an expression.
 * For instance, a CheckExpr is inserted into the tree before the divisor of a
 * divide operation. The CheckExpr checks to make sure that the divisor is not
 * zero.
 * 
 * @see RCExpr
 * @see UCExpr
 * @see ZeroCheckExpr
 */
public abstract class CheckExpr extends Expr {
	Expr expr;

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            An expression that is to be checked.
	 * @param type
	 *            The type of this expression.
	 */
	public CheckExpr(final Expr expr, final Type type) {
		super(type);
		this.expr = expr;
		expr.setParent(this);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
		}
	}

	/**
	 * Returns the expression being checked.
	 */
	public Expr expr() {
		return expr;
	}

	public int exprHashCode() {
		return 9 + expr.exprHashCode() ^ type.simple().hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof CheckExpr)
				&& ((CheckExpr) other).expr.equalsExpr(expr);
	}
}
