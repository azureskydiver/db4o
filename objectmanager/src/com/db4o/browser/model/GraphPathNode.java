/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import com.db4o.browser.model.nodes.IModelNode;

/**
 * GraphPathNode.  Maintains one step in the path to the current tree node.
 *
 * @author djo
 */
public class GraphPathNode {
	/**
	 * @param children
	 * @param selectedChild
	 */
	public GraphPathNode(IModelNode[] children, int selectedChild) {
		this.children = children;
		this.selectedChild = selectedChild;
	}

    public final IModelNode[] children;
    public final int selectedChild;
}
