/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.swt.custom.CTabFolder;

import com.db4o.browser.gui.views.QueryBrowserPane;
import com.db4o.reflect.ReflectClass;

public class QueryTabController extends BrowserController {

    public QueryTabController(QueryController queryController, CTabFolder folder, QueryBrowserPane ui, ReflectClass clazz) {
        super(ui, queryController);
    }

}
