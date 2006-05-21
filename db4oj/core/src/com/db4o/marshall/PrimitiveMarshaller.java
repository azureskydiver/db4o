/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public abstract class PrimitiveMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract int marshall(Transaction trans, YapClassPrimitive yapClassPrimitive, Object obj, YapWriter parentWriter);
    
    protected int objectLength(TypeHandler4 handler, Object obj){
        return handler.linkLength() + YapConst.OBJECT_LENGTH + YapConst.YAPID_LENGTH;
    }

}
