/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;


import com.db4o.browser.model.nodes.ClassNode;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.reflect.ReflectClass;

/**
 * DatabaseGraphIterator.  A visitor that can traverse the contents of an 
 * object database file.
 *
 * @author djo
 */
public class DatabaseGraphIterator extends AbstractGraphIterator {
    
	/**
     * (non-API)
     * Constructor DatabaseGraphIterator.  Constructs a DatabaseGraphIterator that can
     * traverse all the objects in a database graph.
     * 
     * @param database The Database to traverse
     * @param classes The StoredClasses to consider as the root
     */
    public DatabaseGraphIterator(IDatabase database, ReflectClass[] start) {
        this.database = database;
        
        startModel = new IModelNode[start.length];
        for (int i = 0; i < start.length; i++) {
			startModel[i] = new ClassNode(start[i], database);
		}
        reset();
    }
}
