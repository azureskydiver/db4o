/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.internal.*;


public abstract class PrimitiveMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract boolean useNormalClassRead();
    
    public abstract Date readDate(BufferImpl bytes);
    
    public abstract Object readShort(BufferImpl buffer);
    
    public abstract Object readInteger(BufferImpl buffer);
    
    public abstract Object readFloat(BufferImpl buffer);
    
    public abstract Object readDouble(BufferImpl buffer);
    
    public abstract Object readLong(BufferImpl buffer);
    
}
