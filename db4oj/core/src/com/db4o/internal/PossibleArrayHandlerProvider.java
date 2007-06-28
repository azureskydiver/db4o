/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;


/**
 * @exclude
 */
public interface PossibleArrayHandlerProvider {
    
    void readCandidates(MarshallerFamily mf, Buffer buffer, QCandidates candidates) throws Db4oIOException;
    
    TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes);

}
