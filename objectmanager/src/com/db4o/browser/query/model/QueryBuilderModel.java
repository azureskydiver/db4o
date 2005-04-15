/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.model;

import com.db4o.browser.model.Database;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

public class QueryBuilderModel {

    private Database database;

    private QueryPrototypeInstance rootInstance;

    public QueryBuilderModel(ReflectClass input, Database database) {
        this.database = database;
        rootInstance = new QueryPrototypeInstance(input, this);
    }

    public Query getQuery() {
        Query result = database.query();
        rootInstance.addUserConstraints(result);
        return result;
    }

    public QueryPrototypeInstance getRootInstance() {
        return rootInstance;
    }
    
    public Database getDatabase() {
        return database;
    }
    
}
