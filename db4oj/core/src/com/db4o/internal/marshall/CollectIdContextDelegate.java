/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class CollectIdContextDelegate extends CollectIdContext{
    
    public CollectIdContext _parentContext;
    
    public CollectIdContextDelegate(CollectIdContext context, ObjectHeader header, ReadBuffer buffer) {
        super(context.transaction(), header, buffer);
        _parentContext = context;
    }
    
    public void addId(int id) {
        _parentContext.addId(id);
    }

    public Tree ids() {
        return _parentContext.ids();
    }

}
