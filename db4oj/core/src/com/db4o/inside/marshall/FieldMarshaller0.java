/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.handlers.*;

/**
 * @exclude
 */
public class FieldMarshaller0 implements FieldMarshaller {

    public int marshalledLength(ObjectContainerBase stream, FieldMetadata field) {
        int len = stream.stringIO().shortLength(field.getName());
        if(field.needsArrayAndPrimitiveInfo()){
            len += 1;
        }
        if(field.needsHandlerId()){
            len += YapConst.ID_LENGTH;
        }
        return len;
    }
    
    public RawFieldSpec readSpec(ObjectContainerBase stream, Buffer reader) {
        
        String name = null;
        
        try {
            name = StringMarshaller.readShort(stream, reader);
        } catch (CorruptionException ce) {
            return null;
        }
        
        if (name.indexOf(YapConst.VIRTUAL_FIELD_PREFIX) == 0) {
            VirtualFieldMetadata[] virtuals = stream.i_handlers.i_virtualFields;
            for (int i = 0; i < virtuals.length; i++) {
                if (name.equals(virtuals[i].getName())) {
                    return new RawFieldSpec(name);
                }
            }
        }
        int handlerID = reader.readInt();
        byte attribs=reader.readByte();
        
        return new RawFieldSpec(name,handlerID,attribs);
    }

    
    public final FieldMetadata read(ObjectContainerBase stream, FieldMetadata field, Buffer reader) {
    	RawFieldSpec spec=readSpec(stream, reader);
    	return fromSpec(spec, stream, field);
    }
    
    protected FieldMetadata fromSpec(RawFieldSpec spec,ObjectContainerBase stream, FieldMetadata field) {
    	if(spec==null) {
    		return field;
    	}
    	String name=spec.name();
        if (spec.isVirtual()) {
            VirtualFieldMetadata[] virtuals = stream.i_handlers.i_virtualFields;
            for (int i = 0; i < virtuals.length; i++) {
                if (name.equals(virtuals[i].getName())) {
                    return virtuals[i];
                }
            }
        }
        
        field.init(field.getParentYapClass(), name);
        field.init(spec.handlerID(), spec.isPrimitive(), spec.isArray(), spec.isNArray());
        field.loadHandler(stream);
        field.alive();
        
        return field;
    }


    public void write(Transaction trans, ClassMetadata clazz, FieldMetadata field, Buffer writer) {
        
        field.alive();
        
        writer.writeShortString(trans, field.getName());
        if(field.isVirtual()){
            return;
        }
        
        TypeHandler4 handler = field.getHandler();
        
        if (handler instanceof ClassMetadata) {
            
            // TODO: ensure there is a test case, to make this happen 
            if (handler.getID() == 0) {
                trans.stream().needsUpdate(clazz);
            }
        }
        int handlerID = 0;
        try {
            // The handler can be null and it can fail to
            // deliver the ID.

            // In this case the field is dead.

            handlerID = handler.getID();
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        if (handlerID == 0) {
            handlerID = field.getHandlerID();
        }
        writer.writeInt(handlerID);
        YapBit yb = new YapBit(0);
        yb.set(handler instanceof MultidimensionalArrayHandler); // keep the order
        yb.set(handler instanceof ArrayHandler);
        yb.set(field.isPrimitive());
        writer.append(yb.getByte());
    }


	public void defrag(ClassMetadata yapClass, FieldMetadata yapField, YapStringIO sio,ReaderPair readers) throws CorruptionException {
		readers.readShortString(sio);
        if (yapField.isVirtual()) {
        	return;
        }
        // handler ID
        readers.copyID();
        // skip primitive/array/narray attributes
        readers.incrementOffset(1);
	}
}
