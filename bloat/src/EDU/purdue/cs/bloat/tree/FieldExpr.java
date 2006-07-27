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
 * FieldExpr represents the <i>getfield</i> opcode which fetches a field from
 * an object.
 * 
 * @see MemberRef
 */
public class FieldExpr extends MemRefExpr {
	// getfield

	Expr object; // The object whose field we are fetching

	MemberRef field; // The field to fetch

	/**
	 * Constructor.
	 * 
	 * @param object The object (result of an Expr) whose field is to be
	 *        fetched.
	 * @param field
	 *            The field of object to be fetched.
	 * @param type
	 *            The type of this expression.
	 */
	public FieldExpr(final Expr object, final MemberRef field, final Type type) {
		super(type);
		this.object = object;
		this.field = field;

		object.setParent(this);
	}

	public Expr object() {
		return object;
	}

	public MemberRef field() {
		return field;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			object.visit(visitor);
		} else {
			object.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitFieldExpr(this);
	}

	public int exprHashCode() {
		return 11 + object.exprHashCode() ^ type.simple().hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof FieldExpr)
				&& ((FieldExpr) other).field.equals(field)
				&& ((FieldExpr) other).object.equalsExpr(object);
	}

	public Object clone() {
		return copyInto(new FieldExpr((Expr) object.clone(), field, type));
	}
}
