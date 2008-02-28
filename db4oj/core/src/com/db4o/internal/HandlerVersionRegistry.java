/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.internal.handlers.*;


/**
 * @exclude
 */
public class HandlerVersionRegistry {
    
    private final HandlerRegistry _registry;
    
    private final Hashtable4 _versions = new Hashtable4();
    
    public HandlerVersionRegistry(HandlerRegistry registry){
        _registry = registry;
    }

    public void put(FieldHandler handler, int version, TypeHandler4 replacement) {
        _versions.put(new HandlerVersionKey(handler, version), replacement);
    }

    public TypeHandler4 correctHandlerVersion(TypeHandler4 handler, int version) {
        if(version == HandlerRegistry.HANDLER_VERSION){
            return handler;
        }
        TypeHandler4 replacement = (TypeHandler4) _versions.get(new HandlerVersionKey(handler, version));
        if(replacement != null){
            return replacement;
        }
        
        if(handler instanceof FirstClassObjectHandler  && (version == 0)){
            handler = new FirstClassObjectHandler0(((FirstClassObjectHandler)handler).classMetadata());
        }
        if(handler instanceof MultidimensionalArrayHandler && (version == 0)){
            return new MultidimensionalArrayHandler0((ArrayHandler)handler, _registry, version);
        }
        if(handler instanceof ArrayHandler  && (version == 0)){
            return new ArrayHandler0((ArrayHandler)handler, _registry, version);
        }
        if(handler instanceof PrimitiveFieldHandler  && (version == 0)){
            return new PrimitiveFieldHandler((PrimitiveFieldHandler) handler, _registry, version);
        }
        return handler;
    }
    
    private class HandlerVersionKey {
        
        private final FieldHandler _handler;
        
        private final int _version;
        
        public HandlerVersionKey(FieldHandler handler, int version){
            _handler = handler;
            _version = version;
        }

        public int hashCode() {
            return _handler.hashCode() + _version * 4271;
        }

        public boolean equals(Object obj) {
            HandlerVersionKey other = (HandlerVersionKey) obj;
            return _handler.equals(other._handler) && _version == other._version;
        }

    }
    
}
