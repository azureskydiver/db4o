/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;


/**
 * @exclude
 */
public interface FirstClassHandler {
    
    void cascadeActivation(ActivationContext4 context);
    
    TypeHandler4 readCandidateHandler(QueryingReadContext context);
    
    void readCandidates(QueryingReadContext context) throws Db4oIOException;
    
    public void collectIDs(QueryingReadContext context);

}
