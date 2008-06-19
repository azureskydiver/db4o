/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
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
        context.readInt(); // class ID
        int elementCount = context.readInt();
        for (int i = elementCount; i > 0; i--) {
            handler.delete(context);
        }
    }

    public void defragment(DefragmentContext context) {
        // TODO Auto-generated method stub

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
    
   public void readCandidates(QueryingReadContext context) throws Db4oIOException {
        context.readInt(); // skip class id
        int elementCount = context.readInt();
        TypeHandler4 elementHandler = context.container().handlers().untypedObjectHandler();
        readSubCandidates(context, elementCount, elementHandler);
    }
   
   private void readSubCandidates(QueryingReadContext context, int count, TypeHandler4 elementHandler) {
       QCandidates candidates = context.candidates();
       for (int i = 0; i < count; i++) {
           QCandidate qc = candidates.readSubCandidate(context, elementHandler);
           if(qc != null){
               candidates.addByIdentity(qc);
           }
       }
   }

    private ActivationDepth descend(ObjectContainerBase container, ActivationDepth depth, Object obj){
        if(obj == null){
            return new NonDescendingActivationDepth(depth.mode());
        }
        ClassMetadata cm = container.classMetadataForObject(obj);
        if(cm.isPrimitive()){
            return new NonDescendingActivationDepth(depth.mode());
        }
        return depth.descend(cm);
    }

}
