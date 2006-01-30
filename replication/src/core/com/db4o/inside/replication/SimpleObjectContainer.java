/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.replication;

import com.db4o.*;


public interface SimpleObjectContainer {
    
    public void activate(Object object);
    
    public void commit();
    
    public void delete(Class clazz);
    
    public String getName();

    public ObjectSet getStoredObjects(Class type);
     
    public void storeNew(Object o);
     
    public void update(Object o);

}
