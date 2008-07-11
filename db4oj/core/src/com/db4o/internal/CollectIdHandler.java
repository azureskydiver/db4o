/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.marshall.*;


/**
 * allows collection of IDs if a query is executed on 
 * a field node 
 */
public interface CollectIdHandler extends TypeHandler4{
    
    public void oldCollectIDs(CollectIdContext context);

}
