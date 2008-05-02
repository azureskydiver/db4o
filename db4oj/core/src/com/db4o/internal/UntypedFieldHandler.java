/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


public class UntypedFieldHandler extends ClassMetadata implements BuiltinTypeHandler, FieldHandler{
    
    private static final int HASHCODE = 1003303143;
    
	public UntypedFieldHandler(ObjectContainerBase container){
		super(container, container._handlers.ICLASS_OBJECT);
	}

	public void cascadeActivation(
		Transaction trans,
		Object onObject,
		ActivationDepth depth) {
	    
	    TypeHandler4 typeHandler = typeHandlerForObject(onObject, true);
        if (typeHandler instanceof CascadingTypeHandler) {
            ((CascadingTypeHandler)typeHandler).cascadeActivation(trans, onObject, depth);
        }
	}

    private HandlerRegistry handlerRegistry() {
        return container()._handlers;
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
        TypeHandler4 typeHandler = configuredHandler(container().classMetadataForId(classMetadataID).classReflector());
        if(typeHandler == null){
        	typeHandler = ((ObjectContainerBase)context.objectContainer()).typeHandlerForId(classMetadataID);
        }
        if(typeHandler != null){
            typeHandler.delete(context);
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
    
	public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, ByteArrayBuffer[] a_bytes) {
        return mf._untyped.readArrayHandler(a_trans, a_bytes);
	}
    
    public ObjectID readObjectID(InternalReadContext context){
        int payloadOffset = context.readInt();
        if(payloadOffset == 0){
            return ObjectID.IS_NULL;
        }
        TypeHandler4 typeHandler = readTypeHandler(context, payloadOffset);
        if(typeHandler == null){
            return ObjectID.IS_NULL;
        }
        seekSecondaryOffset(context, typeHandler);
        if(typeHandler instanceof ReadsObjectIds){
            return ((ReadsObjectIds)typeHandler).readObjectID(context);
        }
        return ObjectID.NOT_POSSIBLE;
    }
    
    public void defragment(DefragmentContext context) {
        int payLoadOffSet = context.readInt();
        if(payLoadOffSet == 0){
            return;
        }
        int linkOffSet = context.offset();
        context.seek(payLoadOffSet);
        
        int typeHandlerId = context.copyIDReturnOriginalID();
		TypeHandler4 typeHandler = context.typeHandlerForId(typeHandlerId);
		if(typeHandler != null){
			seekSecondaryOffset(context, typeHandler);
		    context.correctHandlerVersion(typeHandler).defragment(context);
		}
        context.seek(linkOffSet);
    }

    private TypeHandler4 readTypeHandler(InternalReadContext context, int payloadOffset) {
        context.seek(payloadOffset);
        TypeHandler4 typeHandler = container().typeHandlerForId(context.readInt());
        if(NullableArrayHandling.enabled()){
        	typeHandler = container().handlers().correctHandlerVersion(typeHandler, context.handlerVersion());
        }
        return typeHandler;
    }

    /**
     * @param buffer
     * @param typeHandler
     */
    protected void seekSecondaryOffset(ReadBuffer buffer, TypeHandler4 typeHandler) {
        // do nothing, no longer needed in current implementation.
    }

    protected boolean isPrimitiveArray(TypeHandler4 classMetadata) {
        return classMetadata instanceof PrimitiveFieldHandler && ((PrimitiveFieldHandler)classMetadata).isArray();
    }

    public Object read(ReadContext readContext) {
        InternalReadContext context = (InternalReadContext) readContext;
        int payloadOffset = context.readInt();
        if(payloadOffset == 0){
            return null;
        }
        int savedOffSet = context.offset();
        TypeHandler4 typeHandler = readTypeHandler(context, payloadOffset);
        if(typeHandler == null){
            context.seek(savedOffSet);
            return null;
        }
        seekSecondaryOffset(context, typeHandler);
        Object obj = context.readAtCurrentSeekPosition(typeHandler);
        context.seek(savedOffSet);
        return obj;
    }
    
    public TypeHandler4 readTypeHandlerRestoreOffset(InternalReadContext context){
        int savedOffset = context.offset();
        int payloadOffset = context.readInt();
        TypeHandler4 typeHandler = payloadOffset == 0 ? null : readTypeHandler(context, payloadOffset);  
        context.seek(savedOffset);
        return typeHandler;
    }

    public void write(WriteContext context, Object obj) {
        if(obj == null){
            context.writeInt(0);
            return;
        }
        MarshallingContext marshallingContext = (MarshallingContext) context;
        TypeHandler4 typeHandler = typeHandlerForObject(obj);
        if(typeHandler == null){
            context.writeInt(0);
            return;
        }
        int id = handlerRegistry().typeHandlerID(typeHandler);
        MarshallingContextState state = marshallingContext.currentState();
        marshallingContext.createChildBuffer(false, false);
        context.writeInt(id);
        if(!isPrimitiveArray(typeHandler)){
            marshallingContext.doNotIndirectWrites();
        }
        writeObject(context, typeHandler, obj);
        marshallingContext.restoreState(state);
    }

    private void writeObject(WriteContext context, TypeHandler4 typeHandler, Object obj) {
        if(FieldMetadata.useDedicatedSlot(context, typeHandler)){
            context.writeObject(obj);
        }else {
            typeHandler.write(context, obj);
        }
    }

    public TypeHandler4 typeHandlerForObject(Object obj) {
    	return typeHandlerForObject(obj, false);
    }

    public TypeHandler4 typeHandlerForObject(Object obj, boolean lookupRegistered) {
        ReflectClass claxx = reflector().forObject(obj);
        if(claxx.isArray()){
            return handlerRegistry().untypedArrayHandler(claxx);
        }
        if(lookupRegistered) {
	        TypeHandler4 configuredHandler = configuredHandler(claxx);
	        if(configuredHandler != null){
	            return configuredHandler;
	        }
        }
        return container().typeHandlerForReflectClass(claxx);
    }

	private TypeHandler4 configuredHandler(ReflectClass claxx) {
		TypeHandler4 configuredHandler =
		    container().configImpl().typeHandlerForClass(claxx, HandlerRegistry.HANDLER_VERSION);
		return configuredHandler;
	}

    public ReflectClass classReflector(Reflector reflector) {
        return super.classReflector();
    }
    
    public boolean equals(Object obj) {
        return obj instanceof UntypedFieldHandler;
    }
    
    public int hashCode() {
        return HASHCODE;
    }

}
