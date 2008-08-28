/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.typehandler;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */

/**
 * @decaf.ignore.jdk11
 */
public class EmbeddedListTypeHandler implements TypeHandler4 , FirstClassHandler, CanHoldAnythingHandler, VariableLengthTypeHandler, EmbeddedTypeHandler{

    public PreparedComparison prepareComparison(Context context, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    public void write(WriteContext context, Object obj) {
        List list = (List)obj;
        writeClass(context, list);
        writeElementCount(context, list);
        writeElements(context, list);
        return;
    }
    
    public Object read(ReadContext context) {
        ClassMetadata classMetadata = readClass(context);            
        List list = (List) classMetadata.instantiateFromReflector(container(context));
        int elementCount = context.readInt();
        TypeHandler4 elementHandler = elementTypeHandler(context, list);
        for (int i = 0; i < elementCount; i++) {
            list.add(context.readObject(elementHandler));
        }
        return list;
    }
    
    private void writeElementCount(WriteContext context, List list) {
        context.writeInt(list.size());
    }

    private void writeElements(WriteContext context, List list) {
        TypeHandler4 elementHandler = elementTypeHandler(context, list);
        final Iterator elements = list.iterator();
        while (elements.hasNext()) {
            context.writeObject(elementHandler, elements.next());
        }
    }

    private void writeClass(WriteContext context, List list) {
        int classID = classID(context, list);
        context.writeInt(classID);
    }
    
    private int classID(WriteContext context, Object obj) {
        ObjectContainerBase container = container(context);
        GenericReflector reflector = container.reflector();
        ReflectClass claxx = reflector.forObject(obj);
        ClassMetadata classMetadata = container.produceClassMetadata(claxx);
        return classMetadata.getID();
    }

    private ObjectContainerBase container(Context context) {
        return ((InternalObjectContainer)context.objectContainer()).container();
    }
    
    private TypeHandler4 elementTypeHandler(Context context, List list){
        
        // TODO: If all elements in the list are of one type,
        //       it is possible to use a more specific handler
        
        return container(context).handlers().untypedObjectHandler();
    }        

    private ClassMetadata readClass(ReadContext context) {
        int classID = context.readInt();
        ClassMetadata classMetadata = container(context).classMetadataForId(classID);
        return classMetadata;
    }

    public void delete(final DeleteContext context) throws Db4oIOException {
        if (! context.cascadeDelete()) {
            return;
        }
        TypeHandler4 handler = elementTypeHandler(context, null);
        skipClass(context);
        int elementCount = context.readInt();
        for (int i = elementCount; i > 0; i--) {
            handler.delete(context);
        }
    }

	private void skipClass(final ReadBuffer context) {
		context.readInt(); // class ID
	}

    public void defragment(DefragmentContext context) {
        context.copyID();
        TypeHandler4 handler = elementTypeHandler(context, null);
        int elementCount = context.readInt();
        for (int i = 0; i < elementCount; i++) {
            handler.defragment(context);
        }
    }
    
    public final void cascadeActivation(ActivationContext4 context) {
        Iterator all = ((List) context.targetObject()).iterator();
        while (all.hasNext()) {
            context.cascadeActivationToChild(all.next());
        }
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        return this;
    }
    
    public void collectIDs(final QueryingReadContext context) {
    	skipClass(context);
        int elementCount = context.readInt();
        TypeHandler4 elementHandler = context.container().handlers().untypedObjectHandler();
        for (int i = 0; i < elementCount; i++) {
            context.readId(elementHandler);
        }
    }

}
