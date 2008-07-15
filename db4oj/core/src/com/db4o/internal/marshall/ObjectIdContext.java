/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class ObjectIdContext extends ObjectHeaderContext {
    
    private final int _id;

    public ObjectIdContext(Transaction transaction, ReadBuffer buffer, ObjectHeader objectHeader, int id) {
        super(transaction, buffer, objectHeader);
        _id = id;
    }
    
    public int id(){
        return _id;
    }
    

}
