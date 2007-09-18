/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;


public class UntypedFieldHandler extends ClassMetadata implements BuiltinTypeHandler{
    
	public UntypedFieldHandler(ObjectContainerBase container){
		super(container, container._handlers.ICLASS_OBJECT);
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
		return Handlers4.UNTYPED_ID;
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
    
    public ObjectID readObjectID(InternalReadContext context){
        int payloadOffset = context.readInt();
        if(payloadOffset == 0){
            return ObjectID.IS_NULL;
        }
        ClassMetadata classMetadata = readClassMetadata(context, payloadOffset);
        if(classMetadata == null){
            return ObjectID.IS_NULL;
        }
        seekSecondaryOffset(context, classMetadata);
        return classMetadata.readObjectID(context);
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection) {
        if(mf._untyped.useNormalClassRead()){
            return super.readSubCandidate(mf, reader, candidates, withIndirection);
        }
        return mf._untyped.readSubCandidate(reader, candidates, withIndirection);
    } 
	
    public void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect) {
        if(mf._untyped.useNormalClassRead()){
            super.defrag(mf,readers, redirect);
        }
    	mf._untyped.defrag(readers);
    }
    
    private boolean isArray(TypeHandler4 handler){
        if(handler instanceof ClassMetadata){
            return ((ClassMetadata)handler).isArray();
        }
        return handler instanceof ArrayHandler;
    }
    
    public Object read(ReadContext readContext) {
        InternalReadContext context = (InternalReadContext) readContext;
        int payloadOffset = context.readInt();
        if(payloadOffset == 0){
            return null;
        }
        int savedOffSet = context.offset();
        ClassMetadata classMetadata = readClassMetadata(context, payloadOffset);
        if(classMetadata == null){
            context.seek(savedOffSet);
            return null;
        }
        seekSecondaryOffset(context, classMetadata);
        Object obj = classMetadata.read(context);
        context.seek(savedOffSet);
        return obj;
    }


    private ClassMetadata readClassMetadata(InternalReadContext context, int payloadOffset) {
        context.seek(payloadOffset);
        ClassMetadata classMetadata = container().classMetadataForId(context.readInt());
        return classMetadata;
    }

    private void seekSecondaryOffset(InternalReadContext context, ClassMetadata classMetadata) {
        if(classMetadata instanceof PrimitiveFieldHandler && classMetadata.isArray()){
            // unnecessary secondary offset, consistent with old format
            context.seek(context.readInt());
        }
    }

    public void write(WriteContext context, Object obj) {
        if(obj == null){
            context.writeInt(0);
            return;
        }
        MarshallingContext marshallingContext = (MarshallingContext) context;
        TypeHandler4 handler =   ClassMetadata.forObject(context.transaction(), obj, true);
        if(handler == null){
            context.writeInt(0);
            return;
        }
        MarshallingContextState state = marshallingContext.currentState();
        marshallingContext.createChildBuffer(false, false);
        int id = marshallingContext.container().handlers().handlerID(handler);
        context.writeInt(id);
        if(isArray(handler)){
            // TODO: This indirection is unneccessary, but it is required by the 
            // current old reading format. 
            // Remove in the next version of UntypedFieldHandler  
            marshallingContext.prepareIndirectionOfSecondWrite();
        }else{
            marshallingContext.doNotIndirectWrites();
        }
        handler.write(context, obj);
        marshallingContext.restoreState(state);
    }

}
