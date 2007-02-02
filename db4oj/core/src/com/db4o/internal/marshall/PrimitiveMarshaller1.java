/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


public class PrimitiveMarshaller1 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    public int writeNew(Transaction trans, PrimitiveFieldHandler yapClassPrimitive, Object obj, boolean topLevel, StatefulBuffer writer, boolean withIndirection, boolean restoreLinkOffset){
        if(obj != null){
            TypeHandler4 handler = yapClassPrimitive.i_handler;
            handler.writeNew(_family, obj, topLevel, writer, withIndirection, restoreLinkOffset);
        }
        return 0;
    }
    
    public Date readDate(Buffer bytes){
		return new Date(LongHandler.readLong(bytes));
	}
	

}
