/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class PrimitiveFieldHandler extends ClassMetadata{
    
    private final TypeHandler4 _handler;
    
    PrimitiveFieldHandler(ObjectContainerBase container, TypeHandler4 handler, int handlerID) {
    	super(container, handler.classReflector());
        i_fields = FieldMetadata.EMPTY_ARRAY;
        _handler = handler;
        _id = handlerID;
    }
    
    void activateFields(Transaction trans, Object obj, int depth) {
        // Override
        // do nothing
    }

    final void addToIndex(LocalObjectContainer container, Transaction trans, int id) {
        // Override
        // Primitive Indices will be created later.
    }

    boolean allowsQueries() {
        return false;
    }

    void cacheDirty(Collection4 col) {
        // do nothing
    }

    public void deleteEmbedded(MarshallerFamily mf, StatefulBuffer a_bytes) throws Db4oIOException {
        if(mf._primitive.useNormalClassRead()){
            super.deleteEmbedded(mf, a_bytes);
            return;
        }
        
        // Do nothing here, we should be in the payload area
        // no action to be taken.
    }

    
    public void deleteEmbedded1(MarshallerFamily mf, StatefulBuffer a_bytes, int a_id) throws Db4oIOException  {
        
        if(_handler instanceof ArrayHandler){
            ArrayHandler ya = (ArrayHandler)_handler;
            
            // TODO: the following checks, whether the array stores
            // primitives. There is one case that is not covered here:
            // If a primitive array is stored to an untyped array or
            // to an Object variable, they would need to be deleted 
            // and freed also. However, if they are untyped, every 
            // single one would have to be read an checked and this
            // would be extremely slow.
            
            // Solution: Store information, whether an object is 
            // primitive in our pointers, in the highest bit of the
            // length int.
            
            if(ya._isPrimitive){
                ya.deletePrimitiveEmbedded(a_bytes, this);
                a_bytes.slotDelete();
                return;
            }
        }
        
       if(_handler instanceof UntypedFieldHandler){
            // Any-In-Any: Ignore delete 
            a_bytes.incrementOffset(_handler.linkLength());
        }else{
            _handler.deleteEmbedded(mf, a_bytes);
        }
		
		// TODO: Was this freeing call necessary? 
		//   free(a_bytes.getTransaction(), a_id, a_bytes.getAddress(), a_bytes.getLength());
		
		free(a_bytes, a_id);
			
    }

    void deleteMembers(MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes, int a_type, boolean isUpdate) {
        if (a_type == Const4.TYPE_ARRAY) {
            new ArrayHandler(a_bytes.getStream(),this, true).deletePrimitiveEmbedded(a_bytes, this);
        } else if (a_type == Const4.TYPE_NARRAY) {
            new MultidimensionalArrayHandler(a_bytes.getStream(),this, true).deletePrimitiveEmbedded(a_bytes, this);
        }
    }
    
	final void free(StatefulBuffer a_bytes, int a_id) {
          a_bytes.getTransaction().slotFreePointerOnCommit(a_id, a_bytes.slot());
	}
    
	public boolean hasClassIndex() {
	    return false;
	}

    Object instantiate(ObjectReference ref, Object obj, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer buffer, boolean addToIDTree) {
        if (obj == null) {
        	// FIXME catchall
            try {
                obj = _handler.read(mf, buffer, true);
            } 
            catch (CorruptionException ce) {
                return null;
            }
            ref.setObjectWeak(buffer.getStream(), obj);
        }
        ref.setStateClean();
        return obj;
    }
    
    Object instantiateTransient(ObjectReference a_yapObject, Object a_object, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes) {
    	// FIXME catchall
        try {
            return _handler.read(mf, a_bytes, true);
        }
        catch (CorruptionException ce) {
            return null;
        }
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

    void instantiateFields(ObjectReference a_yapObject, Object a_onObject, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes) {
        Object obj = null;
        // FIXME catchall
        try {
            obj = _handler.read(mf, a_bytes, true);
        }
        catch (CorruptionException ce) {
        }
        if (obj != null  &&  (_handler instanceof DateHandler)) {
            ((DateHandler)_handler).copyValue(obj, a_onObject);
        }
    }
    
    void instantiateFields(UnmarshallingContext context) {
        Object obj = context.read(_handler);
        if (obj != null  &&  (_handler instanceof DateHandler)) {
            ((DateHandler)_handler).copyValue(obj, context.persistentObject());
        }
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
    
    public Comparable4 prepareComparison(Object a_constraint) {
        _handler.prepareComparison(a_constraint);
        return _handler;
    }
    
    public Object read(MarshallerFamily mf, StatefulBuffer a_bytes, boolean redirect) throws CorruptionException, Db4oIOException {
        if(mf._primitive.useNormalClassRead()){
            return super.read(mf, a_bytes, redirect);
        }
        return _handler.read(mf, a_bytes, false);
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes) {
        if (isArray()) {
            return _handler;
        }
        return null;
    }
    
    public Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, Buffer reader, boolean toArray) throws CorruptionException, Db4oIOException {
        if(mf._primitive.useNormalClassRead()){
            return super.readQuery(trans, mf, withRedirection, reader, toArray);
        }
        return _handler.readQuery(trans, mf, withRedirection, reader, toArray);
    }

    
    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection) {
        return _handler.readSubCandidate(mf, reader, candidates, withIndirection);
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

    public void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect) {
        if(mf._primitive.useNormalClassRead()){
            super.defrag(mf,readers, redirect);
        }
        else {
            _handler.defrag(mf, readers, false);
        }
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

}
