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
 * CallMethodExpr represents the invocation of an object's method. In addition
 * to knowing what method is being called and its parameters, it also knows what
 * "kind" of method call it is (<tt>VIRTUAL</tt>, <tt>NONVIRTUAL</tt>, or
 * <tt>INTERFACE</tt>) and the object that is the reciever of this method
 * call.
 * 
 * @see CallStaticExpr
 */
public class CallMethodExpr extends CallExpr {
	// Different kinds of methods to call...
	public static final int VIRTUAL = 0; // invokevirtual

	public static final int NONVIRTUAL = 1; // invokespecial

	public static final int INTERFACE = 2; // invokeinterface

	Expr receiver;

	int kind;

	/**
	 * Constructor.
	 * 
	 * @param kind
	 *            The kind (VIRTUAL, NONVIRTUAL, or INTERFACE) of method that is
	 *            being called.
	 * @param receiver
	 *            The expression (object) whose method is being called.
	 * @param params
	 *            Parameters to the method.
	 * @param method
	 *            The method being called.
	 * @param type
	 *            The type of this expression.
	 */
	public CallMethodExpr(final int kind, final Expr receiver,
			final Expr[] params, final MemberRef method, final Type type) {
		super(params, method, type);
		this.receiver = receiver;
		this.kind = kind;

		receiver.setParent(this);
	}

	public int kind() {
		return kind;
	}

	public Expr receiver() {
		return receiver;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			for (int i = params.length - 1; i >= 0; i--) {
				params[i].visit(visitor);
			}

			receiver.visit(visitor);
		} else {
			receiver.visit(visitor);

			for (int i = 0; i < params.length; i++) {
				params[i].visit(visitor);
			}
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitCallMethodExpr(this);
	}

	public int exprHashCode() {
		int v = 5 + kind ^ receiver.exprHashCode();

		for (int i = 0; i < params.length; i++) {
			v ^= params[i].exprHashCode();
		}

		return v;
	}

	public boolean equalsExpr(final Expr other) {
		return false;
	}

	public Object clone() {
		final Expr[] p = new Expr[params.length];

		for (int i = 0; i < params.length; i++) {
			p[i] = (Expr) params[i].clone();
		}

		return copyInto(new CallMethodExpr(kind, (Expr) receiver.clone(), p,
				method, type));
	}
}
