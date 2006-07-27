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

/**
 * A PhiStmt is inserted into a CFG in Single Static Assignment for. It is used
 * to "merge" uses of the same variable in different basic blocks.
 * 
 * @see PhiJoinStmt
 * @see PhiCatchStmt
 */
public abstract class PhiStmt extends Stmt implements Assign {
	VarExpr target; // The variable into which the Phi statement assigns

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            A stack expression or local variable that is the target of
	 *            this phi-statement.
	 */
	public PhiStmt(final VarExpr target) {
		this.target = target;
		target.setParent(this);
	}

	public VarExpr target() {
		return target;
	}

	/**
	 * Return the expressions (variables) defined by this PhiStmt. In this case,
	 * only the target is defined.
	 */
	public DefExpr[] defs() {
		return new DefExpr[] { target };
	}

	public abstract Collection operands();

	public Object clone() {
		throw new RuntimeException();
	}
}
