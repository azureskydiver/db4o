/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.handlers;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;



public final class YDate extends YLong {
	
	private static final Date PROTO = new Date(0);
    
    public YDate(ObjectContainerBase stream) {
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
	
	public Object primitiveNull(){
		return null;
	}
	
	public Object read(MarshallerFamily mf, StatefulBuffer writer, boolean redirect)
			throws CorruptionException {
		return mf._primitive.readDate(writer);
	}
	
	Object read1(Buffer a_bytes) {
		return primitiveMarshaller().readDate(a_bytes);
	}

	private PrimitiveMarshaller primitiveMarshaller() {
		return MarshallerFamily.current()._primitive;
	}

	public void write(Object a_object, Buffer a_bytes){
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
	
	public static String now(){
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
