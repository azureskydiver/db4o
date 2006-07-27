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
 * UPExpr represents an update check opcode which checks the persistent store to
 * determine if a variable needs to be updated.
 */
public class UCExpr extends CheckExpr {
	public static final int POINTER = 1;

	public static final int SCALAR = 2;

	int kind;

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            The expression to check to see if it needs to be updated.
	 * @param kind
	 *            The kind of expression (POINTER or SCALAR) to be checked.
	 * @param type
	 *            The type of this expression.
	 */
	public UCExpr(final Expr expr, final int kind, final Type type) {
		super(expr, type);
		this.kind = kind;
	}

	public int kind() {
		return kind;
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitUCExpr(this);
	}

	public boolean equalsExpr(final Expr other) {
		return (other instanceof UCExpr) && super.equalsExpr(other)
				&& (((UCExpr) other).kind == kind);
	}

	public Object clone() {
		return copyInto(new UCExpr((Expr) expr.clone(), kind, type));
	}
}
