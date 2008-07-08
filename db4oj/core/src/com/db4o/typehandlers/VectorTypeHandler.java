/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.typehandlers;

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

/**
 * TypeHandler for java.util.Vector
 * @sharpen.ignore
 */
public class VectorTypeHandler implements TypeHandler4 , FirstClassHandler, CanHoldAnythingHandler, VariableLengthTypeHandler{

    public PreparedComparison prepareComparison(Context context, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    public void write(WriteContext context, Object obj) {
        Vector vector = (Vector)obj;
        writeElementCount(context, vector);
        writeElements(context, vector);
    }
    
	public Object read(ReadContext context) {
        Vector vector = (Vector)((UnmarshallingContext) context).persistentObject();
        int elementCount = context.readInt();
        TypeHandler4 elementHandler = elementTypeHandler(context, vector);
        for (int i = 0; i < elementCount; i++) {
            vector.addElement(context.readObject(elementHandler));
        }
        return vector;
    }
    
	private void writeElementCount(WriteContext context, Vector vector) {
		context.writeInt(vector.size());
	}

	private void writeElements(WriteContext context, Vector vector) {
		TypeHandler4 elementHandler = elementTypeHandler(context, vector);
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            context.writeObject(elementHandler, elements.nextElement());
        }
	}

    private ObjectContainerBase container(Context context) {
        return ((InternalObjectContainer)context.objectContainer()).container();
    }
    
    private TypeHandler4 elementTypeHandler(Context context, Vector vector){
        
        // TODO: If all elements in the list are of one type,
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
        }
    }

    public void defragment(DefragmentContext context) {
        TypeHandler4 handler = elementTypeHandler(context, null);
        int elementCount = context.readInt();
        for (int i = 0; i < elementCount; i++) {
            handler.defragment(context);
        }
    }
    
    public final void cascadeActivation(ActivationContext4 context) {
        Enumeration all = ((Vector) context.targetObject()).elements();
        while (all.hasMoreElements()) {
            context.cascadeActivationToChild(all.nextElement());
        }
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        return this;
    }
	
   public void readCandidates(QueryingReadContext context) throws Db4oIOException {
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

}