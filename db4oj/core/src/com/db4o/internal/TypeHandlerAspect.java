/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;


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

    public String getName() {
        return _typeHandler.getClass().getName();
    }

    public void cascadeActivation(Transaction trans, Object obj, ActivationDepth depth) {
        throw new NotImplementedException();
    }

    public void collectIDs(CollectIdContext context) {
        throw new NotImplementedException();
    }

    public void defragAspect(DefragmentContext context) {
        throw new NotImplementedException();
    }

    public int extendedLength() {
        throw new NotImplementedException();
        // return 0;
    }

    public int linkLength() {
        throw new NotImplementedException();
        // return 0;
    }

    public void marshall(MarshallingContext context, Object child) {
        _typeHandler.write(context, context.getObject());
    }
    
    public AspectType aspectType() {
        return AspectType.TYPEHANDLER;
    }

    public void instantiate(UnmarshallingContext context) {
        _typeHandler.read(context);
    }

}
