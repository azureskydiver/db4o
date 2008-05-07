/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.marshall.*;


/**
 * @exclude
 */
public interface FirstClassHandler extends CascadingTypeHandler {
    
    void readCandidates(QueryingReadContext context) throws Db4oIOException;
    
    TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, ByteArrayBuffer[] a_bytes);

}
