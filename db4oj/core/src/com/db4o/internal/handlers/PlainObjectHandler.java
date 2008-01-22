package com.db4o.internal.handlers;

import com.db4o.ext.Db4oIOException;
import com.db4o.foundation.NotImplementedException;
import com.db4o.foundation.PreparedComparison;
import com.db4o.internal.*;
import com.db4o.marshall.*;


public class PlainObjectHandler implements TypeHandler4{

    public void defragment(DefragmentContext context) {
        throw new NotImplementedException();
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        throw new NotImplementedException();
    }

    public Object read(ReadContext context) {
        int id = context.readInt();
        Transaction transaction = context.transaction();
        Object obj = transaction.objectForIdFromCache(id);
        if(obj != null){
            return obj;
        }
        obj = new Object();
        addReference(context, obj, id);
        return obj;
    }

    public void write(WriteContext context, Object obj) {
        Transaction transaction = context.transaction();
        ObjectContainerBase container = transaction.container();
        int id = container.getID(transaction, obj);
        if(id <= 0){
            id = container.newUserObject();
            
            // TODO: Free on rollback
            
            addReference(context, obj, id);
        }
        context.writeInt(id);
    }

    private void addReference(Context context, Object obj, int id) {
        Transaction transaction = context.transaction();
        ObjectReference ref = new ObjectReference(id);
        ref.setObjectWeak(transaction.container(), obj);
        transaction.addNewReference(ref);
    }

    public PreparedComparison prepareComparison(Object obj) {
        throw new NotImplementedException();
    }

}
