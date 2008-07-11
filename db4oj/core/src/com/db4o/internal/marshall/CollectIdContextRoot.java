/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class CollectIdContextRoot extends CollectIdContext{
    
    private TreeInt _ids;
    
    public CollectIdContextRoot(Transaction transaction, ObjectHeader oh, ReadBuffer buffer, String fieldName) {
        super(transaction, oh, buffer, fieldName);
    }

    public void addIdToTree(int id) {
        _ids = (TreeInt) Tree.add(_ids, new TreeInt(id));
    }
    
    public Tree ids() {
        return _ids;
    }

}
