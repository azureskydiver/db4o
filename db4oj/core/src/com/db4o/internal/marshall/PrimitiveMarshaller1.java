/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.internal.*;


public class PrimitiveMarshaller1 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    public int writeNew(Transaction trans, PrimitiveFieldHandler yapClassPrimitive, Object obj, boolean topLevel, StatefulBuffer writer, boolean withIndirection, boolean restoreLinkOffset){
        if(obj != null){
            TypeHandler4 handler = yapClassPrimitive.i_handler;
            handler.write(_family, obj, topLevel, writer, withIndirection, restoreLinkOffset);
        }
        return 0;
    }
    
    public Date readDate(Buffer bytes){
		return new Date(bytes.readLong());
	}
    
    public Object readInteger(Buffer bytes) {
    	return new Integer(bytes.readInt());
    }
    
    public Object readFloat(Buffer bytes) {
    	return PrimitiveMarshaller0.unmarshallFloat(bytes);
    }

	public Object readDouble(Buffer buffer) {
		return PrimitiveMarshaller0.unmarshalDouble(buffer);
	}

	public Object readLong(Buffer buffer) {
		return new Long(buffer.readLong());
	}

	public Object readShort(Buffer buffer) {
		return new Short(PrimitiveMarshaller0.unmarshallShort(buffer));
	}

}
