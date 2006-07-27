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
 * StackManipStmt represents the opcodes that manipulate the stack such as
 * <tt>swap</tt> and <tt>dup</tt>.
 */
public class StackManipStmt extends Stmt implements Assign {
	StackExpr[] target;

	StackExpr[] source;

	int kind;

	// 0 1 -> 1 0
	public static final int SWAP = 0;

	// 0 -> 0 0
	public static final int DUP = 1;

	// 0 1 -> 1 0 1
	public static final int DUP_X1 = 2;

	// 0 1 2 -> 2 0 1 2
	public static final int DUP_X2 = 3;

	// 0 1 -> 0 1 0 1
	public static final int DUP2 = 4;

	// 0 1 2 -> 1 2 0 1 2
	public static final int DUP2_X1 = 5;

	// 0 1 2 3 -> 2 3 0 1 2 3
	public static final int DUP2_X2 = 6;

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            The new contents of the stack
	 * @param source
	 *            The old contents of the stack
	 * @param kind
	 *            The kind of stack manipulation (SWAP, DUP, etc.) to take
	 *            place.
	 */
	public StackManipStmt(final StackExpr[] target, final StackExpr[] source,
			final int kind) {
		this.kind = kind;

		this.target = target;

		for (int i = 0; i < target.length; i++) {
			this.target[i].setParent(this);
		}

		this.source = source;

		for (int i = 0; i < source.length; i++) {
			this.source[i].setParent(this);
		}
	}

	public DefExpr[] defs() {
		return target;
	}

	public StackExpr[] target() {
		return target;
	}

	public StackExpr[] source() {
		return source;
	}

	public int kind() {
		return kind;
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitStackManipStmt(this);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			for (int i = target.length - 1; i >= 0; i--) {
				target[i].visit(visitor);
			}

			for (int i = source.length - 1; i >= 0; i--) {
				source[i].visit(visitor);
			}
		} else {
			for (int i = 0; i < source.length; i++) {
				source[i].visit(visitor);
			}

			for (int i = 0; i < target.length; i++) {
				target[i].visit(visitor);
			}
		}
	}

	public Object clone() {
		final StackExpr[] t = new StackExpr[target.length];

		for (int i = 0; i < target.length; i++) {
			t[i] = (StackExpr) target[i].clone();
		}

		final StackExpr[] s = new StackExpr[source.length];

		for (int i = 0; i < source.length; i++) {
			s[i] = (StackExpr) source[i].clone();
		}

		return copyInto(new StackManipStmt(t, s, kind));
	}
}
