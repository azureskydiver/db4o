/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

import com.db4o.browser.gui.controllers.BrowserController;
import com.db4o.browser.gui.controllers.IBrowserController;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;

/**
 * TreeController.
 *
 * @author djo
 */
public class TreeController implements IBrowserController {
	private BrowserController parent;
	private TreeViewer viewer;
	private SelectionChangedController selectionListener;
	
	public TreeController(BrowserController parent, Tree tree) {
		this.parent = parent;
		this.viewer = new TreeViewer(tree);
		
        viewer.setContentProvider(new TreeContentProvider());
        viewer.setLabelProvider(new TreeLabelProvider());
		final TreeSelectionChangedController treeSelectionChangedController = new TreeSelectionChangedController();
		viewer.addSelectionChangedListener(treeSelectionChangedController);

		selectionListener = parent.getSelectionChangedController();
		selectionListener.setTreeViewer(viewer);
		selectionListener.setTreeSelectionChangedController(treeSelectionChangedController);
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#setInput(com.db4o.browser.model.IGraphIterator, com.db4o.browser.model.GraphPosition)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
		IGraphIterator oldInput = (IGraphIterator) viewer.getInput();
		if (oldInput != null)
			oldInput.removeSelectionChangedListener(selectionListener);
		
		viewer.setInput(input);
		input.addSelectionChangedListener(selectionListener);
	}
}
