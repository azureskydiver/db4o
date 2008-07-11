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
        super(context.transaction(), header, buffer, context.fieldName());
        _parentContext = context;
    }
    
    public void addIdToTree(int id) {
        _parentContext.addIdToTree(id);
    }

    public Tree ids() {
        return _parentContext.ids();
    }

}
