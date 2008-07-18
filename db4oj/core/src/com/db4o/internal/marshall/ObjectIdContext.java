/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.marshall.*;


/**
 * @exclude
 */
public interface ObjectIdContext extends HandlerVersionContext, MarshallingInfo, ReadContext {
    
    public int id();

}
