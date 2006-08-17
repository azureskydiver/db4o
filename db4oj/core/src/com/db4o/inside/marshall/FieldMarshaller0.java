/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

/**
 * @exclude
 */
public class FieldMarshaller0 extends FieldMarshaller {

    public int marshalledLength(YapStream stream, YapField field) {
        int len = stream.stringIO().shortLength(field.getName());
        if(field.needsArrayAndPrimitiveInfo()){
            len += 1;
        }
        if(field.needsHandlerId()){
            len += YapConst.ID_LENGTH;
        }
        return len;
    }
    

    public YapField read(YapStream stream, YapField field, YapReader reader) {
        
        String name = null;
        
        try {
            name = StringMarshaller.readShort(stream, reader);
        } catch (CorruptionException ce) {
            return field;
        }
        
        if (name.indexOf(YapConst.VIRTUAL_FIELD_PREFIX) == 0) {
            YapFieldVirtual[] virtuals = stream.i_handlers.i_virtualFields;
            for (int i = 0; i < virtuals.length; i++) {
                if (name.equals(virtuals[i].getName())) {
                    return virtuals[i];
                }
            }
        }
        field.init(field.getParentYapClass(), name, 0);
        int handlerID = reader.readInt();
        YapBit yb = new YapBit(reader.readByte());
        boolean isPrimitive = yb.get();
        boolean isArray = yb.get();
        boolean isNArray = yb.get();
        
        field.init(handlerID, isPrimitive, isArray, isNArray);
        field.loadHandler(stream);
        
        return field;
    }


    public void write(Transaction trans, YapClass clazz, YapField field, YapReader writer) {
        
        field.alive();
        
        writer.writeShortString(trans, field.getName());
        if(field.isVirtual()){
            return;
        }
        
        TypeHandler4 handler = field.getHandler();
        
        if (handler instanceof YapClass) {
            
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
        yb.set(handler instanceof YapArrayN); // keep the order
        yb.set(handler instanceof YapArray);
        yb.set(field.isPrimitive());
        writer.append(yb.getByte());
    }

}
