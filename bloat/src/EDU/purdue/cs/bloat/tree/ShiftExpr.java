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
 * <tt>ShiftExpr</tt> represents a bit shift operation.
 */
public class ShiftExpr extends Expr {
	int dir;

	Expr expr;

	Expr bits;

	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	public static final int UNSIGNED_RIGHT = 2;

	/**
	 * Constructor.
	 * 
	 * @param dir
	 *            The direction (LEFT, RIGHT, or UNSIGNED_RIGHT) in which to
	 *            shift.
	 * @param expr
	 *            The expression to shift.
	 * @param bits
	 *            The number of bits to shift.
	 * @param type
	 *            The type of this expression.
	 */
	public ShiftExpr(final int dir, final Expr expr, final Expr bits,
			final Type type) {
		super(type);
		this.dir = dir;
		this.expr = expr;
		this.bits = bits;

		expr.setParent(this);
		bits.setParent(this);
	}

	public int dir() {
		return dir;
	}

	public Expr expr() {
		return expr;
	}

	public Expr bits() {
		return bits;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			bits.visit(visitor);
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
			bits.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitShiftExpr(this);
	}

	public int exprHashCode() {
		return 19 + dir ^ expr.exprHashCode() ^ bits.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof ShiftExpr)
				&& (((ShiftExpr) other).dir == dir)
				&& ((ShiftExpr) other).expr.equalsExpr(expr)
				&& ((ShiftExpr) other).bits.equalsExpr(bits);
	}

	public Object clone() {
		return copyInto(new ShiftExpr(dir, (Expr) expr.clone(), (Expr) bits
				.clone(), type));
	}
}
