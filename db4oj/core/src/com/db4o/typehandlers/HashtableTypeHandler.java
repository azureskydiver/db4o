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
 * Typehandler for java.util.Hashtable
 * @sharpen.ignore
 */
public class HashtableTypeHandler implements TypeHandler4 , FirstClassHandler, CanHoldAnythingHandler, VariableLengthTypeHandler{
    
    public PreparedComparison prepareComparison(Context context, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    public void write(WriteContext context, Object obj) {
        Hashtable hashTable = (Hashtable)obj;
        KeyValueHandlerPair handlers = detectKeyValueTypeHandlers(container(context), hashTable);
        writeTypeHandlerIds(context, handlers);
        writeElementCount(context, hashTable);
        writeElements(context, hashTable, handlers);
    }
    
    public Object read(ReadContext context) {
    	UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context; 
    	Hashtable hashtable = (Hashtable) unmarshallingContext.persistentObject();
    	hashtable.clear();
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(unmarshallingContext, unmarshallingContext);
        int elementCount = unmarshallingContext.readInt();
        for (int i = 0; i < elementCount; i++) {
            Object key = unmarshallingContext.readActivatedObject(handlers._keyHandler);
            Object value = unmarshallingContext.readObject(handlers._valueHandler);
            hashtable.put(key, value);
        }
        return hashtable;
    }
    
    private void writeElementCount(WriteContext context, Hashtable hashtable) {
        context.writeInt(hashtable.size());
    }

    private void writeElements(WriteContext context, Hashtable hashtable, KeyValueHandlerPair handlers) {
        final Enumeration elements = hashtable.keys();
        while (elements.hasMoreElements()) {
            Object key = elements.nextElement();
            context.writeObject(handlers._keyHandler, key);
            context.writeObject(handlers._valueHandler, hashtable.get(key));
        }
    }

    private ObjectContainerBase container(Context context) {
        return ((InternalObjectContainer)context.objectContainer()).container();
    }
    
    public void delete(final DeleteContext context) throws Db4oIOException {
        if (! context.cascadeDelete()) {
            return;
        }
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(context, context);
        int elementCount = context.readInt();
        for (int i = elementCount; i > 0; i--) {
            handlers._keyHandler.delete(context);
            handlers._valueHandler.delete(context);
        }
    }

    public void defragment(DefragmentContext context) {
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(context, context);
        int elementCount = context.readInt(); 
        for (int i = elementCount; i > 0; i--) {
            context.defragment(handlers._keyHandler);
            context.defragment(handlers._valueHandler);
        }
    }
    
    public final void cascadeActivation(ActivationContext4 context) {
    	Hashtable hashtable = (Hashtable) context.targetObject();
        Enumeration keys = (hashtable).keys();
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            context.cascadeActivationToChild(key);
            context.cascadeActivationToChild(hashtable.get(key));
        }
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        return this;
    }
    
    public void collectIDs(final QueryingReadContext context) {
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(context, context);
        int elementCount = context.readInt();
        for (int i = 0; i < elementCount; i++) {
            context.readId(handlers._keyHandler);
            context.skipId(handlers._valueHandler);
        }
    }

	private void writeTypeHandlerIds(WriteContext context, KeyValueHandlerPair handlers) {
		context.writeInt(0);
		context.writeInt(0);
	}

	private KeyValueHandlerPair readKeyValueTypeHandlers(ReadBuffer buffer, Context context) {
		buffer.readInt();
		buffer.readInt();
		TypeHandler4 untypedHandler = container(context).handlers().untypedObjectHandler();
		return new KeyValueHandlerPair(untypedHandler, untypedHandler);
	}

	private KeyValueHandlerPair detectKeyValueTypeHandlers(InternalObjectContainer container, Hashtable hashTable) {
		TypeHandler4 untypedHandler = container.handlers().untypedObjectHandler();
		return new KeyValueHandlerPair(untypedHandler, untypedHandler);
	}

}
