/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;


import com.db4o.ObjectSet;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.browser.model.nodes.InstanceNode;

/**
 * ObjectSetGraphIterator.  A visitor that can traverse the contents of an 
 * object database file.
 *
 * @author djo
 */
public class ObjectSetGraphIterator extends AbstractGraphIterator {
    
	/**
     * (non-API)
     * Constructor ObjectSetGraphIterator.  Constructs a ObjectSetGraphIterator that can
     * traverse all the objects in a database graph.
     * 
     * @param database The Database to traverse
     * @param classes The StoredClasses to consider as the root
     */
    public ObjectSetGraphIterator(Database database, ObjectSet queryResult) {
        this.database = database;
        
        startModel = new IModelNode[queryResult.size()];
        for (int i = 0; i < queryResult.size(); i++) {
			startModel[i] = new InstanceNode(queryResult.next(), database);
		}
        reset();
    }
}
