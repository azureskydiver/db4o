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
 * ArithExpr represents a binary arithmetic expression. It consists of two
 * operands and an operator.
 */
public class ArithExpr extends Expr {
	char operation; // Arithmetic operator

	Expr left; // Expression on left-hand side of operation

	Expr right; // Expression on right-hand side of operation

	// Operators...
	public static final char ADD = '+';

	public static final char SUB = '-';

	public static final char DIV = '/';

	public static final char MUL = '*';

	public static final char REM = '%';

	public static final char AND = '&';

	public static final char IOR = '|';

	public static final char XOR = '^';

	public static final char CMP = '?';

	public static final char CMPL = '<';

	public static final char CMPG = '>';

	/**
	 * Constructor.
	 * 
	 * @param operation
	 *            Arithmetic operation that this expression performs.
	 * @param left
	 *            Left-hand argument to operation.
	 * @param right
	 *            Right-hand argument to operation.
	 * @param type
	 *            The type of this expression.
	 */
	public ArithExpr(final char operation, final Expr left, final Expr right,
			final Type type) {
		super(type);
		this.operation = operation;
		this.left = left;
		this.right = right;

		left.setParent(this);
		right.setParent(this);
	}

	public int operation() {
		return operation;
	}

	public Expr left() {
		return left;
	}

	public Expr right() {
		return right;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			right.visit(visitor);
			left.visit(visitor);
		} else {
			left.visit(visitor);
			right.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitArithExpr(this);
	}

	public int exprHashCode() {
		return 1 + operation ^ left.exprHashCode() ^ right.exprHashCode();
	}

	/**
	 * Compare this arithmetic expression to another Expression.
	 * 
	 * @return True, if both expressions have the same contents.
	 */
	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof ArithExpr)
				&& (((ArithExpr) other).operation == operation)
				&& ((ArithExpr) other).left.equalsExpr(left)
				&& ((ArithExpr) other).right.equalsExpr(right);
	}

	public Object clone() {
		return copyInto(new ArithExpr(operation, (Expr) left.clone(),
				(Expr) right.clone(), type));
	}
}
