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
 * ConstantExpr represents a constant expression. It is used when opcodes <i>ldc</i>,
 * <i>iinc</i>, and <i>getstatic</i> are visited. It value must be an Integer,
 * Long, Float, Double, or String.
 */
public class ConstantExpr extends Expr implements LeafExpr {
	// ldc

	Object value; // The operand to the ldc instruction

	/**
	 * Constructor.
	 * 
	 * @param value
	 *            The operand of the ldc instruction
	 * @param type
	 *            The Type of the operand
	 */
	public ConstantExpr(final Object value, final Type type) {
		super(type);
		this.value = value;
	}

	/**
	 * @return The operand of the ldc instruction
	 */
	public Object value() {
		return value;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitConstantExpr(this);
	}

	/**
	 * @return A hash code for this expression.
	 */
	public int exprHashCode() {
		if (value != null) {
			return 10 + value.hashCode();
		}

		return 10;
	}

	/**
	 * Compare this ConstantExpr to another Expr.
	 * 
	 * @param other
	 *            An Expr to compare this to.
	 * 
	 * @return True, if this and other are the same (that is, have the same
	 *         contents).
	 */
	public boolean equalsExpr(final Expr other) {
		if (!(other instanceof ConstantExpr)) {
			return false;
		}

		if (value == null) {
			return ((ConstantExpr) other).value == null;
		}

		if (((ConstantExpr) other).value == null) {
			return false;
		}

		return ((ConstantExpr) other).value.equals(value);
	}

	public Object clone() {
		return copyInto(new ConstantExpr(value, type));
	}
}
