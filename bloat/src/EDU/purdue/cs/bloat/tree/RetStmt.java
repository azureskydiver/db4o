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
import EDU.purdue.cs.bloat.editor.*;

/**
 * RetStmt represents the <i>ret</i> opcode which returns from a subroutine.
 * Recall that when a subroutine returns, the <i>ret</i> opcode's argument
 * specifies a local variable that stores the return address of
 */
public class RetStmt extends JumpStmt {
	// ret

	Subroutine sub; // Subroutine from which to return.

	/**
	 * Constructor.
	 * 
	 * @param sub
	 *            The subroutine in which the return statement resides. That is,
	 *            from where the program control is returning.
	 * 
	 * @see Tree#addInstruction(Instruction, Subroutine)
	 */
	public RetStmt(final Subroutine sub) {
		this.sub = sub;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitRetStmt(this);
	}

	public Subroutine sub() {
		return sub;
	}

	public Object clone() {
		return copyInto(new RetStmt(sub));
	}
}
