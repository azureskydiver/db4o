/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
    private String currentFile = null;

	/**
     * Constructor BrowserController.  Create a BrowserController for a
     * particular user interface.
     * 
	 * @param ui The DbBrowserPane to use as for the user interface
	 */
	public BrowserController(DbBrowserPane ui) {
        this.ui = ui;
		
		// Get the selection service
		SelectionService selectionService = SelectionService.getDefault();

        // Initialize the ObjectTree's controllers
        final TreeViewer tree = ui.getObjectTree();
        tree.setContentProvider(new TreeContentProvider());
        tree.setLabelProvider(new TreeLabelProvider());
		tree.addSelectionChangedListener(selectionService);
		
		// Listen to all selection changed events and broadcast them to the rest of the UI
		selectionService.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				tree.setSelection(event.getSelection());
			}
		});
    }

	/**
     * Method open.  Open a database file.
     * 
	 * @param file The platform-specific path/file name.
	 */
	public void open(String file) {
        currentFile = file;
		IGraphIterator i = BrowserCore.getDefault().iterator(file);
        
        TreeViewer tree = ui.getObjectTree();
        tree.setInput(i);
	}

}
