/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;


public final class UntypedFieldHandler extends ClassMetadata {
    
	public UntypedFieldHandler(ObjectContainerBase stream){
		super(stream, stream._handlers.ICLASS_OBJECT);
	}

	public void cascadeActivation(
		Transaction a_trans,
		Object a_object,
		int a_depth,
		boolean a_activate) {
		ClassMetadata yc = forObject(a_trans, a_object, false);
		if (yc != null) {
			yc.cascadeActivation(a_trans, a_object, a_depth, a_activate);
		}
	}
    
	public void deleteEmbedded(MarshallerFamily mf, StatefulBuffer reader) throws Db4oIOException {
        mf._untyped.deleteEmbedded(reader);
	}
	
	public int getID() {
		return 11;
	}

	public boolean hasField(ObjectContainerBase a_stream, String a_path) {
		return a_stream.classCollection().fieldExists(a_path);
	}
	
	public boolean hasClassIndex() {
	    return false;
	}
    
	public boolean holdsAnyClass() {
		return true;
	}
    
    public boolean isStrongTyped(){
		return false;
	}
    
    public Object read(MarshallerFamily mf, StatefulBuffer a_bytes, boolean redirect) throws CorruptionException, Db4oIOException {
        if(mf._untyped.useNormalClassRead()){
            return super.read(mf, a_bytes, redirect);
        }
        return mf._untyped.read(a_bytes);
    }

	public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes) {
        return mf._untyped.readArrayHandler(a_trans, a_bytes);
	}
    
    public Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, Buffer reader, boolean toArray) throws CorruptionException, Db4oIOException {
        if(mf._untyped.useNormalClassRead()){
            return super.readQuery(trans, mf, withRedirection, reader, toArray);
        }
        return mf._untyped.readQuery(trans, reader, toArray);
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection) {
        if(mf._untyped.useNormalClassRead()){
            return super.readSubCandidate(mf, reader, candidates, withIndirection);
        }
        return mf._untyped.readSubCandidate(reader, candidates, withIndirection);
    } 
	
    public Object write(MarshallerFamily mf, Object obj, boolean topLevel, StatefulBuffer writer, boolean withIndirection, boolean restoreLinkeOffset) {
        return mf._untyped.writeNew(obj, restoreLinkeOffset, writer);
    }

    public void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect) {
        if(mf._untyped.useNormalClassRead()){
            super.defrag(mf,readers, redirect);
        }
    	mf._untyped.defrag(readers);
    }
    
    public Object read(ReadContext context) {
        throw new NotImplementedException();
    }

    public void write(WriteContext context, Object obj) {
        ((MarshallingContext)context).writeAny(obj);
    }

}
