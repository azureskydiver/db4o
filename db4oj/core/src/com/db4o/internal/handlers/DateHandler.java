/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;
import com.db4o.reflect.*;

public final class DateHandler extends LongHandler {
	
	private static final Date PROTO = new Date(0);
    
    public DateHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
        return Handlers4.handlerCanHold(this, claxx) ? obj : No4.INSTANCE;
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

	public void write(Object a_object, Buffer a_bytes){
        // TODO: This is a temporary fix to prevent exceptions with
        // Marshaller.LEGACY.  
        if(a_object == null){
            a_object = new Date(0);
        }
		a_bytes.writeLong(((Date)a_object).getTime());
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

    public Object read(ReadContext context) {
        long milliseconds = ((Long)super.read(context)).longValue();
        return new Date(milliseconds);
    }

    public void write(WriteContext context, Object obj) {
        long milliseconds = ((Date)obj).getTime();
        super.write(context, new Long(milliseconds));
    }	
}
