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
 * ZeroCheckExpr represents a check for a zero value. For instance, when a
 * division operation is performed. a ZeroCheckExpr is inserted to ensure that
 * the divisor is not zero. It is used when division is performed (<i>idiv</i>,
 * <i>ldiv</i>) a remainder is taken (<i>irem</i>, <i>lrem</i>), or a field
 * is accessed (<i>getfield</i>, <i>putfield</i).
 */
public class ZeroCheckExpr extends CheckExpr {
	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            The expression to check for a zero value.
	 * @param type
	 *            The type of this expression.
	 */
	public ZeroCheckExpr(final Expr expr, final Type type) {
		super(expr, type);
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitZeroCheckExpr(this);
	}

	public boolean equalsExpr(final Expr other) {
		return (other instanceof ZeroCheckExpr) && super.equalsExpr(other);
	}

	public Object clone() {
		return copyInto(new ZeroCheckExpr((Expr) expr.clone(), type));
	}
}
