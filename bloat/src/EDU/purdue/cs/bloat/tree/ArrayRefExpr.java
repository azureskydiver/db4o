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
 * ArrayRefExpr represents an expression that references an element in an array.
 */
public class ArrayRefExpr extends MemRefExpr {
	Expr array;

	Expr index;

	Type elementType;

	/**
	 * Constructor.
	 * 
	 * @param array
	 *            The array whose element we are indexing.
	 * @param index
	 *            The index into the array.
	 * @param elementType
	 *            The type of elements in array.
	 * @param type
	 *            The type of this expression.
	 */
	public ArrayRefExpr(final Expr array, final Expr index,
			final Type elementType, final Type type) {
		super(type);
		this.array = array;
		this.index = index;
		this.elementType = elementType;

		array.setParent(this);
		index.setParent(this);
	}

	public Expr array() {
		return array;
	}

	public Expr index() {
		return index;
	}

	public Type elementType() {
		return elementType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			index.visit(visitor);
			array.visit(visitor);
		} else {
			array.visit(visitor);
			index.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitArrayRefExpr(this);
	}

	public int exprHashCode() {
		return 4 + array.exprHashCode() ^ index.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof ArrayRefExpr)
				&& ((ArrayRefExpr) other).array.equalsExpr(array)
				&& ((ArrayRefExpr) other).index.equalsExpr(index);
	}

	public Object clone() {
		return copyInto(new ArrayRefExpr((Expr) array.clone(), (Expr) index
				.clone(), elementType, type));
	}
}
