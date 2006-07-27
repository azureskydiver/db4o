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
 * CastExpr represents an expression that casts an object to a given type.
 */
public class CastExpr extends Expr {
	Expr expr; // An expression (object) to cast

	Type castType; // The Type to cast expr to

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            Expression (object) to be cast.
	 * @param type
	 *            The type to which to cast expr and as well as the type of this
	 *            expression.
	 */
	public CastExpr(final Expr expr, final Type type) {
		this(expr, type, type);
	}

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            Expression (object) to be cast.
	 * @param castType
	 *            The type to which to cast expr.
	 * @param type
	 *            The type of this expression.
	 */
	public CastExpr(final Expr expr, final Type castType, final Type type) {
		super(type);
		this.expr = expr;
		this.castType = castType;

		expr.setParent(this);
	}

	public Expr expr() {
		return expr;
	}

	public Type castType() {
		return castType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitCastExpr(this);
	}

	public int exprHashCode() {
		return 7 + expr.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof CastExpr)
				&& ((CastExpr) other).castType.equals(castType)
				&& ((CastExpr) other).expr.equalsExpr(expr);
	}

	public Object clone() {
		return copyInto(new CastExpr((Expr) expr.clone(), castType, type));
	}
}
