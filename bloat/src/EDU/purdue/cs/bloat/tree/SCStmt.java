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
 * SCStmt represents a swizzle check on an element in an array.
 */
public class SCStmt extends Stmt {

	Expr array;

	Expr index;

	boolean redundant;

	/**
	 * Constructor.
	 * 
	 * @param a
	 *            The array on which to place the swizzle check.
	 * @param i
	 *            The element in array a to swizzle.
	 */
	public SCStmt(final Expr a, final Expr i) {
		this.array = a;
		this.index = i;
		this.redundant = false;
		array.setParent(this);
		index.setParent(this);
	}

	public Expr array() {
		return array;
	}

	public Expr index() {
		return index;
	}

	/**
	 * @return True, if the swizzle check is redundent.
	 */
	public boolean redundant() {
		return redundant;
	}

	public void set_redundant(final boolean val) {
		this.redundant = val;
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitSCStmt(this);
	}

	public Object clone() {
		return copyInto(new SCStmt((Expr) array.clone(), (Expr) index.clone()));
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			index.visit(visitor);
			array.visit(visitor);
		} else {
			array.visit(visitor);
			index.visit(visitor);
		}
	}

}
