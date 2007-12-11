/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.internal.*;


public class PrimitiveMarshaller1 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    public Date readDate(BufferImpl bytes){
		return new Date(bytes.readLong());
	}
    
    public Object readInteger(BufferImpl bytes) {
    	return new Integer(bytes.readInt());
    }
    
    public Object readFloat(BufferImpl bytes) {
    	return PrimitiveMarshaller0.unmarshallFloat(bytes);
    }

	public Object readDouble(BufferImpl buffer) {
		return PrimitiveMarshaller0.unmarshalDouble(buffer);
	}

	public Object readLong(BufferImpl buffer) {
		return new Long(buffer.readLong());
	}

	public Object readShort(BufferImpl buffer) {
		return new Short(PrimitiveMarshaller0.unmarshallShort(buffer));
	}

}
