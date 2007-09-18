/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class UntypedMarshaller {
    
    MarshallerFamily _family;
    
    public abstract void deleteEmbedded(StatefulBuffer reader) throws Db4oIOException;
    
    public abstract Object read(StatefulBuffer reader) throws CorruptionException, Db4oIOException;
    
    public abstract TypeHandler4 readArrayHandler(Transaction a_trans, Buffer[] a_bytes);

    public abstract boolean useNormalClassRead();
    
    public abstract Object readQuery(Transaction trans, Buffer reader, boolean toArray) throws CorruptionException, Db4oIOException;

	public abstract void defrag(BufferPair readers);
}
