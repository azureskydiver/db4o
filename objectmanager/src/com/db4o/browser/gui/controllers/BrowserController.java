/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.jface.viewers.ISelectionChangedListener;

import com.db4o.browser.gui.controllers.detail.DetailController;
import com.db4o.browser.gui.controllers.tree.TreeController;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.IGraphIterator;

/**
 * BrowserController.  The root MVC Controller for a browser window.
 *
 * @author djo
 */
public class BrowserController implements IBrowserController {
    
    private DbBrowserPane ui;
    private String currentFile = null;
	private SelectionService selectionService = new SelectionService();
	private TreeController treeController;
	private DetailController detailController;
	private SelectionChangedController selectionChangedController;

	/**
     * Constructor BrowserController.  Create a BrowserController for a
     * particular user interface.
     * 
	 * @param ui The DbBrowserPane to use as for the user interface
	 */
	public BrowserController(DbBrowserPane ui) {
        this.ui = ui;
		
		// Manage the selection
		selectionChangedController = new SelectionChangedController();
		selectionService.addSelectionChangedListener(selectionChangedController);

		// Initialize the ObjectTree's controllers
		treeController = new TreeController(this, ui.getObjectTree());
		detailController = new DetailController(this, ui);
	}

	/**
	 * Return the window's selection service.
	 * 
	 * @return The window's selection service
	 */
	public SelectionService getSelectionService() {
		return selectionService;
	}

	/**
     * Method open.  Open a database file.
     * 
	 * @param file The platform-specific path/file name.
	 */
	public void open(String file) {
        currentFile = file;
		IGraphIterator i = BrowserCore.getDefault().iterator(file);
		setInput(i);
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#open(com.db4o.browser.model.IGraphIterator)
	 */
	public void setInput(IGraphIterator input) {
		// Set the tree's input
		treeController.setInput(input);
		detailController.setInput(input);
	}

	public ISelectionChangedListener getSelectionChangedController() {
		return selectionChangedController;
	}

}
