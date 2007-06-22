/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.internal.marshall.*;


/**
 * @exclude
 */
public interface IndexableTypeHandler extends Indexable4, TypeHandler4{
    
    Object indexEntryToObject(Transaction trans, Object indexEntry);
    
    Object readIndexEntry(MarshallerFamily mf, StatefulBuffer writer) throws CorruptionException, Db4oIOException;

}
