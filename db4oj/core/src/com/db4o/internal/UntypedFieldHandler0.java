/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class UntypedFieldHandler0 extends UntypedFieldHandler {

    public UntypedFieldHandler0(ObjectContainerBase container) {
        super(container);
    }
    
    public Object read(ReadContext context) {
        return context.readObject();
    }
    
    public ObjectID readObjectID(InternalReadContext context){
        int id = context.readInt();
        return id == 0 ? ObjectID.IS_NULL : new ObjectID(id);
    }
    
    public void defragment(DefragmentContext context) {
    	throw new NotImplementedException();
    }

}
