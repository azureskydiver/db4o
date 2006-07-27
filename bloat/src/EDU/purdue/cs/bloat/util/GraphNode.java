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

package EDU.purdue.cs.bloat.util;

import java.util.*;

/**
 * GraphNode represents a node in a Graph. Each node has a set of predacessors
 * and successors associated with it as well as a pre-order and post-order
 * traversal index. This information is maintained by the Graph in which the
 * GraphNode resides.
 * 
 * @see Graph
 */
public abstract class GraphNode {
	protected HashSet succs;

	protected HashSet preds;

	protected int preIndex;

	protected int postIndex;

	/**
	 * Constructor.
	 */
	public GraphNode() {
		succs = new HashSet();
		preds = new HashSet();
		preIndex = -1;
		postIndex = -1;
	}

	/**
	 * @return This node's index in a pre-order traversal of the Graph that
	 *         contains it.
	 */
	int preOrderIndex() {
		return preIndex;
	}

	/**
	 * @return This node's index in a post-order traversal of the Graph that
	 *         contains it.
	 */
	int postOrderIndex() {
		return postIndex;
	}

	void setPreOrderIndex(final int index) {
		preIndex = index;
	}

	void setPostOrderIndex(final int index) {
		postIndex = index;
	}

	/**
	 * Returns the successor (or children) nodes of this GraphNode.
	 */
	protected Collection succs() {
		return succs;
	}

	/**
	 * Returns the predacessor (or parent) nodes of this GraphNode.
	 */
	protected Collection preds() {
		return preds;
	}
}
