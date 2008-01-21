package com.db4o.internal.handlers;

import sun.security.action.*;

import com.db4o.ext.Db4oIOException;
import com.db4o.foundation.NotImplementedException;
import com.db4o.foundation.PreparedComparison;
import com.db4o.internal.*;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;


public class PlainObjectHandler implements TypeHandler4{

    public void defragment(DefragmentContext context) {
        throw new NotImplementedException();
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        throw new NotImplementedException();
    }

    public Object read(ReadContext context) {
        throw new NotImplementedException();
    }

    public void write(WriteContext context, Object obj) {
        Transaction transaction = context.transaction();
        ObjectContainerBase container = transaction.container();
        int id = container.getID(transaction, obj);
        if(id > 0){
            return;
        }
        id = container.newUserObject();
        
        throw new NotImplementedException();
    }

    public PreparedComparison prepareComparison(Object obj) {
        throw new NotImplementedException();
    }

}
