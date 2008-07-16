/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class TypeHandlerAspect extends ClassAspect {
    
    private final TypeHandler4 _typeHandler;
    
    public TypeHandlerAspect(TypeHandler4 typeHandler){
        _typeHandler = typeHandler;
    }
    
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj == null || obj.getClass() != getClass()){
            return false;
        }
        TypeHandlerAspect other = (TypeHandlerAspect) obj;
        return _typeHandler.equals(other._typeHandler);
    }
    
    public int hashCode() {
        return _typeHandler.hashCode();
    }
    

}
