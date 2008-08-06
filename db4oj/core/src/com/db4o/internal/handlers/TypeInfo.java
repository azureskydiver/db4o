/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class TypeInfo {
    
 // TODO: remove when no longer needed in HandlerRegistry
    public ClassMetadata classMetadata;  
    
    public FieldHandler fieldHandler;
    
    public TypeHandler4 typeHandler;
    
    public ReflectClass classReflector;

    public TypeInfo(
        ClassMetadata classMetadata_, 
        FieldHandler fieldHandler_,
        TypeHandler4 typeHandler_, 
        ReflectClass classReflector_) {
        classMetadata = classMetadata_;
        fieldHandler = fieldHandler_;
        typeHandler = typeHandler_;
        classReflector = classReflector_;
    }

}
