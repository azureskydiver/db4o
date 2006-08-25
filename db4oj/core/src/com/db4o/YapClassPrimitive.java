/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.ClassIndex;
import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class YapClassPrimitive extends YapClass{
    
    public final TypeHandler4 i_handler;
    
    YapClassPrimitive(YapStream a_stream, TypeHandler4 a_handler) {
    	super(a_stream, a_handler.classReflector());
        i_fields = YapField.EMPTY_ARRAY;
        i_handler = a_handler;
    }

    void activateFields(Transaction a_trans, Object a_object, int a_depth) {
        // Override
        // do nothing
    }


    final void addToIndex(YapFile a_stream, Transaction a_trans, int a_id) {
        // Override
        // Primitive Indices will be created later.
    }

    boolean allowsQueries() {
        return false;
    }

    void cacheDirty(Collection4 col) {
        // do nothing
    }

    public boolean canHold(ReflectClass claxx) {
        // Do we need this at all???
        // Check if this method is ever called
        return i_handler.canHold(claxx);
    }
    
    public ReflectClass classReflector(){
    	return i_handler.classReflector();
    }
    
    public void deleteEmbedded(MarshallerFamily mf, YapWriter a_bytes) {
        if(mf._primitive.useNormalClassRead()){
            super.deleteEmbedded(mf, a_bytes);
            return;
        }
        
        // Do nothing here, we should be in the payload area
        // no action to be taken.
    }

    
    public void deleteEmbedded1(MarshallerFamily mf, YapWriter a_bytes, int a_id) {
        
        if(i_handler instanceof YapArray){
            YapArray ya = (YapArray)i_handler;
            
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
            
            if(ya.i_isPrimitive){
                ya.deletePrimitiveEmbedded(a_bytes, this);
                a_bytes.slotDelete();
                return;
            }
        }
        
       if(i_handler instanceof YapClassAny){
            // Any-In-Any: Ignore delete 
            a_bytes.incrementOffset(i_handler.linkLength());
        }else{
            i_handler.deleteEmbedded(mf, a_bytes);
        }
		
		// TODO: Was this freeing call necessary? 
		//   free(a_bytes.getTransaction(), a_id, a_bytes.getAddress(), a_bytes.getLength());
		
		free(a_bytes, a_id);
			
    }

    void deleteMembers(MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes, int a_type, boolean isUpdate) {
        if (a_type == YapConst.TYPE_ARRAY) {
            new YapArray(a_bytes.getStream(),this, true).deletePrimitiveEmbedded(a_bytes, this);
        } else if (a_type == YapConst.TYPE_NARRAY) {
            new YapArrayN(a_bytes.getStream(),this, true).deletePrimitiveEmbedded(a_bytes, this);
        }
    }
    
	final void free(Transaction a_trans, int a_id, int a_address, int a_length) {
        a_trans.slotFreePointerOnCommit(a_id, a_address, a_length);
	}
	
	final void free(YapWriter a_bytes, int a_id) {
          a_bytes.getTransaction().slotFreePointerOnCommit(a_id, a_bytes.getAddress(), a_bytes.getLength());
	}
    
	boolean hasIndex() {
	    return false;
	}

    Object instantiate(YapObject a_yapObject, Object a_object, MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes, boolean a_addToIDTree) {
        if (a_object == null) {
            try {
                a_object = i_handler.read(mf, a_bytes, true);
            } catch (CorruptionException ce) {
                return null;
            }
            a_yapObject.setObjectWeak(a_bytes.getStream(), a_object);
        }
        a_yapObject.setStateClean();
        return a_object;
    }
    
    Object instantiateTransient(YapObject a_yapObject, Object a_object, MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes) {
        try {
            return i_handler.read(mf, a_bytes, true);
        } catch (CorruptionException ce) {
            return null;
        }
    }

    void instantiateFields(YapObject a_yapObject, Object a_onObject, MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes) {
        Object obj = null;
        try {
            obj = i_handler.read(mf, a_bytes, true);
        } catch (CorruptionException ce) {
            obj = null;
        }
        if (obj != null) {
            i_handler.copyValue(obj, a_onObject);
        }
    }

    public boolean isArray() {
        return i_id == YapHandlers.ANY_ARRAY_ID || i_id == YapHandlers.ANY_ARRAY_N_ID;
    }
    
    public boolean isPrimitive(){
        return true;
    }
    
    public int isSecondClass(){
        return YapConst.UNKNOWN;
    }
    
	boolean isStrongTyped(){
		return false;
	}
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        i_handler.calculateLengths(trans, header, topLevel, obj, withIndirection);
    }
    
    public YapComparable prepareComparison(Object a_constraint) {
        i_handler.prepareComparison(a_constraint);
        return i_handler;
    }
    
    public final ReflectClass primitiveClassReflector(){
        return i_handler.primitiveClassReflector();
    }
    
    public Object read(MarshallerFamily mf, YapWriter a_bytes, boolean redirect) throws CorruptionException{
        if(mf._primitive.useNormalClassRead()){
            return super.read(mf, a_bytes, redirect);
        }
        return i_handler.read(mf, a_bytes, false);
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, YapReader[] a_bytes) {
        if (isArray()) {
            return i_handler;
        }
        return null;
    }
    
    public Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, YapReader reader, boolean toArray) throws CorruptionException{
        if(mf._primitive.useNormalClassRead()){
            return super.readQuery(trans, mf, withRedirection, reader, toArray);
        }
        return i_handler.readQuery(trans, mf, withRedirection, reader, toArray);
    }

    
    public QCandidate readSubCandidate(MarshallerFamily mf, YapReader reader, QCandidates candidates, boolean withIndirection) {
        return i_handler.readSubCandidate(mf, reader, candidates, withIndirection);
    } 

    void removeFromIndex(Transaction ta, int id) {
        // do nothing
    }
    
    public boolean supportsIndex() {
        return true;
    }

    public final boolean writeObjectBegin() {
        return false;
    }
    
    public Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel, YapWriter a_bytes, boolean withIndirection, boolean restoreLinkOffset) {
        mf._primitive.writeNew(a_bytes.getTransaction(), this, a_object, topLevel, a_bytes, withIndirection, restoreLinkOffset);
        return a_object;
    }
    
    public String toString(){
        return "Wraps " + i_handler.toString() + " in YapClassPrimitive";
    }

    public void defrag(MarshallerFamily mf, YapReader source, YapReader target, IDMapping mapping) {
    	Debug.log("BEFORE PRIMITIVE "+source._offset+","+target._offset);
        if(mf._primitive.useNormalClassRead()){
            super.defrag(mf,source,target,mapping);
        }
        else {
            i_handler.defrag(mf, source, target, mapping);
        }
    	Debug.log("AFTER PRIMITIVE "+source._offset+","+target._offset);
    }
}
