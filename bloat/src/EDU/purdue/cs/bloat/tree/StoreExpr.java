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

package EDU.purdue.cs.bloat.tree;

import EDU.purdue.cs.bloat.editor.*;

/**
 * StoreExpr represents a store of an expression into a memory location.
 * 
 * @see MemExpr
 */
public class StoreExpr extends Expr implements Assign {
	MemExpr target;

	Expr expr;

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            The memory location (or local variable, etc.) into which expr
	 *            is stored.
	 * @param expr
	 *            An expression whose value is to be stored.
	 * @param type
	 *            The type of this expression.
	 */
	public StoreExpr(final MemExpr target, final Expr expr, final Type type) {
		super(type);

		this.target = target;
		this.expr = expr;

		target.setParent(this);
		expr.setParent(this);
	}

	/**
	 * Returns the <tt>MemExpr</tt> into which the expression is stored.
	 */
	public DefExpr[] defs() {
		return new DefExpr[] { target };
	}

	/**
	 * Returns the memory location (or local variable) into which the expression
	 * is stored.
	 */
	public MemExpr target() {
		return target;
	}

	/**
	 * Returns the expression being stored.
	 */
	public Expr expr() {
		return expr;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			target.visitOnly(visitor);
			expr.visit(visitor);
			target.visitChildren(visitor);
		} else {
			target.visitChildren(visitor);
			expr.visit(visitor);
			target.visitOnly(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitStoreExpr(this);
	}

	public int exprHashCode() {
		return 22 + target.exprHashCode() ^ expr.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other instanceof StoreExpr)
				&& ((StoreExpr) other).target.equalsExpr(target)
				&& ((StoreExpr) other).expr.equalsExpr(expr);
	}

	public Object clone() {
		return copyInto(new StoreExpr((MemExpr) target.clone(), (Expr) expr
				.clone(), type));
	}
}
