/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package EDU.purdue.cs.bloat.cfg;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;

/**
 * Subroutine represents a subroutine (target of a <i>jsr</i> instruction) in
 * java bytecode. Subroutines are used to implement the finally part of a
 * try-catch-finally block.
 * <p>
 * Each Subroutine belongs in a control flow graph, has an entry and exit block,
 * and has a local variable that contains its return address. Additionally, it
 * maintains a list of paths from blocks in which the subroutine is called to
 * block that is executed after the subroutine returns.
 * <p>
 * Note that it is assumed that each subroutine ends with a <i>ret</i>. While
 * this is true for bytecode generated by javac, it is not required.
 * 
 * @see AddressStoreStmt
 * @see Block
 */

// Important: I assume there is a ret statement for each jsr.
// This is true for javac code, but not in general.
public class Subroutine {
	FlowGraph graph; // CFG containing this Subroutine

	Block entry; // Basic Block at beginning of code

	Block exit; // Basic Block ending code

	ArrayList paths;

	LocalVariable returnAddress; // This Subroutine's return address

	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            The CFG containing the block.
	 */
	public Subroutine(final FlowGraph graph) {
		this.graph = graph;
		this.entry = null;
		this.exit = null;
		this.paths = new ArrayList();
		this.returnAddress = null;
	}

	/**
	 * Returns the local variable containing the return address of this
	 * subroutine.
	 */
	public LocalVariable returnAddress() {
		return returnAddress;
	}

	/**
	 * Sets the address (stored in a LocalVariable) to which this subroutine
	 * will return once it is finished.
	 * 
	 * @param returnAddress
	 *            Local variable that stores the address to which the subroutine
	 *            returns when it is completed.
	 * 
	 * @see Tree#visit_astore
	 */
	public void setReturnAddress(final LocalVariable returnAddress) {
		this.returnAddress = returnAddress;
	}

	/**
	 * Returns the number of places that this subroutine is called.
	 */
	public int numPaths() {
		return paths.size();
	}

	/**
	 * Returns the paths (a Collection of two-element arrays of Blocks) that
	 * represent the Blocks that end in a call to this subroutine and the block
	 * that begin with the return address from this subroutine.
	 */
	public Collection paths() {
		return paths;
	}

	/**
	 * Returns the CFG that contains this subroutine.
	 */
	public FlowGraph graph() {
		return graph;
	}

	/**
	 * Removes all paths involving block regardless of whether it is a calling
	 * (source) block or a returning (target) block.
	 */
	public void removePathsContaining(final Block block) {
		for (int i = paths.size() - 1; i >= 0; i--) {
			final Block[] path = (Block[]) paths.get(i);
			if ((path[0] == block) || (path[1] == block)) {
				if (FlowGraph.DEBUG) {
					System.out.println("removing path " + path[0] + " -> "
							+ path[1]);
				}
				paths.remove(i);
			}
		}
	}

	/**
	 * Removes a path between a caller Block and a return Block.
	 */
	public void removePath(final Block callerBlock, final Block returnBlock) {
		for (int i = 0; i < paths.size(); i++) {
			final Block[] path = (Block[]) paths.get(i);
			if ((path[0] == callerBlock) && (path[1] == returnBlock)) {
				if (FlowGraph.DEBUG) {
					System.out.println("removing path " + path[0] + " -> "
							+ path[1]);
				}
				paths.remove(i);
				return;
			}
		}
	}

	/**
	 * Removes all caller-return paths.
	 */
	public void removeAllPaths() {
		paths = new ArrayList();
	}

	/**
	 * Adds a path from the block before a Subroutine is called to a block after
	 * the subroutine is called. If the callerBlock is already associated with a
	 * returnBlock, the old returnBlock is replaced.
	 * 
	 * @param callerBlock
	 *            The block in which the subroutine is called. This Block ends
	 *            with a <i>jsr</i> to this subroutine.
	 * @param returnBlock
	 *            The block to which the subroutine returns. This Block begins
	 *            at the return address of this subroutine.
	 */
	public void addPath(final Block callerBlock, final Block returnBlock) {
		for (int i = 0; i < paths.size(); i++) {
			final Block[] path = (Block[]) paths.get(i);
			if (path[0] == callerBlock) {
				path[1] = returnBlock;
				return;
			}
		}

		paths.add(new Block[] { callerBlock, returnBlock });
	}

	/**
	 * Returns the "return block" for a given "caller block".
	 */
	public Block pathTarget(final Block block) {
		for (int i = 0; i < paths.size(); i++) {
			final Block[] path = (Block[]) paths.get(i);
			if (path[0] == block) {
				return path[1];
			}
		}

		return null;
	}

	/**
	 * Returns the "caller block" for a given "return block".
	 */
	public Block pathSource(final Block block) {
		for (int i = 0; i < paths.size(); i++) {
			final Block[] path = (Block[]) paths.get(i);
			if (path[1] == block) {
				return path[0];
			}
		}

		return null;
	}

	/**
	 * Sets the entry Block for this Subroutine.
	 */
	public void setEntry(final Block entry) {
		this.entry = entry;
	}

	/**
	 * Sets the exit Block for this Subroutine.
	 */
	public void setExit(final Block exit) {
		this.exit = exit;
	}

	/**
	 * Returns the first Block in the subroutine.
	 */
	public Block entry() {
		return entry;
	}

	/**
	 * Returns the last Block in the subroutine.
	 */
	public Block exit() {
		return exit;
	}

	/**
	 * Prints a textual representation of this Subroutine.
	 * 
	 * @param out
	 *            The PrintStream to which to print.
	 */
	public void print(final PrintStream out) {
		out.println("    " + entry);

		final Iterator e = paths().iterator();

		while (e.hasNext()) {
			final Block[] path = (Block[]) e.next();
			out.println("    path: " + path[0] + " -> " + path[1]);
		}
	}

	public String toString() {
		return "sub " + entry;
	}
}
