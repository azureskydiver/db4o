/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.controllers;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;

import com.db4o.ObjectSet;
import com.db4o.binding.CannotSaveException;
import com.db4o.browser.gui.controllers.BrowserController;
import com.db4o.browser.gui.controllers.QueryController;
import com.db4o.browser.model.BrowserCore;
import com.db4o.browser.model.Database;
import com.db4o.browser.model.ObjectSetGraphIterator;
import com.db4o.browser.query.model.QueryBuilderModel;
import com.db4o.browser.query.view.QueryBrowserPane;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

/**
 * QueryTabController. Manages a single query tab. Creates the MVC relationship
 * for the query editor's model, view, and controller.
 * 
 * Upon request, gets the current query from the query model, runs it, and hands
 * the resulting IGraphIterator to the embedded browser's controller object.
 * 
 * @author djo
 */
public class QueryTabController extends BrowserController {
    
    private BrowserController databaseBrowserController;
    private Database database; 
    
    private QueryBuilderModel queryModel;
    private QueryBuilderPaneController queryController;
    
    private ReflectClass input;

    public QueryTabController(QueryController queryController, CTabFolder folder, QueryBrowserPane ui, ReflectClass clazz) {
        super(ui, queryController);
        this.databaseBrowserController = queryController.getBrowserController();
        this.database = BrowserCore.getDefault().getDatabase(databaseBrowserController.getCurrentFile());
    }
    
    protected void addQueryButtonHandler() {
        ui.getQueryButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                try {
                    queryController.save();
                    runQuery();
                } catch (CannotSaveException e1) {
                    // We couldn't save, so do nothing.  The user has already
                    // been informed of the problem.
                }
            }
        });
    }

    protected void runQuery() {
        Query query = queryModel.getQuery();
        ObjectSet results = query.execute();
        setInput(new ObjectSetGraphIterator(database, results), null);
    }

    public void setInput(ReflectClass input) {
        this.input = input;
        
        queryModel = new QueryBuilderModel(input, database);
        queryController = new QueryBuilderPaneController(queryModel, (QueryBrowserPane)ui);
        
        runQuery();
    }

}
