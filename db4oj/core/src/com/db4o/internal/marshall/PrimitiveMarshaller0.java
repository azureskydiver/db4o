/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.Deploy;
import com.db4o.internal.*;


public class PrimitiveMarshaller0 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return true;
    }
    
    public Date readDate(BufferImpl bytes) {
		final long value = bytes.readLong();
		if (value == Long.MAX_VALUE) {
			return MarshallingConstants0.NULL_DATE;
		}
		return new Date(value);
	}
    
    public Object readInteger(BufferImpl bytes) {
		final int value = bytes.readInt();
		if (value == Integer.MAX_VALUE) {
			return null;
		}
		return new Integer(value);
	}

	public Object readFloat(BufferImpl bytes) {
		Float value = unmarshallFloat(bytes);
		if (value.isNaN()) {
			return null;
		}
		return value;
	}
	
	public Object readDouble(BufferImpl buffer) {
		Double value = unmarshalDouble(buffer);
		if (value.isNaN()) {
			return null;
		}
		return value;
	}	

	public Object readLong(BufferImpl buffer) {
		long value = buffer.readLong();
		if (value == Long.MAX_VALUE) {
			return null;
		}
		return new Long(value);
	}
	
	public Object readShort(BufferImpl buffer) {
		short value = unmarshallShort(buffer);
		if (value == Short.MAX_VALUE) {
			return null;
		}
		return new Short(value);
	}
	
	public static Double unmarshalDouble(BufferImpl buffer) {
		return new Double(Platform4.longToDouble(buffer.readLong()));
	}

	public static Float unmarshallFloat(BufferImpl buffer) {
		return new Float(Float.intBitsToFloat(buffer.readInt()));
	}	
	
	public static short unmarshallShort(BufferImpl buffer){
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
