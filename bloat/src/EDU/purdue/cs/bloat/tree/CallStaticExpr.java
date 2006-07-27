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
 * CallStaticExpr represents the <tt>invokestatic</tt> opcode which invokes a
 * class (static) method. Static methods can always be inlined.
 * 
 * @see CallMethodExpr
 */
public class CallStaticExpr extends CallExpr {
	// invokestatic

	/**
	 * Constructor.
	 * 
	 * @param params
	 *            Parameters to the method.
	 * @param method
	 *            The (class) method to be invoked.
	 * @param type
	 *            The type of this expression.
	 */
	public CallStaticExpr(final Expr[] params, final MemberRef method,
			final Type type) {
		super(params, method, type);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			for (int i = params.length - 1; i >= 0; i--) {
				params[i].visit(visitor);
			}
		} else {
			for (int i = 0; i < params.length; i++) {
				params[i].visit(visitor);
			}
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitCallStaticExpr(this);
	}

	public int exprHashCode() {
		int v = 6;

		for (int i = 0; i < params.length; i++) {
			v ^= params[i].exprHashCode();
		}

		return v;
	}

	public boolean equalsExpr(final Expr other) {
		return false;
	}

	public Object clone() {
		final Expr[] p = new Expr[params.length];

		for (int i = 0; i < params.length; i++) {
			p[i] = (Expr) params[i].clone();
		}

		return copyInto(new CallStaticExpr(p, method, type));
	}
}
