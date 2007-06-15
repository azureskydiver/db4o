/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

import com.db4o.reflect.*;


/**
 * base class for CustomClassHandler, to change some behaviour only 
 */
public class VanillaClassHandler implements CustomClassHandler{

    public boolean canNewInstance() {
        return false;
    }

    public Object newInstance() {
        return null;
    }

    public ReflectClass classSubstitute() {
        return null;
    }
    
    public boolean ignoreAncestor() {
        return false;
    }


}
