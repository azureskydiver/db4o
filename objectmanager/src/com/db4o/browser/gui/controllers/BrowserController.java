/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.jface.viewers.TreeViewer;

import com.db4o.browser.gui.views.DbBrowserPane;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.IGraphIterator;

/**
 * BrowserController.  The root MVC Controller for a browser window.
 *
 * @author djo
 */
public class BrowserController {
    
    private DbBrowserPane ui;

	/**
     * Constructor BrowserController.  Create a BrowserController for a
     * particular user interface.
     * 
	 * @param ui
	 */
	public BrowserController(DbBrowserPane ui) {
        this.ui = ui;
    }

	/**
	 * @param file
	 */
	public void open(String file) {
		IGraphIterator i = BrowserCore.getDefault().iterator(file);
        
        TreeViewer tree = ui.getObjectTree();
        tree.setContentProvider(new TreeContentProvider());
        tree.setLabelProvider(new TreeLabelProvider());
        tree.setInput(i);
	}
	
}
