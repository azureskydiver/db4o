/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.marshall.*;


/**
 * @exclude
 */
public interface PossibleArrayHandlerProvider {
    
    TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes);

}
