/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.internal.*;


public abstract class PrimitiveMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract boolean useNormalClassRead();
    
    public abstract Date readDate(Buffer bytes);
    
    public abstract Object readShort(Buffer buffer);
    
    public abstract Object readInteger(Buffer buffer);
    
    public abstract Object readFloat(Buffer buffer);
    
    public abstract Object readDouble(Buffer buffer);
    
    public abstract Object readLong(Buffer buffer);
        
    protected final int objectLength(TypeHandler4 handler){
        return handler.linkLength() + Const4.OBJECT_LENGTH + Const4.ID_LENGTH;
    }
}
