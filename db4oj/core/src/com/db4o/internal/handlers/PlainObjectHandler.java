package com.db4o.internal.handlers;

import com.db4o.ext.Db4oIOException;
import com.db4o.foundation.NotImplementedException;
import com.db4o.foundation.PreparedComparison;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


public class PlainObjectHandler implements TypeHandler4, ReadsObjectIds, EmbeddedTypeHandler{

    public void defragment(DefragmentContext context) {
        context.copySlotlessID();
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        // do nothing
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

    public PreparedComparison prepareComparison(Context context, Object obj) {
        throw new NotImplementedException();
    }

    public ObjectID readObjectID(InternalReadContext context) {
        return ObjectID.read(context);
    }

}
