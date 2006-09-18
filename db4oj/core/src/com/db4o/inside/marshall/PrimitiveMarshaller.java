/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


public abstract class PrimitiveMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract boolean useNormalClassRead();
    
    public abstract int writeNew(Transaction trans, YapClassPrimitive yapClassPrimitive, Object obj, boolean topLevel, YapWriter parentWriter, boolean withIndirection, boolean restoreLinkOffset);
    
    protected final int objectLength(TypeHandler4 handler){
        return handler.linkLength() + YapConst.OBJECT_LENGTH + YapConst.ID_LENGTH;
    }

}
