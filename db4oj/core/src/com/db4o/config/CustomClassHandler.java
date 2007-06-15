/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

import com.db4o.reflect.*;


/**
 * Custom class handler to provide modified instantiation, 
 * marshalling and querying behaviour for special classes.
 */
public interface CustomClassHandler {
    
    /**
     * implement to create a new instance of an object.
     * @return the new Object
     */
    public Object newInstance();
    
    /**
     * implement and return true, if this CustomClassHandler creates
     * object instances on calls to {@link #newInstance()}. 
     * @return true, if this CustomClassHandler creates new instances.
     */
    public boolean canNewInstance();

    public ReflectClass classSubstitute();
    
    public boolean ignoreAncestor();


}
