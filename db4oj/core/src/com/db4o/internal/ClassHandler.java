/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


import com.db4o.internal.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class ClassHandler {
    
    private final ClassMetadata _classMetadata;
    
    public ClassHandler(ClassMetadata classMetadata) {
        _classMetadata = classMetadata;
    }

    public boolean customizedNewInstance() {
        return configInstantiates();
    }
    
    private boolean configInstantiates(){
        return config() != null && config().instantiates();
    }
    
    public Object instantiateObject(StatefulBuffer buffer, MarshallerFamily mf) {
        if (configInstantiates()) {
            return _classMetadata.instantiateFromConfig(buffer.getStream(), buffer, mf);
        }
        return  _classMetadata.instantiateFromReflector(buffer.getStream());
    }
    
    public Config4Class config() {
        return _classMetadata.config();
    }
    
    public ReflectClass classSubstitute(){
        return null;
    }
    
    public boolean ignoreAncestor() {
        return false;
    }



}
