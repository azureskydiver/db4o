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

import EDU.purdue.cs.bloat.cfg.*;

/**
 * SwitchStmt represents a switch statement.
 */
public class SwitchStmt extends JumpStmt {
	Expr index;

	Block defaultTarget;

	Block[] targets;

	int[] values;

	/**
	 * Constructor.
	 * 
	 * @param index
	 *            The expression on which the switch is made.
	 * @param defaultTarget
	 *            The code to be executed if index is not contained in values.
	 * @param targets
	 *            The code to be executed for each value in values.
	 * @param values
	 *            The interesting values that index can have. That is, the
	 *            values of index in which a non-default target is executed.
	 */
	public SwitchStmt(final Expr index, final Block defaultTarget,
			final Block[] targets, final int[] values) {
		this.index = index;
		this.defaultTarget = defaultTarget;
		this.targets = targets;
		this.values = values;

		index.setParent(this);
	}

	public Expr index() {
		return index;
	}

	public void setDefaultTarget(final Block block) {
		this.defaultTarget = block;
	}

	public Block defaultTarget() {
		return defaultTarget;
	}

	public Block[] targets() {
		return targets;
	}

	public int[] values() {
		return values;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		index.visit(visitor);
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitSwitchStmt(this);
	}

	public Object clone() {
		final Block[] t = new Block[targets.length];
		System.arraycopy(targets, 0, t, 0, targets.length);

		final int[] v = new int[values.length];
		System.arraycopy(values, 0, v, 0, values.length);

		return copyInto(new SwitchStmt((Expr) index.clone(), defaultTarget, t,
				v));
	}
}
