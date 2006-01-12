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

import EDU.purdue.cs.bloat.util.*;
import java.util.*;

/**
 * DominatorTree finds the dominator tree of a FlowGraph.  
 * <p>
 * The algorithm used is Purdum-Moore.  It isn't as fast as Lengauer-Tarjan,
 * but it's a lot simpler.
 *
 * @see FlowGraph
 * @see Block
 */
public class DominatorTree
{
  public static boolean DEBUG = false;
  
  /**
   * Calculates what vertices dominate other verices and notify the basic 
   * Blocks as to who their dominator is.
   *
   * @param graph
   *        The cfg that is used to find the dominator tree.
   * @param reverse
   *        Do we go in revsers?  That is, are we computing the 
   *        dominatance (false) or postdominance (true) tree.
   * @see Block
   */  
  public static void buildTree(FlowGraph graph, boolean reverse)
  {
    int size = graph.size();          // The number of vertices in the cfg
    
    Map snkPreds = new HashMap();     // The predacessor vertices from the sink
    

    // Determine the predacessors of the cfg's sink node
    insertEdgesToSink(graph, snkPreds, reverse);

    // Get the index of the root
    int root = reverse
      ? graph.preOrderIndex(graph.sink())
      : graph.preOrderIndex(graph.source());

    Assert.isTrue(0 <= root && root < size);

    // Bit matrix indicating the dominators of each vertex.
    // If bit j of dom[i] is set, then node j dominates node i.
    BitSet[] dom = new BitSet[size];

    // A bit vector of all 1's
    BitSet ALL = new BitSet(size);          

    for (int i = 0; i < size; i++) {
      ALL.set(i);
    }

    // Initially, all the bits in the dominance matrix are set, except
    // for the root node.  The root node is initialized to have itself
    // as an immediate dominator.
    //
    for (int i = 0; i < size; i++) {
      BitSet blockDoms = new BitSet(size);
      dom[i] = blockDoms;

      if (i != root) {
	blockDoms.or(ALL);
      }
      else {
	blockDoms.set(root);
      }
    }

    // Did the dominator bit vector array change?
    boolean changed = true;

    while (changed) {
      changed = false;

      // Get the basic blocks contained in the cfg
      Iterator blocks = reverse
	? graph.postOrder().iterator()
	: graph.preOrder().iterator();

      // Compute the dominators of each node in the cfg.  We iterate 
      // over every node in the cfg.  The dominators of a node, x, are
      // found by taking the intersection of the dominator bit vectors
      // of each predacessor of x and unioning that with x.  This 
      // process is repeated until no changes are made to any dominator
      // bit vector.
      
      while (blocks.hasNext()) {
	Block block = (Block) blocks.next();

	int i = graph.preOrderIndex(block);

	Assert.isTrue(0 <= i && i < size, "Unreachable block " +
		      block);

	// We already know the dominators of the root, keep looking
	if (i == root) {
	  continue;
	}

	BitSet oldSet = dom[i];
	BitSet blockDoms = new BitSet(size);
	blockDoms.or(oldSet);

	// print(graph, reverse, "old set", i, blockDoms);

	// blockDoms := intersection of dom(pred) for all pred(block).
	Collection preds = reverse
	  ? graph.succs(block)
	  : graph.preds(block);

	Iterator e = preds.iterator();

	// Find the intersection of the dominators of block's 
	// predacessors.
	while (e.hasNext()) {
	  Block pred = (Block) e.next();

	  int j = graph.preOrderIndex(pred);
	  Assert.isTrue(j >= 0, "Unreachable block " + pred);

	  blockDoms.and(dom[j]);
	}

	// Don't forget to account for the sink node if block is a
	// leaf node.  Appearantly, there are not edges between 
	// leaf nodes and the sink node!
	preds = (Collection) snkPreds.get(block);

	if (preds != null) {
	  e = preds.iterator();

	  while (e.hasNext()) {
	    Block pred = (Block) e.next();

	    int j = graph.preOrderIndex(pred);
	    Assert.isTrue(j >= 0, "Unreachable block " + pred);

	    blockDoms.and(dom[j]);
	  }
	}

	// Include yourself in your dominators?!
	blockDoms.set(i);

	// print(graph, reverse, "intersecting " + preds, i, blockDoms);

	// If the set changed, set the changed bit.
	if (! blockDoms.equals(oldSet)) {
	  changed = true;
	  dom[i] = blockDoms;
	}
      }
    }

    // Once we have the predacessor bit vectors all squared away, we can
    // determine which vertices dominate which vertices.

    Iterator blocks = graph.nodes().iterator();

    // Initialize each block's (post)dominator parent and children
    while (blocks.hasNext()) {
      Block block = (Block) blocks.next();
      if (! reverse) {
	block.setDomParent(null);
	block.domChildren().clear();
      }
      else {
	block.setPdomParent(null);
	block.pdomChildren().clear();
      }
    }

    blocks = graph.nodes().iterator();

    // A block's immediate dominator is its closest dominator.  So, we
    // start with the dominators, dom(b), of a block, b.  To find the 
    // imediate dominator of b, we remove all blocks from dom(b) that
    // dominate any block in dom(b).

    while (blocks.hasNext()) {
      Block block = (Block) blocks.next();

      int i = graph.preOrderIndex(block);

      Assert.isTrue(0 <= i && i < size, "Unreachable block " + block);

      if (i == root) {
	if (! reverse) {
	  block.setDomParent(null);
	}
	else {
	  block.setPdomParent(null);
	}

      } else {
	// Find the immediate dominator
	// idom := dom(block) - dom(dom(block)) - block
	BitSet blockDoms = dom[i];

	// print(graph, reverse, "dom set", i, blockDoms);

	BitSet idom = new BitSet(size);
	idom.or(blockDoms);
	idom.clear(i);

	for (int j = 0; j < size; j++) {
	  if (i != j && blockDoms.get(j)) {
	    BitSet domDomBlocks = dom[j];

	    // idom = idom - (domDomBlocks - {j})
	    BitSet b = new BitSet(size);
	    b.or(domDomBlocks);
	    b.xor(ALL);
	    b.set(j);
	    idom.and(b);

	    // print(graph, reverse,
	    //    "removing dom(" + graph.preOrder().get(j) +")",
	    //    i, idom);
	  }
	}

	Block parent = null;

	// A block should only have one immediate dominator.
	for (int j = 0; j < size; j++) {
	  if (idom.get(j)) {
	    Block p = (Block) graph.preOrder().get(j);

	    Assert.isTrue(parent == null,
			  block + " has more than one immediate dominator: " +
			  parent + " and " + p);

	    parent = p;
	  }
	}

	Assert.isTrue(parent != null,
		      block + " has 0 immediate " +
		      (reverse ? "postdominators" : "dominators"));

	if (! reverse) {
	  if (DEBUG) {
	    System.out.println(parent + " dominates " + block);
	  }

	  block.setDomParent(parent);

	} else {
	  if (DEBUG) {
	    System.out.println(parent + " postdominates " + block);
	  }

	  block.setPdomParent(parent);
	}
      }
    }
  }

  /**
   * Determines which nodes are predacessors of a cfg's sink node.
   * Creates a Map that maps the sink node to its predacessors (or 
   * the leaf nodes to the sink node, their predacessor, if we're going
   * backwards).
   *
   * @param graph
   *        The cfg to operate on.
   * @param preds
   *        A mapping from leaf nodes to their predacessors.  The exact
   *        semantics depend on whether or not we are going forwards.
   * @param reverse
   *        Are we computing the dominators or postdominators?
   */
  private static void insertEdgesToSink(FlowGraph graph,
					Map preds, boolean reverse)
  {
    BitSet visited = new BitSet();      // see insertEdgesToSinkDFS
    BitSet returned = new BitSet();
 
    visited.set(graph.preOrderIndex(graph.source()));

    insertEdgesToSinkDFS(graph, graph.source(),
			 visited, returned, preds, reverse);
  }

  /**
   * This method determines which nodes are the predacessor of the sink
   * node of a cfg.  A depth-first traversal of the cfg is performed.
   * When a leaf node (that is not the sink node) is encountered, add an
   * entry to the preds Map.
   *
   * @param graph
   *        The cfg being operated on.
   * @param block
   *        The basic Block to start at.
   * @param visited
   *        Vertices that were visited
   * @param returned
   *        Vertices that returned
   * @param preds
   *        Maps a node to a HashSet representing its predacessors.  In
   *        the case that we're determining the dominace tree, preds 
   *        maps the sink node to its predacessors.  In the case that
   *        we're determining the postdominance tree, preds maps the
   *        sink node's predacessors to the sink node.
   * @param reverse
   *        Do we go in reverse?
   */
  private static void insertEdgesToSinkDFS(FlowGraph graph, Block block,
					   BitSet visited, BitSet returned, 
					   Map preds, boolean reverse)
  {
    boolean leaf = true;      // Is a vertex a leaf node?

    // Get the successors of block
    Iterator e = graph.succs(block).iterator();

    while (e.hasNext()) {
      Block succ = (Block) e.next();

      // Determine index of succ vertex in a pre-order traversal
      int index = graph.preOrderIndex(succ); 
      Assert.isTrue(index >= 0, "Unreachable block " + succ);

      if (! visited.get(index)) {
	// If the successor block hasn't been visited, visit it
	visited.set(index);
	insertEdgesToSinkDFS(graph, succ, visited, returned,
			     preds, reverse);
	returned.set(index);
	leaf = false;

      } else if (returned.get(index)) {
	// Already visited and returned, so a descendent of succ 
	// has an edge to the sink.
	leaf = false;
      }
    }

    if (leaf && block != graph.sink()) {
      // If we're dealing with a leaf node that is not the sink, set
      // up its predacessor set.

      if (! reverse) {
	// If we're going forwards (computing dominators), get the
	// predacessor vertices from the sink
	Set p = (Set) preds.get(graph.sink());

	// If there are no (known) predacessors, make a new HashSet to
	// store them and register it in the pred Map.
	if (p == null) {
	  p = new HashSet();
	  preds.put(graph.sink(), p);
	}

	// The block is in the predacessors of the sink
	p.add(block);

      } else {
	// If we're going backwards, get the block's predacessors
	Set p = (Set) preds.get(block);

	if (p == null) {
	  p = new HashSet();
	  preds.put(block, p);
	}

	// Add the sink vertex to the predacessors of the block
	p.add(graph.sink());
      }
    }
  }

  private static void print(FlowGraph graph, boolean rev,
			    String msg, int j, BitSet set)
  {
    if (rev) {
      System.out.print("reverse ");
    }
    else {
      System.out.print("forward ");
    }

    System.out.print(msg + " for " + graph.preOrder().get(j) + " =");

    for (int i = 0; i < graph.size(); i++) {
      if (set.get(i)) {
	System.out.print(" " + graph.preOrder().get(i));
      }
    }

    System.out.println();
  }
}
