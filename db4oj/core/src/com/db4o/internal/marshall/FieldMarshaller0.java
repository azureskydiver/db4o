/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.io.IOException;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;

/**
 * @exclude
 */
public class FieldMarshaller0 extends AbstractFieldMarshaller {

    public int marshalledLength(ObjectContainerBase stream, ClassAspect aspect) {
        int len = stream.stringIO().shortLength(aspect.getName());
        if(aspect instanceof FieldMetadata){
            FieldMetadata field = (FieldMetadata) aspect;
            if(field.needsArrayAndPrimitiveInfo()){
                len += 1;
            }
            if(field.needsHandlerId()){
                len += Const4.ID_LENGTH;
            }
        }
        return len;
    }
    
    protected RawFieldSpec readSpec(AspectType aspectType, ObjectContainerBase stream, ByteArrayBuffer reader) {
        String name = StringHandler.readStringNoDebug(stream.transaction().context(), reader);
        if(! aspectType.isFieldMetadata()){
            return new RawFieldSpec(aspectType, name);
        }
        if (name.indexOf(Const4.VIRTUAL_FIELD_PREFIX) == 0) {
        	if(stream._handlers.virtualFieldByName(name)!=null) {
                return new RawFieldSpec(aspectType, name);
        	}
        }
        int handlerID = reader.readInt();
        byte attribs=reader.readByte();
        return new RawFieldSpec(aspectType, name,handlerID,attribs);
    }
    
    public final FieldMetadata read(ObjectContainerBase stream, FieldMetadata field, ByteArrayBuffer reader) {
    	RawFieldSpec spec=readSpec(stream, reader);
    	return fromSpec(spec, stream, field);
    }
    
    protected FieldMetadata fromSpec(RawFieldSpec spec,ObjectContainerBase stream, FieldMetadata field) {
    	if(spec==null) {
    		return field;
    	}
        String name=spec.name();
    	if(! spec.isFieldMetadata()){
    	    field.init(field.containingClass(), name);
    	    return field;
    	}
        if (spec.isVirtual()) {
        	return stream._handlers.virtualFieldByName(name);
        }
        
        field.init(field.containingClass(), name);
        field.init(spec.handlerID(), spec.isPrimitive(), spec.isArray(), spec.isNArray());
        field.loadHandlerById(stream);
        field.alive();
        
        return field;
    }


    public void write(Transaction trans, ClassMetadata clazz, ClassAspect aspect, ByteArrayBuffer writer) {
        
        writer.writeShortString(trans, aspect.getName());
        
        if(! (aspect instanceof FieldMetadata)){
            return;
        }
        
        FieldMetadata field = (FieldMetadata) aspect;
        
        field.alive();
        
        if(field.isVirtual()){
            return;
        }
        
        TypeHandler4 handler = field.getHandler();
        
        if (handler instanceof ClassMetadata) {
            
            // TODO: ensure there is a test case, to make this happen 
            if ( ((ClassMetadata)handler).getID() == 0) {
                trans.container().needsUpdate(clazz);
            }
        }
        writer.writeInt(field.handlerID());
        BitMap4 bitmap = new BitMap4(3);
        bitmap.set(0, field.isPrimitive());
        bitmap.set(1, handler instanceof ArrayHandler);
        bitmap.set(2, handler instanceof MultidimensionalArrayHandler); // keep the order
        writer.writeByte(bitmap.getByte(0));
    }


	public void defrag(ClassMetadata classMetadata, ClassAspect aspect, LatinStringIO sio,
        DefragmentContextImpl context) throws CorruptionException, IOException {
        context.incrementStringOffset(sio);
        if (!(aspect instanceof FieldMetadata)) {
            return;
        }
        if (((FieldMetadata) aspect).isVirtual()) {
            return;
        }
        // handler ID
        context.copyID();
        // skip primitive/array/narray attributes
        context.incrementOffset(1);
    }
}
