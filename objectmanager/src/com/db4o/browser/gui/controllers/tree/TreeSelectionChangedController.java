/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.tree;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;

/**
 * TreeSelectionChangedController.  When the tree's selection changes, updates
 * the model's selection.
 *
 * @author djo
 */
public class TreeSelectionChangedController implements
		ISelectionChangedListener {

	private int treeSelectionChanging=0;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		try {
			++treeSelectionChanging;
			
			TreeViewer source = (TreeViewer) event.getSource();
			IGraphIterator model = (IGraphIterator) source.getInput();
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			GraphPosition node = (GraphPosition) selection.getFirstElement();
			model.setSelectedPath(node);
		} finally {
			--treeSelectionChanging;
		}
	}
	
	/**
	 * @return
	 */
	public boolean isTreeSelectionChanging() {
		return treeSelectionChanging > 0;
	}

}
