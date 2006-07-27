/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 *
 * <p>
 *
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

package EDU.purdue.cs.bloat.cfg;

import java.util.*;

import EDU.purdue.cs.bloat.tree.*;

/**
 * <tt>ReplaceTarget</tt> replaces the block that is the target of a
 * <tt>JumpStmt</tt>, <tt>JsrStmt</tt>, <tt>RetStmt</tt>,
 * <tt>GotoStmt</tt>, <tt>SwitchStmt</tt>, or <tt>IfStmt</tt> with
 * another <tt>Block</tt>.
 */
public class ReplaceTarget extends TreeVisitor {
	Block oldDst;

	Block newDst;

	public ReplaceTarget(final Block oldDst, final Block newDst) {
		this.oldDst = oldDst;
		this.newDst = newDst;
	}

	public void visitTree(final Tree tree) {
		final Stmt last = tree.lastStmt();

		if (last instanceof JumpStmt) {
			final JumpStmt stmt = (JumpStmt) last;

			if (FlowGraph.DEBUG) {
				System.out.println("  Replacing " + oldDst + " with " + newDst
						+ " in " + stmt);
			}

			if (stmt.catchTargets().remove(oldDst)) {
				stmt.catchTargets().add(newDst);
			}

			stmt.visit(this);
		}
	}

	public void visitJsrStmt(final JsrStmt stmt) {
		if (stmt.sub().entry() == oldDst) {
			if (FlowGraph.DEBUG) {
				System.out.print("  replacing " + stmt);
			}

			stmt.block().graph().setSubEntry(stmt.sub(), newDst);

			if (FlowGraph.DEBUG) {
				System.out.println("   with " + stmt);
			}
		}
	}

	public void visitRetStmt(final RetStmt stmt) {
		final Iterator paths = stmt.sub().paths().iterator();

		while (paths.hasNext()) {
			final Block[] path = (Block[]) paths.next();

			if (FlowGraph.DEBUG) {
				System.out.println("  path = " + path[0] + " " + path[1]);
			}

			if (path[1] == oldDst) {
				if (FlowGraph.DEBUG) {
					System.out.println("  replacing ret to " + oldDst
							+ " with ret to " + newDst);
				}

				path[1] = newDst;
				((JsrStmt) path[0].tree().lastStmt()).setFollow(newDst);
			}
		}
	}

	public void visitGotoStmt(final GotoStmt stmt) {
		if (stmt.target() == oldDst) {
			if (FlowGraph.DEBUG) {
				System.out.print("  replacing " + stmt);
			}

			stmt.setTarget(newDst);

			if (FlowGraph.DEBUG) {
				System.out.println("   with " + stmt);
			}
		}
	}

	public void visitSwitchStmt(final SwitchStmt stmt) {
		if (stmt.defaultTarget() == oldDst) {
			if (FlowGraph.DEBUG) {
				System.out.print("  replacing " + stmt);
			}

			stmt.setDefaultTarget(newDst);

			if (FlowGraph.DEBUG) {
				System.out.println("   with " + stmt);
			}
		}

		final Block[] targets = stmt.targets();

		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == oldDst) {
				if (FlowGraph.DEBUG) {
					System.out.print("  replacing " + stmt);
				}

				targets[i] = newDst;

				if (FlowGraph.DEBUG) {
					System.out.println("   with " + stmt);
				}
			}
		}
	}

	public void visitIfStmt(final IfStmt stmt) {
		if (stmt.trueTarget() == oldDst) {
			if (FlowGraph.DEBUG) {
				System.out.print("  replacing " + stmt);
			}

			stmt.setTrueTarget(newDst);

			if (FlowGraph.DEBUG) {
				System.out.println("   with " + stmt);
			}
		}

		if (stmt.falseTarget() == oldDst) {
			if (FlowGraph.DEBUG) {
				System.out.print("  replacing " + stmt);
			}

			stmt.setFalseTarget(newDst);

			if (FlowGraph.DEBUG) {
				System.out.println("   with " + stmt);
			}
		}
	}
}
