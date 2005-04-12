/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import com.db4o.browser.query.controllers.QueryTabController;
import com.db4o.browser.query.view.QueryBrowserPane;
import com.db4o.reflect.ReflectClass;

/**
 * QueryController. Opens a new query tab and supplies information about the
 * opened database to the query browser in that tab.
 *
 * @author djo
 */
public class QueryController {

    private CTabFolder folder;
    private BrowserController browserController;

    public QueryController(CTabFolder folder) {
        this.folder = folder;
    }
    
    public void setBrowserController(BrowserController browserController) {
        this.browserController = browserController;
    }
    
    public void open(ReflectClass clazz) {
        QueryBrowserPane ui = new QueryBrowserPane(folder, SWT.NULL);
        CTabItem queryTab = new CTabItem(folder, SWT.CLOSE);
        queryTab.setControl(ui);
        queryTab.setText(clazz.getName());
        folder.setSelection(queryTab);
        
        QueryTabController controller = new QueryTabController(this, folder, ui, clazz);
        controller.setInput(clazz);
    }

    /**
     * @return Returns the browserController.
     */
    public BrowserController getBrowserController() {
        return browserController;
    }
    

}
