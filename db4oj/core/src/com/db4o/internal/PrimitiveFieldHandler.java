/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class PrimitiveFieldHandler extends ClassMetadata{
    
    public final TypeHandler4 i_handler;
    
    PrimitiveFieldHandler(ObjectContainerBase a_stream, TypeHandler4 a_handler) {
    	super(a_stream, a_handler.classReflector());
        i_fields = FieldMetadata.EMPTY_ARRAY;
        i_handler = a_handler;
    }

    void activateFields(Transaction a_trans, Object a_object, int a_depth) {
        // Override
        // do nothing
    }


    final void addToIndex(LocalObjectContainer a_stream, Transaction a_trans, int a_id) {
        // Override
        // Primitive Indices will be created later.
    }

    boolean allowsQueries() {
        return false;
    }

    void cacheDirty(Collection4 col) {
        // do nothing
    }

    public ReflectClass classReflector(){
    	return i_handler.classReflector();
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
        
        if(i_handler instanceof ArrayHandler){
            ArrayHandler ya = (ArrayHandler)i_handler;
            
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
        
       if(i_handler instanceof UntypedFieldHandler){
            // Any-In-Any: Ignore delete 
            a_bytes.incrementOffset(i_handler.linkLength());
        }else{
            i_handler.deleteEmbedded(mf, a_bytes);
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

    Object instantiate(ObjectReference a_yapObject, Object a_object, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes, boolean a_addToIDTree) {
        if (a_object == null) {
        	// FIXME catchall
            try {
                a_object = i_handler.read(mf, a_bytes, true);
            } 
            catch (CorruptionException ce) {
                return null;
            }
            a_yapObject.setObjectWeak(a_bytes.getStream(), a_object);
        }
        a_yapObject.setStateClean();
        return a_object;
    }
    
    Object instantiateTransient(ObjectReference a_yapObject, Object a_object, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes) {
    	// FIXME catchall
        try {
            return i_handler.read(mf, a_bytes, true);
        }
        catch (CorruptionException ce) {
            return null;
        }
    }

    void instantiateFields(ObjectReference a_yapObject, Object a_onObject, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes) {
        Object obj = null;
        // FIXME catchall
        try {
            obj = i_handler.read(mf, a_bytes, true);
        }
        catch (CorruptionException ce) {
        }
        if (obj != null  &&  (i_handler instanceof DateHandler)) {
            ((DateHandler)i_handler).copyValue(obj, a_onObject);
        }
    }

    public boolean isArray() {
        return _id == HandlerRegistry.ANY_ARRAY_ID || _id == HandlerRegistry.ANY_ARRAY_N_ID;
    }
    
    public boolean isPrimitive(){
        return true;
    }
    
	public boolean isStrongTyped(){
		return false;
	}
    
    public Comparable4 prepareComparison(Object a_constraint) {
        i_handler.prepareComparison(a_constraint);
        return i_handler;
    }
    
    public Object read(MarshallerFamily mf, StatefulBuffer a_bytes, boolean redirect) throws CorruptionException, Db4oIOException {
        if(mf._primitive.useNormalClassRead()){
            return super.read(mf, a_bytes, redirect);
        }
        return i_handler.read(mf, a_bytes, false);
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes) {
        if (isArray()) {
            return i_handler;
        }
        return null;
    }
    
    public Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, Buffer reader, boolean toArray) throws CorruptionException, Db4oIOException {
        if(mf._primitive.useNormalClassRead()){
            return super.readQuery(trans, mf, withRedirection, reader, toArray);
        }
        return i_handler.readQuery(trans, mf, withRedirection, reader, toArray);
    }

    
    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection) {
        return i_handler.readSubCandidate(mf, reader, candidates, withIndirection);
    } 

    void removeFromIndex(Transaction ta, int id) {
        // do nothing
    }
    
    public final boolean writeObjectBegin() {
        return false;
    }
    
    public Object write(MarshallerFamily mf, Object a_object, boolean topLevel, StatefulBuffer a_bytes, boolean withIndirection, boolean restoreLinkOffset) {
        mf._primitive.writeNew(a_bytes.getTransaction(), this, a_object, topLevel, a_bytes, withIndirection, restoreLinkOffset);
        return a_object;
    }
    
    public String toString(){
        return "Wraps " + i_handler.toString() + " in YapClassPrimitive";
    }

    public void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect) {
        if(mf._primitive.useNormalClassRead()){
            super.defrag(mf,readers, redirect);
        }
        else {
            i_handler.defrag(mf, readers, false);
        }
    }
    
    public Object wrapWithTransactionContext(Transaction transaction, Object value) {
        return value;
    }
    
    public Object read(ReadContext context) {
        throw new NotImplementedException();
    }
    
    public void write(WriteContext context, Object obj) {
        i_handler.write(context, obj);
    }

}
