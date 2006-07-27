/**
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

/**
 * SRStmt represents the swizzle of a range of elements in an array.
 */
public class SRStmt extends Stmt {
	Expr array;

	Expr start; // starting value of range

	Expr end; // terminating value of range

	/**
	 * Constructor.
	 * 
	 * @param a
	 *            The array to swizzle.
	 * @param s
	 *            The starting value of the swizzle range.
	 * @param t
	 *            The terminating value of the swizzle range.
	 */
	public SRStmt(final Expr a, final Expr s, final Expr t) {
		this.array = a;
		this.start = s;
		this.end = t;
		array.setParent(this);
		start.setParent(this);
		end.setParent(this);
	}

	public Expr array() {
		return array;
	}

	public Expr start() {
		return start;
	}

	public Expr end() {
		return end;
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitSRStmt(this);
	}

	public Object clone() {
		return copyInto(new SRStmt((Expr) array.clone(), (Expr) start.clone(),
				(Expr) end.clone()));
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			end.visit(visitor);
			start.visit(visitor);
			array.visit(visitor);
		} else {
			array.visit(visitor);
			start.visit(visitor);
			end.visit(visitor);
		}
	}
}
