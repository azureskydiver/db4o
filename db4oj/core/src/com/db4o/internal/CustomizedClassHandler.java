/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.config.*;
import com.db4o.internal.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class CustomizedClassHandler extends ClassHandler {
    
    private final CustomClassHandler _customHandler;

    public CustomizedClassHandler(ClassMetadata classMetadata, CustomClassHandler customHandler) {
        super(classMetadata);
        _customHandler = customHandler;
    }
    
    public boolean customizedNewInstance() {
        return _customHandler.canNewInstance();
    }
    
    public Object instantiateObject(StatefulBuffer buffer, MarshallerFamily mf) {
        if (customizedNewInstance()) {
            return _customHandler.newInstance();
        }
        return  super.instantiateObject(buffer, mf);
    }
    
    public ReflectClass classSubstitute(){
        return _customHandler.classSubstitute();
    }
    
    public boolean ignoreAncestor() {
        return _customHandler.ignoreAncestor();
    }

}
