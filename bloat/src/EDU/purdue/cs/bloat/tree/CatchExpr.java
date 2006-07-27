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

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;

/**
 * CatchExpr represents an expression that catches an exception. A CatchExpr is
 * used when evaluating a method's try-catch blocks when a control flow graph is
 * constructed.
 * 
 * @see TryCatch
 * @see FlowGraph#FlowGraph(MethodEditor)
 * @see MethodEditor
 */
public class CatchExpr extends Expr {
	Type catchType;

	/**
	 * Constructor.
	 * 
	 * @param catchType
	 *            The type of the exception that is being caught.
	 * @param type
	 *            The type of this expression.
	 */
	public CatchExpr(final Type catchType, final Type type) {
		super(type);
		this.catchType = catchType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitCatchExpr(this);
	}

	public Type catchType() {
		return catchType;
	}

	public int exprHashCode() {
		return 8 + type.simple().hashCode() ^ catchType.hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		if (other instanceof CatchExpr) {
			final CatchExpr c = (CatchExpr) other;

			if (catchType != null) {
				return catchType.equals(c.catchType);
			} else {
				return c.catchType == null;
			}
		}

		return false;
	}

	public Object clone() {
		return copyInto(new CatchExpr(catchType, type));
	}
}
