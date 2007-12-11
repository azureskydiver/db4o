/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

/**
 * @exclude
 */
public abstract class ArrayMarshaller {
    
    public MarshallerFamily _family;
    
    public final TreeInt collectIDs(ArrayHandler arrayHandler, TreeInt tree, StatefulBuffer reader) throws Db4oIOException{
        Transaction trans = reader.getTransaction();
        return arrayHandler.collectIDs1(trans, tree, prepareIDReader(trans,reader));
    }

    public abstract void defragIDs(ArrayHandler arrayHandler,DefragmentContext context);
    
    protected abstract BufferImpl prepareIDReader(Transaction trans,BufferImpl reader) throws Db4oIOException;
}
