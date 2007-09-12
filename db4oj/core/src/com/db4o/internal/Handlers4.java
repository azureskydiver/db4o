/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class Handlers4 {

    
    public static final int LONG_ID = 2;
    
    
    
    public static boolean handlerCanHold(TypeHandler4 handler, ReflectClass claxx){
        TypeHandler4 baseTypeHandler = baseTypeHandler(handler);
        if(Handlers4.handlesSimple(baseTypeHandler)){
            return claxx.equals(baseTypeHandler.classReflector());
        }
        
        if(baseTypeHandler instanceof UntypedFieldHandler){
            return true;
        }
        
        ClassMetadata classMetadata = (ClassMetadata) baseTypeHandler;
        ReflectClass classReflector = classMetadata.classReflector();
        if(classReflector.isCollection()){
            return true;
        }
        return classReflector.isAssignableFrom(claxx);
    }
    
    public static boolean handlesSimple(TypeHandler4 handler){
        TypeHandler4 baseTypeHandler = baseTypeHandler(handler); 
        return (baseTypeHandler instanceof PrimitiveHandler) ||  (baseTypeHandler instanceof StringHandler);
    }
    
    public static boolean handlesClass(TypeHandler4 handler){
        return baseTypeHandler(handler) instanceof ClassMetadata;
    }
    
    public static ReflectClass primitiveClassReflector(TypeHandler4 handler){
        TypeHandler4 baseTypeHandler = baseTypeHandler(handler);
        if(baseTypeHandler instanceof PrimitiveHandler){
            return ((PrimitiveHandler)baseTypeHandler).primitiveClassReflector();
        }
        return null;
    }
    
    public static TypeHandler4 baseTypeHandler(TypeHandler4 handler){
        if(handler instanceof ArrayHandler){
            return ((ArrayHandler)handler)._handler;
        }
        if(handler instanceof PrimitiveFieldHandler){
            return ((PrimitiveFieldHandler)handler).typeHandler();
        }
        return handler;
    }
}
