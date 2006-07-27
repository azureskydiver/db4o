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

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

/**
 * An expression in which a definition occurs. Each instance has a unique
 * version number associated with it.
 */
public abstract class DefExpr extends Expr {
	Set uses; // Expressions in which the definition is used

	int version; // Which number DefExpr is this?

	static int next = 0; // Total number of DefExprs

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            The Type (descriptor) of this expression
	 */
	public DefExpr(final Type type) {
		super(type);
		uses = new HashSet();
		version = DefExpr.next++;
	}

	/**
	 * Clean up this expression. Notify all the expressions that use this
	 * definition that it is no longer their defining expression.
	 */
	public void cleanupOnly() {
		super.cleanupOnly();

		final List a = new ArrayList(uses);

		uses.clear();

		final Iterator e = a.iterator();

		while (e.hasNext()) {
			final Expr use = (Expr) e.next();
			use.setDef(null);
		}
	}

	/**
	 * Returns Number DefExpr this is. This is also the SSA version number of
	 * the expression that this <tt>DefExpr</tt> defines.
	 */
	public int version() {
		return version;
	}

	/**
	 * Determines whether or not this <tt>DefExpr</tt> defines a local
	 * variable in its parent.
	 * 
	 * @see Assign#defs
	 */
	public boolean isDef() {
		if (parent instanceof Assign) {
			final DefExpr[] defs = ((Assign) parent).defs();

			if (defs != null) {
				for (int i = 0; i < defs.length; i++) {
					if (defs[i] == this) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Returns the <tt>Expr</tt>s in which the variable defined by this are
	 * used.
	 */
	public Collection uses() {
		return new HashSet(uses);
	}

	public boolean hasUse(final Expr use) {
		return uses.contains(use);
	}

	protected void addUse(final Expr use) {
		uses.add(use);
	}

	protected void removeUse(final Expr use) {
		uses.remove(use);
	}
}
