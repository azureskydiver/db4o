/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


public class UntypedFieldHandler extends ClassMetadata implements BuiltinTypeHandler{
    
	public UntypedFieldHandler(ObjectContainerBase container){
		super(container, container._handlers.ICLASS_OBJECT);
	}
	

	public void cascadeActivation(
		Transaction trans,
		Object onObject,
		ActivationDepth depth) {
		ClassMetadata classMetadata = forObject(trans, onObject, false);
		if (classMetadata != null) {
			classMetadata.cascadeActivation(trans, onObject, depth);
		}
	}
    
	public void delete(DeleteContext context) throws Db4oIOException {
        int payLoadOffset = context.readInt();
        if(context.isLegacyHandlerVersion()){
        	context.defragmentRecommended();
        	return;
        }
        if (payLoadOffset <= 0) {
        	return;
        }
        int linkOffset = context.offset();
        context.seek(payLoadOffset);
        int classMetadataID = context.readInt();
        ClassMetadata classMetadata = 
        	((ObjectContainerBase)context.objectContainer()).classMetadataForId(classMetadataID);
        if(classMetadata != null){
            classMetadata.delete(context);
        }
        context.seek(linkOffset);
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
    
	public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, BufferImpl[] a_bytes) {
        return mf._untyped.readArrayHandler(a_trans, a_bytes);
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
    
    public void defragment(DefragmentContext context) {
        int payLoadOffSet = context.readInt();
        if(payLoadOffSet == 0){
            return;
        }
        int linkOffSet = context.offset();
        context.seek(payLoadOffSet);
        
        int classMetadataID = context.copyIDReturnOriginalID();
        
        ClassMetadata classMetadata = context.classMetadataForId(classMetadataID);
        if(classMetadata != null){
            classMetadata.defragment(context);
        }
        
        context.seek(linkOffSet);
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
