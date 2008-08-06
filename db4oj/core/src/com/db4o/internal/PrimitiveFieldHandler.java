/* Copyright (C) 2004 - 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class PrimitiveFieldHandler extends ClassMetadata implements FieldHandler, VersionedTypeHandler{
    
    private static final int HASHCODE_FOR_NULL = 283636383;
    
    private final TypeHandler4 _handler;
    
    public PrimitiveFieldHandler(ObjectContainerBase container, TypeHandler4 handler, int handlerID, ReflectClass classReflector) {
    	super(container, classReflector);
        _aspects = FieldMetadata.EMPTY_ARRAY;
        _handler = handler;
        _id = handlerID;
    }
    
    public PrimitiveFieldHandler(){
        super(null, null);
        _handler = null;
    }

    public void activateFields(Transaction trans, Object obj, ActivationDepth depth) {
        // Override
        // do nothing
    }

    final void addToIndex(Transaction trans, int id) {
        // Override
        // Primitive Indices will be created later.
    }

    boolean allowsQueries() {
        return false;
    }

    void cacheDirty(Collection4 col) {
        // do nothing
    }
    
    public boolean descendOnCascadingActivation() {
        return false;
    }

    public void delete(DeleteContext context) throws Db4oIOException {
    	if(context.isLegacyHandlerVersion()){
    		context.readInt();
    		context.defragmentRecommended();
    	}
    }

    void deleteMembers(MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes, int a_type, boolean isUpdate) {
        if (a_type == Const4.TYPE_ARRAY) {
            new ArrayHandler(this, true).deletePrimitiveEmbedded(a_bytes, this);
        } else if (a_type == Const4.TYPE_NARRAY) {
            new MultidimensionalArrayHandler(this, true).deletePrimitiveEmbedded(a_bytes, this);
        }
    }
    
    void deleteMembers(DeleteContextImpl context, int a_type, boolean isUpdate) {
        if (a_type == Const4.TYPE_ARRAY) {
            new ArrayHandler(this, true).deletePrimitiveEmbedded((StatefulBuffer) context.buffer(), this);
        } else if (a_type == Const4.TYPE_NARRAY) {
            new MultidimensionalArrayHandler(this, true).deletePrimitiveEmbedded((StatefulBuffer) context.buffer(), this);
        }
    }
    
	final void free(StatefulBuffer a_bytes, int a_id) {
          a_bytes.transaction().slotFreePointerOnCommit(a_id, a_bytes.slot());
	}
    
	public boolean hasClassIndex() {
	    return false;
	}

    public Object instantiate(UnmarshallingContext context) {
        Object obj = context.persistentObject();
        if (obj == null) {
            obj = context.read(_handler);
            context.setObjectWeak(obj);
        }
        context.setStateClean();
        return obj;
    }
    
    public Object instantiateTransient(UnmarshallingContext context) {
        return _handler.read(context);
    }

    Object instantiateFields(UnmarshallingContext context) {
        Object obj = context.read(_handler);
        if (obj == null  ||  ! (_handler instanceof DateHandler)) {
            return obj;
        }
        final Object existing = context.persistentObject();
		Object newValue = dateHandler().copyValue(obj, existing);
		
        // FIXME: It should not be necessary to set persistentObject here
        context.persistentObject(newValue);
        
		return newValue;
    }

	private DateHandler dateHandler() {
		return ((DateHandler)_handler);
	}

    public boolean isArray() {
        return _id == Handlers4.ANY_ARRAY_ID || _id == Handlers4.ANY_ARRAY_N_ID;
    }
    
    public boolean isPrimitive(){
        return true;
    }
    
	public boolean isStrongTyped(){
		return false;
	}
    
    public PreparedComparison prepareComparison(Context context, Object source) {
    	return _handler.prepareComparison(context, source);
    }
    
    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        if (isArray()) {
            return _handler;
        }
        return null;
    }
    
    public ObjectID readObjectID(InternalReadContext context){
        if(_handler instanceof ClassMetadata){
            return ((ClassMetadata)_handler).readObjectID(context);
        }
        if(_handler instanceof ArrayHandler){
            // TODO: Here we should theoretically read through the array and collect candidates.
            // The respective construct is wild: "Contains query through an array in an array."
            // Ignore for now.
            return ObjectID.IGNORE;
        }
        return ObjectID.NOT_POSSIBLE;
    }
    
    void removeFromIndex(Transaction ta, int id) {
        // do nothing
    }
    
    public final boolean writeObjectBegin() {
        return false;
    }
    
    public String toString(){
        return "Wraps " + _handler.toString() + " in YapClassPrimitive";
    }

    public void defragment(DefragmentContext context) {
    	_handler.defragment(context);
    }
    
    public Object wrapWithTransactionContext(Transaction transaction, Object value) {
        return value;
    }
    
    public Object read(ReadContext context) {
        return _handler.read(context);
    }
    
    public void write(WriteContext context, Object obj) {
        _handler.write(context, obj);
    }
    
    public TypeHandler4 typeHandler(){
        return _handler;
    }
    
    public TypeHandler4 delegateTypeHandler(){
        return _handler;
    }
    
    public TypeHandler4 unversionedTemplate() {
        return new PrimitiveFieldHandler(null, null, 0, null);
    }
    
    public boolean equals(Object obj) {
        if(! (obj instanceof PrimitiveFieldHandler)){
            return false;
        }
        PrimitiveFieldHandler other = (PrimitiveFieldHandler) obj;
        if(_handler == null){
            return other._handler == null;
        }
        return _handler.equals(other._handler);
    }
    
    public int hashCode() {
        if(_handler == null){
            return HASHCODE_FOR_NULL;
        }
        return _handler.hashCode();
    }
    
    public Object deepClone(Object context) {
        TypeHandlerCloneContext typeHandlerCloneContext = (TypeHandlerCloneContext) context;
        PrimitiveFieldHandler original = (PrimitiveFieldHandler) typeHandlerCloneContext.original;
        TypeHandler4 delegateTypeHandler = typeHandlerCloneContext.correctHandlerVersion(original.delegateTypeHandler());
        return new PrimitiveFieldHandler(original.container(), delegateTypeHandler, original._id, original.classReflector());
    }
    
    public boolean isSecondClass() {
    	return isSecondClass(_handler);
    }


}
