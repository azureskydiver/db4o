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
 * <tt>CallExpr</tt> is a superclass of expressions that represent the
 * invocation of a method. It consists of an array of <tt>Expr</tt> that
 * represent the arguments to a method and a <tt>MemberRef</tt> that
 * represents the method itself.
 * 
 * @see CallMethodExpr
 * @see CallStaticExpr
 */
public abstract class CallExpr extends Expr {
	Expr[] params; // The parameters to the method

	MemberRef method; // The method to be invoked

	public int voltaPos; // used for placing swaps and stuff

	/**
	 * Constructor.
	 * 
	 * @param params
	 *            Parameters to the method. Note that these parameters do not
	 *            contain parameter 0, the "this" pointer.
	 * @param method
	 *            The method that is to be invoked.
	 * @param type
	 *            The type of this expression (i.e. the return type of the
	 *            method being called).
	 */
	public CallExpr(final Expr[] params, final MemberRef method, final Type type) {
		super(type);
		this.params = params;
		this.method = method;

		for (int i = 0; i < params.length; i++) {
			params[i].setParent(this);
		}
	}

	public MemberRef method() {
		return method;
	}

	public Expr[] params() {
		return params;
	}
}
