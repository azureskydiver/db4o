/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import com.db4o.browser.gui.controllers.tree.*;
import com.db4o.browser.gui.views.*;
import com.db4o.browser.model.*;

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
	private PathLabelController pathController;

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
		pathController = new PathLabelController(ui.getPathLabel());
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
		pathController.setInput(input, selection);
	}

	/**
	 * Returns the SelectionChangedController for this window.
	 * 
	 * @return SelectionChangedController the current SelectionChangedController
	 */
	public SelectionChangedController getSelectionChangedController() {
		return selectionChangedController;
	}

	public void addToClasspath(File file) {
		try {
			Class urlclclass=Class.forName("java.net.URLClassLoader");
			ClassLoader loader=getClass().getClassLoader();
			while(loader!=null) {
				if(urlclclass.isAssignableFrom(loader.getClass())) {
					Method addmethod=urlclclass.getDeclaredMethod("addURL",new Class[]{URL.class});
					addmethod.setAccessible(true);
					addmethod.invoke(loader,new Object[]{file.toURL()});
				}
				loader=loader.getParent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
