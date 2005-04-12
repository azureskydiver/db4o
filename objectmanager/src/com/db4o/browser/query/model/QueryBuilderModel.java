/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.model;

import java.util.HashSet;

import com.db4o.browser.model.Database;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

public class QueryBuilderModel {

    private Database database;

    private QueryPrototypeInstance rootInstance;

    public QueryBuilderModel(ReflectClass input, Database database) {
        this.database = database;
        addTypeToQuery(input);
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
    
    private HashSet queryTypes = new HashSet();

    public boolean typeInQuery(ReflectClass fieldType) {
        return queryTypes.contains(fieldType);
    }

    public void addTypeToQuery(ReflectClass fieldType) {
        queryTypes.add(fieldType);
    }
}
