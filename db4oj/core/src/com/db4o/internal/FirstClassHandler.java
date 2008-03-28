/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;


/**
 * @exclude
 */
public interface FirstClassHandler extends CascadingTypeHandler {
    
    void readCandidates(int handlerVersion, ByteArrayBuffer buffer, QCandidates candidates) throws Db4oIOException;
    
    TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, ByteArrayBuffer[] a_bytes);

}
