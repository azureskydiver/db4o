/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * SelectionChangedController.  This controller is responsible for notifying anyone who needs to know
 * about a window-global selection changed event.
 *
 * @author djo
 */
public class SelectionChangedController implements ISelectionChangedListener {
	private TreeViewer treeViewer;

	/**
	 * @param treeViewer The treeViewer to set.
	 */
	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		treeViewer.setSelection(event.getSelection());
	}

}
