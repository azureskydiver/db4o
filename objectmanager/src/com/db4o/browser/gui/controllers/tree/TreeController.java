/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

import com.db4o.browser.gui.controllers.BrowserController;
import com.db4o.browser.gui.controllers.IBrowserController;
import com.db4o.browser.model.IGraphIterator;

/**
 * TreeController.
 *
 * @author djo
 */
public class TreeController implements IBrowserController {
	private BrowserController parent;
	private TreeViewer viewer;
	
	public TreeController(BrowserController parent, Tree tree) {
		this.parent = parent;
		this.viewer = new TreeViewer(tree);
		
        viewer.setContentProvider(new TreeContentProvider());
        viewer.setLabelProvider(new TreeLabelProvider());
		viewer.addSelectionChangedListener(parent.getSelectionService());
	}
	
	public void setInput(IGraphIterator input) {
		viewer.setInput(input);
	}
}
