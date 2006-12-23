/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.inside.marshall.MarshallerFamily;
import com.db4o.reflect.*;


final class YDate extends YLong
{
	
	private static final Date PROTO = new Date(0);
    
    public YDate(YapStream stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
        return canHold(claxx) ? obj : No4.INSTANCE;
    }

	public void copyValue(Object a_from, Object a_to){
		try{
			((Date)a_to).setTime(((Date)a_from).getTime());
		}catch(Exception e){
		}
	}
	
	public Object defaultValue(){
		return PROTO;
	}
	
	public int getID(){
		return 10;
	}
    
    public boolean indexNullHandling() {
        return true;
    }
	
	protected Class primitiveJavaClass(){
		return null;
	}
	
	Object primitiveNull(){
		return null;
	}
	
	Object read1(YapReader a_bytes){
		return new Date(readLong(a_bytes));
	}
	
	public Object read(MarshallerFamily mf, YapWriter writer, boolean redirect)
			throws CorruptionException {
		if (mf.converterVersion() == 0) {
			return readOldVersion(writer);
		}
		return super.read(mf, writer, redirect);
	}
	
	private Object readOldVersion(YapWriter a_bytes) {
		final long longValue = readLong(a_bytes);
		if (longValue == Long.MAX_VALUE) {
			return null;
		}
		return new Date(longValue);
	}

	public void write(Object a_object, YapReader a_bytes){
        // TODO: This is a temporary fix to prevent exceptions with
        // Marshaller.LEGACY.  
        if(a_object == null){
            a_object = new Date(0);
        }
		a_bytes.writeLong(((Date)a_object).getTime());
	}
    
    public Object current1(){
        return new Date(currentLong());
    }
	
	static String now(){
		return Platform4.format(new Date(), true);
	}
	
	long val(Object obj){
		return ((Date)obj).getTime();
	}
	
	boolean isEqual1(Object obj){
		return obj instanceof Date && val(obj) == currentLong();
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Date && val(obj) > currentLong();
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Date && val(obj) < currentLong();
	}
	
	
}
