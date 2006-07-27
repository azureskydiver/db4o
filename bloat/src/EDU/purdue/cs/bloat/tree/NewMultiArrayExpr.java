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
 * NewMultiArrayExpr represents the <tt>multianewarray</tt> opcode which
 * creates a new multidimensional array.
 */
public class NewMultiArrayExpr extends Expr {
	// multianewarray

	Expr[] dimensions;

	Type elementType;

	/**
	 * Constructor.
	 * 
	 * @param dimensions
	 *            Expressions representing the size of each of the dimensions in
	 *            the array.
	 * @param elementType
	 *            The type of the elements in the array.
	 * @param type
	 *            The type of this expression.
	 */
	public NewMultiArrayExpr(final Expr[] dimensions, final Type elementType,
			final Type type) {
		super(type);
		this.elementType = elementType;
		this.dimensions = dimensions;

		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i].setParent(this);
		}
	}

	public Expr[] dimensions() {
		return dimensions;
	}

	public Type elementType() {
		return elementType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			for (int i = dimensions.length - 1; i >= 0; i--) {
				dimensions[i].visit(visitor);
			}
		} else {
			for (int i = 0; i < dimensions.length; i++) {
				dimensions[i].visit(visitor);
			}
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitNewMultiArrayExpr(this);
	}

	public int exprHashCode() {
		int v = 17;

		for (int i = 0; i < dimensions.length; i++) {
			v ^= dimensions[i].hashCode();
		}

		return v;
	}

	public boolean equalsExpr(final Expr other) {
		return false;
	}

	public Object clone() {
		final Expr[] d = new Expr[dimensions.length];

		for (int i = 0; i < dimensions.length; i++) {
			d[i] = (Expr) dimensions[i].clone();
		}

		return copyInto(new NewMultiArrayExpr(d, elementType, type));
	}
}
