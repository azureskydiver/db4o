/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.typehandlers;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * Typehandler for classes that implement java.util.Map.
 * @decaf.ignore.jdk11
 */
public class MapTypeHandler implements TypeHandler4 , FirstClassHandler, CanHoldAnythingHandler, VariableLengthTypeHandler{
    
    public PreparedComparison prepareComparison(Context context, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    public void write(WriteContext context, Object obj) {
        Map map = (Map)obj;
        writeElementCount(context, map);
        writeElements(context, map);
    }
    
    public Object read(ReadContext context) {
    	UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context; 
        Map map = (Map)unmarshallingContext.persistentObject();
        map.clear();
        int elementCount = context.readInt();
        TypeHandler4 elementHandler = elementTypeHandler(context, map);
        for (int i = 0; i < elementCount; i++) {
            Object key = unmarshallingContext.readActivatedObject(elementHandler);
            Object value = context.readObject(elementHandler);
            if(key != null  && value != null){
            	map.put(key, value);
            }
        }
        return map;
    }
    
    private void writeElementCount(WriteContext context, Map map) {
        context.writeInt(map.size());
    }

    private void writeElements(WriteContext context, Map map) {
        TypeHandler4 elementHandler = elementTypeHandler(context, map);
        final Iterator elements = map.keySet().iterator();
        while (elements.hasNext()) {
            Object key = elements.next();
            context.writeObject(elementHandler, key);
            context.writeObject(elementHandler, map.get(key));
        }
    }

    private ObjectContainerBase container(Context context) {
        return ((InternalObjectContainer)context.objectContainer()).container();
    }
    
    private TypeHandler4 elementTypeHandler(Context context, Map map){
        
        // TODO: If all elements in the map are of one type,
        //       it is possible to use a more specific handler
        
        return container(context).handlers().untypedObjectHandler();
    }        

    public void delete(final DeleteContext context) throws Db4oIOException {
        if (! context.cascadeDelete()) {
            return;
        }
        TypeHandler4 handler = elementTypeHandler(context, null);
        int elementCount = context.readInt();
        for (int i = elementCount; i > 0; i--) {
            handler.delete(context);
            handler.delete(context);
        }
    }

    public void defragment(DefragmentContext context) {
        TypeHandler4 handler = elementTypeHandler(context, null);
        int elementCount = context.readInt(); 
        for (int i = elementCount; i > 0; i--) {
            context.defragment(handler);
            context.defragment(handler);
        }
    }
    
    public final void cascadeActivation(ActivationContext4 context) {
        Map map = (Map) context.targetObject();
        Iterator keys = (map).keySet().iterator();
        while (keys.hasNext()) {
            final Object key = keys.next();
            context.cascadeActivationToChild(key);
            context.cascadeActivationToChild(map.get(key));
        }
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        return this;
    }
    
    public void collectIDs(final QueryingReadContext context) {
        int elementCount = context.readInt();
        TypeHandler4 elementHandler = context.container().handlers().untypedObjectHandler();
        for (int i = 0; i < elementCount; i++) {
            context.readId(elementHandler);
            context.skipId(elementHandler);
        }
    }

}
