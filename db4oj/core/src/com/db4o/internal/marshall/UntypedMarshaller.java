/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class UntypedMarshaller {
    
    MarshallerFamily _family;
    
    public abstract TypeHandler4 readArrayHandler(Transaction a_trans, BufferImpl[] a_bytes);

    public abstract boolean useNormalClassRead();
    
}
