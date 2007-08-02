/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.Deploy;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


public class PrimitiveMarshaller0 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return true;
    }
    
    public int writeNew(Transaction trans, PrimitiveFieldHandler yapClassPrimitive, Object obj, boolean topLevel, StatefulBuffer parentWriter, boolean withIndirection, boolean restoreLinkOffset){
        
        int id = 0;
        
        if(obj != null){
            
            TypeHandler4 handler = yapClassPrimitive.i_handler;
        
            ObjectContainerBase stream = trans.container();
            id = stream.newUserObject();
            Slot slot = new Slot(-1, objectLength(handler));
            if(! stream.isClient()){
                slot = ((LocalTransaction)trans).file().getSlot(slot.length()); 
            }
            Pointer4 pointer = new Pointer4(id, slot);
            trans.setPointer(pointer);
            
            StatefulBuffer writer = new StatefulBuffer(trans, pointer);
            if (Deploy.debug) {
                writer.writeBegin(Const4.YAPOBJECT);
            }
            writer.writeInt(yapClassPrimitive.getID());
            
            handler.write(_family, obj, false, writer, true, false);
            
            writer.writeEnd();
            stream.writeNew(yapClassPrimitive, writer);
        }
        
        if(parentWriter != null){
            parentWriter.writeInt(id);
        }
        
        return id;
    }
    
    public Date readDate(Buffer bytes) {
		final long value = bytes.readLong();
		if (value == Long.MAX_VALUE) {
			return null;
		}
		return new Date(value);
	}
    
    public Object readInteger(Buffer bytes) {
		final int value = bytes.readInt();
		if (value == Integer.MAX_VALUE) {
			return null;
		}
		return new Integer(value);
	}

	public Object readFloat(Buffer bytes) {
		Float value = unmarshallFloat(bytes);
		if (value.isNaN()) {
			return null;
		}
		return value;
	}
	
	public Object readDouble(Buffer buffer) {
		Double value = unmarshalDouble(buffer);
		if (value.isNaN()) {
			return null;
		}
		return value;
	}	

	public Object readLong(Buffer buffer) {
		long value = buffer.readLong();
		if (value == Long.MAX_VALUE) {
			return null;
		}
		return new Long(value);
	}
	
	public Object readShort(Buffer buffer) {
		short value = unmarshallShort(buffer);
		if (value == Short.MAX_VALUE) {
			return null;
		}
		return new Short(value);
	}
	
	public static Double unmarshalDouble(Buffer buffer) {
		return new Double(Platform4.longToDouble(buffer.readLong()));
	}

	public static Float unmarshallFloat(Buffer buffer) {
		return new Float(Float.intBitsToFloat(buffer.readInt()));
	}	
	
	public static short unmarshallShort(Buffer buffer){
		int ret = 0;
		if (Deploy.debug){
			buffer.readBegin(Const4.YAPSHORT);
		}
		for (int i = 0; i < Const4.SHORT_BYTES; i++){
			ret = (ret << 8) + (buffer._buffer[buffer._offset++] & 0xff);
		}
		if (Deploy.debug){
			buffer.readEnd();
		}
		return (short)ret;
	}
}
