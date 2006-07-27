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

import EDU.purdue.cs.bloat.editor.*;

/**
 * <tt>Handler</tt> represents a try-catch block. It containes a set of
 * protected <tt>Block</tt>s (the "try" blocks), a catch <tt>Block</tt>,
 * and the <tt>Type</tt> of exception that is caught by the catch block.
 * 
 * @see Block
 * @see EDU.purdue.cs.bloat.reflect.Catch
 * @see EDU.purdue.cs.bloat.editor.TryCatch
 */
public class Handler {
	Set protectedBlocks;

	Block catchBlock;

	Type type;

	/**
	 * Constructor.
	 * 
	 * @param catchBlock
	 *            The block of code that handles an exception
	 * @param type
	 *            The type of exception that is thrown
	 */
	public Handler(final Block catchBlock, final Type type) {
		this.protectedBlocks = new HashSet();
		this.catchBlock = catchBlock;
		this.type = type;
	}

	/**
	 * Returns a <tt>Collection</tt> of the "try" blocks.
	 */
	public Collection protectedBlocks() {
		return protectedBlocks;
	}

	public void setCatchBlock(final Block block) {
		catchBlock = block;
	}

	public Block catchBlock() {
		return catchBlock;
	}

	public Type catchType() {
		return type;
	}

	public String toString() {
		return "try -> catch (" + type + ") " + catchBlock;
	}
}
