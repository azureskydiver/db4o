/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import com.db4o.browser.gui.controllers.tree.TreeController;
import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;

/**
 * BrowserController.  The root MVC Controller for a browser window.
 *
 * @author djo
 */
public class BrowserController implements IBrowserController {
    
    private DbBrowserPane ui;
    private String currentFile = null;
	private TreeController treeController;
	private DetailController detailController;
	private SelectionChangedController selectionChangedController;
	private NavigationController navigationController;

	/**
     * Constructor BrowserController.  Create a BrowserController for a
     * particular user interface.
     * 
	 * @param ui The DbBrowserPane to use as for the user interface
	 */
	public BrowserController(DbBrowserPane ui) {
        this.ui = ui;

		// Initialize the ObjectTree's controllers
		selectionChangedController = new SelectionChangedController();
		
		treeController = new TreeController(this, ui.getObjectTree());
		detailController = new DetailController(this, ui);
		navigationController = new NavigationController(ui.getLeftButton(), ui.getRightButton());
	}

	/**
     * Method open.  Open a database file.
     * 
	 * @param file The platform-specific path/file name.
	 */
	public void open(String file) {
        currentFile = file;
		IGraphIterator i = BrowserCore.getDefault().iterator(file);
		setInput(i, null);
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#open(com.db4o.browser.model.IGraphIterator)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
		// Set the various sub-controllers' inputs
		treeController.setInput(input, selection);
		detailController.setInput(input, selection);
		navigationController.setInput(input, selection);
	}

	/**
	 * Returns the SelectionChangedController for this window.
	 * 
	 * @return SelectionChangedController the current SelectionChangedController
	 */
	public SelectionChangedController getSelectionChangedController() {
		return selectionChangedController;
	}

}
