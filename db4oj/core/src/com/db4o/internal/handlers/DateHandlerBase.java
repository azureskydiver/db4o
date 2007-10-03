/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.handlers;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;

/**
 * Shared (java/.net) logic for Date handling.
 */
public abstract class DateHandlerBase extends LongHandler {
	
	public DateHandlerBase(ObjectContainerBase container) {
        super(container);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
        return Handlers4.handlerCanHold(this, claxx) ? obj : No4.INSTANCE;
    }

	public abstract Object copyValue(Object from, Object to);	
	public abstract Object defaultValue();
	public abstract Object primitiveNull();
	public abstract Object nullRepresentationInUntypedArrays();
	
	protected Class primitiveJavaClass() {
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
