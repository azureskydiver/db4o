/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import java.util.Date;

import com.db4o.*;
import com.db4o.inside.*;


public abstract class PrimitiveMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract boolean useNormalClassRead();
    
    public abstract int writeNew(Transaction trans, YapClassPrimitive yapClassPrimitive, Object obj, boolean topLevel, StatefulBuffer parentWriter, boolean withIndirection, boolean restoreLinkOffset);
    
    public abstract Date readDate(Buffer bytes);
    
    protected final int objectLength(TypeHandler4 handler){
        return handler.linkLength() + YapConst.OBJECT_LENGTH + YapConst.ID_LENGTH;
    }

}
