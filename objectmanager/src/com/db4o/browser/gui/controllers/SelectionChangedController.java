/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.db4o.browser.gui.controllers.tree.TreeSelectionChangedController;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
import com.db4o.browser.model.IGraphIteratorSelectionListener;

/**
 * SelectionChangedController.  This controller is responsible for notifying anyone who needs to know
 * about a window-global selection changed event.
 *
 * @author djo
 */
public class SelectionChangedController implements IGraphIteratorSelectionListener {
	private TreeViewer treeViewer;
	private TreeSelectionChangedController treeSelectionChangedController;
	private DetailController detailController;


	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator.IListener#selectionChanged()
	 */
	public void selectionChanged() {
		IGraphIterator model = (IGraphIterator) treeViewer.getInput();
		GraphPosition selectedElement = model.getPath();

		// Right now the only way to change the selection is through the 
		// tree, but this avoids loops when we provide more than one way to
		// change the selection.
		if (!treeSelectionChangedController.isTreeSelectionChanging())
			treeViewer.setSelection(new StructuredSelection(selectedElement), true);
		
		detailController.setInput(model, selectedElement);
	}

	/**
	 * Sets the object that will know about if the tree initiated this selection
	 * change.
	 *  
	 * @param treeSelectionChangedController
	 */
	public void setTreeSelectionChangedController(TreeSelectionChangedController treeSelectionChangedController) {
		this.treeSelectionChangedController = treeSelectionChangedController;
	}

	/**
	 * Sets the TreeViewer controller
	 * 
	 * @param treeViewer The treeViewer to set.
	 */
	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	
	/**
	 * Tells this controller what controller is responsible for the 
	 * detail pane.
	 * 
	 * @param detailController The detailController to set.
	 */
	public void setDetailController(DetailController detailController) {
		this.detailController = detailController;
	}
	

}
