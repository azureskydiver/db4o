/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import com.db4o.browser.gui.views.QueryBrowserPane;
import com.db4o.browser.model.BrowserCore;
import com.db4o.reflect.ReflectClass;

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
        openQueryTab(clazz);
    }
    
    public void open() {
        // Open class selection dialog
        System.out.println("Query.");
    }

    private void openQueryTab(ReflectClass clazz) {
        QueryBrowserPane ui = new QueryBrowserPane(folder, SWT.NULL);
        CTabItem queryTab = new CTabItem(folder, SWT.CLOSE);
        queryTab.setControl(ui);
        queryTab.setText(clazz.getName());
        folder.setSelection(queryTab);
        
        QueryTabController controller = new QueryTabController(this, folder, ui, clazz);
        controller.setInput(BrowserCore.getDefault().iterator(browserController.getCurrentFile(), clazz.getName()), null);
    }

}
