package com.db4o.internal.handlers;

import com.db4o.ext.Db4oIOException;
import com.db4o.foundation.PreparedComparison;
import com.db4o.internal.DefragmentContext;
import com.db4o.internal.DeleteContext;
import com.db4o.internal.TypeHandler4;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;


public class PlainObjectHandler implements TypeHandler4{

    public void defragment(DefragmentContext context) {
        // TODO Auto-generated method stub
        
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        // TODO Auto-generated method stub
        
    }

    public Object read(ReadContext context) {
        // TODO Auto-generated method stub
        return null;
    }

    public void write(WriteContext context, Object obj) {
        
        // TODO Auto-generated method stub
    }

    public PreparedComparison prepareComparison(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

}
