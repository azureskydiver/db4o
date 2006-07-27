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
 * InstanceOfExpr represnts the <i>instanceof</i> opcode which determine if an
 * object is of a given type.
 */
public class InstanceOfExpr extends CondExpr {
	// instanceof

	Expr expr; // Expression (object) whose type we verify

	Type checkType; // Type to verify against

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            Expression (object) whose type is to be verified.
	 * @param checkType
	 *            Type to verify against (That is, is expr of type checkType?)
	 * @param type
	 *            Type of this expression.
	 */
	public InstanceOfExpr(final Expr expr, final Type checkType, final Type type) {
		super(type);
		this.expr = expr;
		this.checkType = checkType;

		expr.setParent(this);
	}

	public Expr expr() {
		return expr;
	}

	public Type checkType() {
		return checkType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitInstanceOfExpr(this);
	}

	public int exprHashCode() {
		return 12 + expr.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof InstanceOfExpr)
				&& ((InstanceOfExpr) other).checkType.equals(checkType)
				&& ((InstanceOfExpr) other).expr.equalsExpr(expr);
	}

	public Object clone() {
		return copyInto(new InstanceOfExpr((Expr) expr.clone(), checkType, type));
	}
}
